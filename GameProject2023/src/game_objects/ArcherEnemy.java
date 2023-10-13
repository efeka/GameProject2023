package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import abstracts.Creature;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class ArcherEnemy extends Creature {

	private ObjectHandler objectHandler;

	private final int shootCooldown = 2000;
	private long lastShotTimer = shootCooldown;
	private boolean isShotReady = false, didShoot = false;

	private float shootVelX, shootVelY;

	private Animation[] idleAnimation;
	private Animation[] shootAnimationTorso, shootAnimationLegs;

	public ArcherEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 25, 100, objectHandler, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BasicEnemy));		
		this.objectHandler = objectHandler;

		setupAnimations();
		texture = TextureLoader.getInstance().getTextures(TextureName.ArcherEnemyIdle)[0];
	}

	@Override
	public void tick() {
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
			int animationFrame1 = shootAnimationTorso[0].getCurrentFrame();
			int animationFrame2 = shootAnimationTorso[1].getCurrentFrame();
			if (!didShoot && (animationFrame1 == 7 || animationFrame2 == 7)) {
				shootArrow(8f);
				didShoot = true;
			}

			if (shootAnimationTorso[0].isPlayedOnce() || shootAnimationTorso[1].isPlayedOnce())
				resetShootingSystem();
		}

		basicBlockCollision();
		runAnimations();
	}


	@Override
	public void render(Graphics g) {
		drawAnimations(g);
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
		int animationIndex = getIndexFromDirection();
		if (isShotReady) {
			shootAnimationLegs[animationIndex].runAnimation();
			shootAnimationTorso[animationIndex].runAnimation();
		}
		else
			idleAnimation[animationIndex].runAnimation();
	}

	private void drawAnimations(Graphics g) {
		int animationIndex = getIndexFromDirection();

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
			shootAnimationLegs[animationIndex].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);

			// Rotate the torso
			Graphics2D g2d = (Graphics2D) g;
			g2d.rotate(rotationAngle, centerX, centerY);
			shootAnimationTorso[animationIndex].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			g2d.rotate(-rotationAngle, centerX, centerY);
		}
		else
			idleAnimation[animationIndex].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
	}

	private void setupAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();

		int idleDelay = 20;
		idleAnimation = new Animation[2];
		idleAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyIdle, 1),
				idleDelay, false);
		idleAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyIdle, -1),
				idleDelay, false);

		int shootDelay = 6;
		shootAnimationTorso = new Animation[2];
		shootAnimationTorso[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyShootTorso, 1),
				shootDelay, true);
		shootAnimationTorso[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyShootTorso, -1),
				shootDelay, true);

		shootAnimationLegs = new Animation[2];
		shootAnimationLegs[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyShootLegs, 1),
				shootDelay, true);
		shootAnimationLegs[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.ArcherEnemyShootLegs, -1),
				shootDelay, true);
	}

	private void resetShootingSystem() {
		isShotReady = didShoot = false;
		lastShotTimer = System.currentTimeMillis();

		shootAnimationLegs[0].resetAnimation();
		shootAnimationLegs[1].resetAnimation();
		shootAnimationTorso[0].resetAnimation();
		shootAnimationTorso[1].resetAnimation();
	}

	public int getIndexFromDirection() {
		return (-direction + 1) / 2;
	}

}
