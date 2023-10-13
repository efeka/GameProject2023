package game_objects;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import abstracts.Creature;
import framework.Animation;
import framework.CreatureAnimationManager;
import framework.CreatureAnimationManager.AnimationType;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import ui.CreatureHealthBar;

public class ToadEnemy extends Creature {

	private ObjectHandler objectHandler;

	// After death, gradually fades out the enemy before removing it from the game
	private float alpha = 1;
	private float fadingRate = 0.005f;

	// If this enemy recently took damage and is in the hurt animation
	private boolean tookDamage = false;

	private boolean attacking = false;
	private boolean startedAttackAnim = false;
	private int attackCooldown = 2000;
	private long lastAttackTimer = attackCooldown;

	// This creature slowly patrols around by slowly walking in 
	// random directions while it does not have a target in its vision
	private Creature targetInVision = null;
	private int maxPatrolTime = 1500, patrolTimer;
	private long patrolStartTimer;

	private float walkingSpeed = 0.5f;
	
	private float runAcceleration = 0.01f;
	private float runningSpeed = 2f;

	private CreatureAnimationManager animationManager;
	
	public ToadEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, (int) (TILE_SIZE * 1.5f), (int) (TILE_SIZE * 1.5f), 25, 100, objectHandler, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BasicEnemy));		
		this.objectHandler = objectHandler;

		texture = TextureLoader.getInstance().getTextures(TextureName.BasicEnemyIdle)[0];
		animationManager = new CreatureAnimationManager();
		setupAnimations();
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
		
		calculateDirection();

		if (invulnerable && (System.currentTimeMillis() - lastInvulnerableTimer >= invulnerableDuration))
			invulnerable = false;

		if (tookDamage && animationManager.isAnimationPlayedOnce(AnimationType.Hurt))
			tookDamage = false;

		if (dead) {
			if (alpha > fadingRate) 
				alpha -= fadingRate;
			else
				objectHandler.removeObject(this);
		}
		else {
			handleAttacking(targetInVision);
			if (!knockedBack && !dead && !startedAttackAnim && !attacking)
				handleMovement();
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
	
	private void calculateDirection() {
		if (velX > 0)
			direction = 1;
		else if (velX < 0)
			direction = -1;
		
		if (knockedBack)
			direction *= -1;
	}

	private void handleMovement() {
		checkForTargetsInVision();

		// Chase and attack the target if it is in vision
		if (targetInVision != null) {
			patrolTimer = Integer.MIN_VALUE;

			if (getGroundAttackBounds().intersects(targetInVision.getBounds()))
				velX = 0;
			else {
				int xDiff = (int) (x - targetInVision.getX());
				if (xDiff > 0)
					velX += -runAcceleration;
				else if (xDiff < 0)
					velX += runAcceleration;
				
				if (Math.abs(velX) > runningSpeed)
					velX = runningSpeed * direction;
			}
		}
		// If there is no nearby target, patrol a small area
		else {
			if (patrolTimer == Integer.MIN_VALUE) {
				patrolTimer = (int) (Math.random() * maxPatrolTime);
				patrolStartTimer = System.currentTimeMillis();

				// Select a random direction to walk towards
				// Staying put is more probable than walking
				int randomDirection = (int) (Math.random() * 100);
				if (randomDirection >= 0 && randomDirection < 60)
					velX = 0;
				else if (randomDirection >= 60 && randomDirection < 80)
					velX = walkingSpeed;
				else
					velX = -walkingSpeed;
			}
			if (System.currentTimeMillis() - patrolStartTimer > patrolTimer)
				patrolTimer = Integer.MIN_VALUE;
		}
	}

	private void handleAttacking(Creature target) {
		if (startedAttackAnim) {
			int currentFrame = animationManager.getCurrentAnimationFrame(AnimationType.Attack);
			attacking = (currentFrame == 3 || currentFrame == 4);
		}
		if (animationManager.isAnimationPlayedOnce(AnimationType.Attack))
			startedAttackAnim = attacking = false;
		
		// Try to attack the player or their summons
		ArrayList<Creature> summonsList = objectHandler.getSummonsList();
		for (int i = summonsList.size() - 1; i >= 0; i--) {
			Creature otherCreature = summonsList.get(i);

			if (getGroundAttackBounds().intersects(otherCreature.getBounds())) {
				if (System.currentTimeMillis() - lastAttackTimer >= attackCooldown) {
					velX = 0;
					animationManager.resetAnimation(AnimationType.Attack);
					startedAttackAnim = true;
					lastAttackTimer = System.currentTimeMillis();
				}

				// Damage the player
				if (attacking) {
					otherCreature.applyKnockback(4 * direction, -5);
					otherCreature.takeDamage(damage, DEFAULT_INVULNERABILITY_DURATION);
				}
			}
		}
	}

	private void checkForTargetsInVision() {
		Player player = objectHandler.getPlayer();
		if (player != null && getVisionBounds().intersects(player.getBounds()))
			targetInVision = player;
		else
			targetInVision = null;

		if (targetInVision == null) {
			List<Creature> summonList = objectHandler.getSummonsList();
			Creature closestTarget = null;
			for (int i = summonList.size() - 1; i >= 0; i--) {
				Creature target = summonList.get(i);
				if (getVisionBounds().intersects(target.getBounds())) {
					if (closestTarget == null)
						closestTarget = target;
					else if (Math.abs(x - closestTarget.getX()) > Math.abs(x - target.getX()))
						closestTarget = target;
					break;
				}
			}
			targetInVision = closestTarget;
		}
	}

	@Override
	public void takeDamage(int damageAmount, int invulnerabilityDuration) {
		if (invulnerable)
			return;
		invulnerableDuration = invulnerabilityDuration; 

		if (invulnerableDuration != 0) {
			lastInvulnerableTimer = System.currentTimeMillis();
			invulnerable = true;

			tookDamage = true;
			animationManager.resetAnimation(AnimationType.Hurt);
		}

		setHealth(health - damageAmount);
		objectHandler.addObject(new DamageNumberPopup(x + width / 3, y - height / 5, damageAmount, objectHandler),
				ObjectHandler.MENU_LAYER);

		if (health <= 0 && !dead) {
			die(false);
			startedAttackAnim = attacking = false;
		}
	}

	@Override
	public void applyKnockback(float velX, float velY) {
		if (invulnerable)
			return;

		knockedBack = true;
		this.velX = velX;
		this.velY = velY;
	}

	// Vision range is longer on the front, but the enemy can still 
	// see some distance behind itself
	private Rectangle getVisionBounds() {
		int visionWidth = TILE_SIZE * 20;
		int visionHeight = TILE_SIZE * 4;
		float visionFrontalRatio = 0.8f;
		int visionY = (int) getBounds().getCenterY() - visionHeight / 2;
		int visionX;
		if (direction == 1)
			 visionX = (int) (x + width / 2 - (visionWidth * (1 - visionFrontalRatio)));
		else
			visionX = (int) (x + width / 2 - (visionWidth * (visionFrontalRatio)));
		
		return new Rectangle(visionX, visionY, visionWidth, visionHeight);
	}

	private void setupAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();

		final int idleDelay = 20;
		Animation[] idleAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyIdle, 1),
						idleDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyIdle, -1),
						idleDelay, false),
		};
		animationManager.addAnimation(AnimationType.Idle, idleAnimation);

		final int runDelay = 15;
		Animation[] runAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyRun, 1),
						runDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyRun, -1),
						runDelay, false),
		};
		animationManager.addAnimation(AnimationType.Run, runAnimation);
		
		final int attackDelay = 10;
		Animation[] attackAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyAttack, 1),
						attackDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyAttack, -1),
						attackDelay, true),
		};
		animationManager.addAnimation(AnimationType.Attack, attackAnimation);
		
		final int hurtDelay = 10;
		Animation[] hurtAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyHurt, 1),
						hurtDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyHurt, -1),
						hurtDelay, true),
		};
		animationManager.addAnimation(AnimationType.Hurt, hurtAnimation);

		final int deathDelay = 10;
		Animation[] deathAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyDeath, 1),
						deathDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyDeath, -1),
						deathDelay, true),	
		};
		animationManager.addAnimation(AnimationType.Death, deathAnimation);
	}

	private void runAnimations() {
		// Dead
		if (dead)
			animationManager.runAnimation(AnimationType.Death);
		// Taken damage
		else if (tookDamage)
			animationManager.runAnimation(AnimationType.Hurt);
		// Attacking
		else if (startedAttackAnim)
			animationManager.runAnimation(AnimationType.Attack);
		// Idle
		else if (velX == 0)
			animationManager.runAnimation(AnimationType.Idle);
		// Running
		else if (velX != 0)
			animationManager.runAnimation(AnimationType.Run);
	}

	private void drawAnimations(Graphics g) {
		final int x = (int) this.x - width / 2;
		final int y = (int) this.y - height / 2;
		final int width = this.width * 2;
		final int height = this.height * 2;

		// Dead
		if (dead) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(makeTransparent(alpha));
			animationManager.drawAnimation(AnimationType.Death, g, direction, x, y, width, height);
			g2d.setComposite(makeTransparent(1));
		}
		// Taken damage
		else if (tookDamage)
			animationManager.drawAnimation(AnimationType.Hurt, g, direction, x, y, width, height);
		// Attacking
		else if (startedAttackAnim)
			animationManager.drawAnimation(AnimationType.Attack, g, direction, x, y, width, height);
		// Idle
		else if (velX == 0)
			animationManager.drawAnimation(AnimationType.Idle, g, direction, x, y, width, height);
		// Running
		else
			animationManager.drawAnimation(AnimationType.Run, g, direction, x, y, width, height);
	}

	private AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	
	@Override
	public void setupHealthBar() {
		healthBar = new CreatureHealthBar(this, -GameConstants.ScaleConstants.TILE_SIZE / 2);
	}

}
