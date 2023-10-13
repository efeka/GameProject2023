package game_objects;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import abstracts.Creature;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class ToadEnemy extends Creature {

	private ObjectHandler objectHandler;

	// After death, gradually fades out the enemy before removing it from the game
	private float alpha = 1;
	private float fadingRate = 0.005f;

	// If this enemy recently took damage and is in the hurt animation
	private boolean tookDamage = false;

	private boolean attacking = false;
	private boolean startedAttacking = false;
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

	private Animation[] idleAnimation;
	private Animation[] runAnimation;
	private Animation[] attackAnimation;
	private Animation[] hurtAnimation;
	private Animation[] deathAnimation;

	public ToadEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, (int) (TILE_SIZE * 1.5f), (int) (TILE_SIZE * 1.5f), 25, 100, objectHandler, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BasicEnemy));		
		this.objectHandler = objectHandler;

		texture = TextureLoader.getInstance().getTextures(TextureName.BasicEnemyIdle)[0];
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

		if (tookDamage && (hurtAnimation[0].isPlayedOnce() || hurtAnimation[1].isPlayedOnce()))
			tookDamage = false;

		if (dead) {
			if (alpha > fadingRate) 
				alpha -= fadingRate;
			else
				objectHandler.removeObject(this);
		}
		else {
			handleAttacking(targetInVision);
			if (!knockedBack && !dead && !startedAttacking && !attacking)
				handleMovement();
		}
		
		basicBlockCollision();

		runAnimations();
	}

	@Override
	public void render(Graphics g) {
		drawAnimations(g);
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
		if (startedAttacking && !attacking) { 
			int currentFrame = Math.max(attackAnimation[0].getCurrentFrame(), attackAnimation[1].getCurrentFrame());
			attacking = currentFrame == 3 || currentFrame == 4;
		}
		if (knockedBack || (attacking && (attackAnimation[0].isPlayedOnce() || attackAnimation[1].isPlayedOnce()))) {
			attacking = false;
			startedAttacking = false;

			attackAnimation[0].resetAnimation();
			attackAnimation[1].resetAnimation();
		}

		// Try to attack the player or their summons
		ArrayList<Creature> summonsList = objectHandler.getSummonsList();
		for (int i = summonsList.size() - 1; i >= 0; i--) {
			Creature otherCreature = summonsList.get(i);

			if (getGroundAttackBounds().intersects(otherCreature.getBounds())) {
				if (System.currentTimeMillis() - lastAttackTimer >= attackCooldown) {
					velX = 0;
					startedAttacking = true;
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
			hurtAnimation[0].resetAnimation();
			hurtAnimation[1].resetAnimation();
		}

		setHealth(health - damageAmount);
		objectHandler.addObject(new DamageNumberPopup(x + width / 3, y - height / 5, damageAmount, objectHandler),
				ObjectHandler.MENU_LAYER);

		if (health <= 0 && !dead) {
			die(false);
			startedAttacking = attacking = false;
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
		idleAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyIdle, 1),
						idleDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyIdle, -1),
						idleDelay, false),
		};

		final int runDelay = 15;
		runAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyRun, 1),
						runDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyRun, -1),
						runDelay, false),
		};

		final int attackDelay = 10;
		attackAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyAttack, 1),
						attackDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyAttack, -1),
						attackDelay, true),
		};

		final int hurtDelay = 10;
		hurtAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyHurt, 1),
						hurtDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyHurt, -1),
						hurtDelay, true),
		};

		final int deathDelay = 10;
		deathAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyDeath, 1),
						deathDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyDeath, -1),
						deathDelay, true),	
		};
	}

	private void runAnimations() {
		int directionToIndex = getIndexFromDirection();

		// Dead
		if (dead)
			deathAnimation[directionToIndex].runAnimation();
		// Taken damage
		else if (tookDamage)
			hurtAnimation[directionToIndex].runAnimation();
		// Attacking
		else if (startedAttacking)
			attackAnimation[directionToIndex].runAnimation();
		// Idle
		else if (velX == 0)
			idleAnimation[directionToIndex].runAnimation();
		// Running
		else if (velX != 0)
			runAnimation[directionToIndex].runAnimation();
	}

	private void drawAnimations(Graphics g) {
		int directionToIndex = getIndexFromDirection();

		final int x = (int) this.x - width / 2;
		final int y = (int) this.y - height / 2;
		final int width = this.width * 2;
		final int height = this.height * 2;

		// Dead
		if (dead) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(makeTransparent(alpha));
			deathAnimation[directionToIndex].drawAnimation(g, x, y, width, height);
			g2d.setComposite(makeTransparent(1));
		}
		// Taken damage
		else if (tookDamage)
			hurtAnimation[directionToIndex].drawAnimation(g, x, y, width, height);
		// Attacking
		else if (startedAttacking)
			attackAnimation[directionToIndex].drawAnimation(g, x, y, width, height);
		// Idle
		else if (velX == 0)
			idleAnimation[directionToIndex].drawAnimation(g, x, y, width, height);
		// Running
		else
			runAnimation[directionToIndex].drawAnimation(g, x, y, width, height);
	}

	public int getIndexFromDirection() {
		return (-direction + 1) / 2;
	}

	private AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}

}
