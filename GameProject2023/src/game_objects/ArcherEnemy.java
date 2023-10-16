package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import abstracts.Creature;
import framework.Animation;
import framework.CreatureAnimationManager.AnimationType;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import ui.CreatureHealthBar;

public class ArcherEnemy extends Creature {

	private ObjectHandler objectHandler;

	private final int shootCooldown = 2000;
	private long lastShotTimer = shootCooldown;
	private boolean isShotReady = false, didShoot = false;

	private float shootVelX, shootVelY;

	private BufferedImage[] legsShootingTextures;

	public ArcherEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 25, 100, objectHandler, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BasicEnemy));		
		this.objectHandler = objectHandler;

		setupAnimations();
		texture = TextureLoader.getInstance().getTextures(TextureName.ArcherEnemyIdle)[0];
	}

	@Override
	public void tick() {
		if (!animationManager.isAnimationPlayedOnce(AnimationType.Spawn)) {
			animationManager.runAnimation(AnimationType.Spawn);
			return;
		}

		x += velX;
		y += velY;

		if (falling || jumping) {
			velY += GRAVITY;

			if (velY > TERMINAL_VELOCITY)
				velY = TERMINAL_VELOCITY;
		}

		if (invulnerable && (System.currentTimeMillis() - lastInvulnerableTimer >= invulnerableDuration))
			invulnerable = false;

		takeAim(8f);

		if (System.currentTimeMillis() - lastShotTimer >= shootCooldown)
			isShotReady = true;
		if (isShotReady) {
			int animationFrame = animationManager.getCurrentAnimationFrame(AnimationType.Attack1);
			if (!didShoot && animationFrame == 7) {
				shootArrow(8f);
				didShoot = true;
			}

			if (animationManager.isAnimationPlayedOnce(AnimationType.Attack1))
				resetShootingSystem();
		}

		basicBlockCollision();
		runAnimations();
		healthBar.tick();
	}


	@Override
	public void render(Graphics g) {
		drawAnimations(g);
		healthBar.render(g);
	}

	@Override
	public void takeDamage(int damageAmount, int invulnerabilityDuration) {
		if (invulnerable)
			return;
		invulnerableDuration = invulnerabilityDuration; 

		if (invulnerableDuration != 0) {
			lastInvulnerableTimer = System.currentTimeMillis();
			invulnerable = true;
		}

		setHealth(health - damageAmount);
		objectHandler.addObject(new DamageNumberPopup(x + width / 3, y - height / 5, damageAmount, objectHandler), ObjectHandler.MENU_LAYER);

		if (health <= 0)
			die();
	}

	@Override
	public void applyKnockback(float velX, float velY) {
		knockedBack = true;
		this.velX = velX;
		this.velY = velY;
	}

	/**
	 * Aims at the center point of the player or one of their summons, depending on which one is closest.
	 * The purpose of this method is to set the shootVelX and shootVelY variables.
	 * They are then used for shooting the arrow in the correct angle,
	 * and rotating the animation using according to that angle.
	 * @param speedX the horizontal speed of the arrow
	 */
	private void takeAim(float speedX) {
		shootVelX = speedX;
		Creature target = getClosestTarget();
		float distanceX = (float) target.getBounds().getCenterX() - x;
		float distanceY = (float) target.getBounds().getCenterY() - y;

		if (distanceX < 0)
			shootVelX = Math.abs(shootVelX) * -1;

		if (shootVelX < 0)
			direction = -1;
		else
			direction = 1;

		float divByZeroCheck = 0.001f; 
		if (shootVelX < -divByZeroCheck || shootVelX > divByZeroCheck) {
			float timeToTargetX = distanceX / shootVelX;
			if (timeToTargetX < -divByZeroCheck || timeToTargetX > divByZeroCheck) {
				shootVelX = distanceX / timeToTargetX;
				shootVelY = (distanceY - 0.5f * GRAVITY * timeToTargetX * timeToTargetX) / timeToTargetX;
			}
		}
	}

	// Returns the closest possible target, which is either the player or one of their summons.
	private Creature getClosestTarget() {
		ArrayList<Creature> targetList = objectHandler.getSummonsList();
		Creature closest = objectHandler.getPlayer();
		int closestDistance = (int) (Math.abs(x - closest.getX()) + Math.abs(y - closest.getY()));
		for (Creature target : targetList) {
			int xDiff = (int) Math.abs(x - target.getX());
			int yDiff = (int) Math.abs(y - target.getY());
			if (xDiff + yDiff < closestDistance) {
				closest = target;
				closestDistance = xDiff + yDiff;
			}
		}
		return closest;
	}

	// Shoot the arrow aimed at the center of the Player
	private void shootArrow(float speedX) {
		takeAim(speedX);
		objectHandler.addObject(new ArrowProjectile(x, y, shootVelX, shootVelY, 15, objectHandler), ObjectHandler.MIDDLE_LAYER);
	}

	private void runAnimations() {
		if (isShotReady)
			animationManager.runAnimation(AnimationType.Attack1);
		else
			animationManager.runAnimation(AnimationType.Idle);
	}

	private void drawAnimations(Graphics g) {
		int directionIndex = getIndexFromDirection();

		final int x = (int) this.x - width / 2;
		final int y = (int) this.y - height / 2;
		final int width = this.width * 2;
		final int height = this.height * 2;
		
		if (animationManager.getCurrentAnimationFrame(AnimationType.Spawn) >= 13) {
			if (isShotReady) {
				float centerX = (float) getBounds().getCenterX();
				float centerY = (float) getBounds().getCenterY();
	
				// Calculate the angle that this enemy will be rotated at
				double rotationAngle;
				if (shootVelX < 0)
					rotationAngle = Math.atan2(shootVelY, shootVelX) + Math.PI;
				else
					rotationAngle = -Math.atan2(shootVelY, -shootVelX) + Math.PI * 3;
	
				// Draw the legs, but dont rotate them
				g.drawImage(legsShootingTextures[directionIndex], x, y, width, height, null);
	
				// Rotate the torso
				Graphics2D g2d = (Graphics2D) g;
				g2d.rotate(rotationAngle, centerX, centerY);
				animationManager.drawAnimation(AnimationType.Attack1, g, direction, x, y, width, height);
				g2d.rotate(-rotationAngle, centerX, centerY);
			}
			else
				animationManager.drawAnimation(AnimationType.Idle, g, direction, x, y, width, height);
		}
		
		if (!animationManager.isAnimationPlayedOnce(AnimationType.Spawn)) {
			int spawnWidth = (int) (width * 0.8f);
			int spawnHeight = (int) (height * 0.8f);
			int spawnX = (int) (x + (width - spawnWidth) / 2);
			int spawnY = (int) (y + (height - spawnHeight) / 2); 
			animationManager.drawAnimation(AnimationType.Spawn, g, 1, 
					spawnX, spawnY, spawnWidth, spawnHeight);
		}
	}

	private void setupAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();

		legsShootingTextures = new BufferedImage[] {
				textureLoader.getTexturesByDirection(TextureName.ArcherEnemyShootLegs, 1)[0],
				textureLoader.getTexturesByDirection(TextureName.ArcherEnemyShootLegs, -1)[0],
		};
		
		int idleDelay = 20;
		Animation[] idleAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyIdle, 1),
						idleDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyIdle, -1),
						idleDelay, false),
		};
		animationManager.addAnimation(AnimationType.Idle, idleAnimation);

		int shootDelay = 6;
		Animation[] shootAnimationTorso = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyShootTorso, 1),
						shootDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyShootTorso, -1),
						shootDelay, true),
		};
		animationManager.addAnimation(AnimationType.Attack1, shootAnimationTorso);
	}

	private void resetShootingSystem() {
		isShotReady = didShoot = false;
		lastShotTimer = System.currentTimeMillis();

		animationManager.resetAnimation(AnimationType.Attack1);
	}

	public int getIndexFromDirection() {
		return (-direction + 1) / 2;
	}

	@Override
	public void setupHealthBar() {
		healthBar = new CreatureHealthBar(this, -GameConstants.ScaleConstants.TILE_SIZE / 2);
	}

}
