package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import abstracts.Creature;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class BasicEnemy extends Creature {

	private ObjectHandler objectHandler;

	// After death, gradually fades out the enemy before removing 
	private float alpha = 1;
	private float fadingRate = 0.005f;

	// If this enemy recently took damage and is in the hurt animation
	private boolean tookDamage = false;

	private boolean attacking = false;
	private boolean startedAttacking = false;
	private int attackCooldown = 2000;
	private long lastAttackTimer = attackCooldown;

	//	private float runningSpeed = 3f;
	//	private float jumpingSpeed = -9f;

	private Animation[] idleAnimation;
	private Animation[] runAnimation;
	private Animation[] attackAnimation;
	private Animation[] hurtAnimation;
	private Animation[] deathAnimation;

	public BasicEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 25, 29, 70, objectHandler, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BasicEnemy));		
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

		if (invulnerable && (System.currentTimeMillis() - lastInvulnerableTimer >= invulnerableDuration))
			invulnerable = false;

		if (tookDamage && (hurtAnimation[0].isPlayedOnce() || hurtAnimation[1].isPlayedOnce()))
			tookDamage = false;

		//if (!knockedBack)
		//handleMovement();
		handleAttacking();
		basicBlockCollision();

		if (dead) {
			if (alpha > fadingRate) 
				alpha -= fadingRate;
			else
				objectHandler.removeObject(this);
		}

		runAnimations();
	}

	@Override
	public void render(Graphics g) {
		drawAnimations(g);
	}

	private void handleAttacking() {
		if (startedAttacking && !attacking) { 
			int currentFrame = Math.max(attackAnimation[0].getCurrentFrame(), attackAnimation[1].getCurrentFrame());
			if (currentFrame == 3 || currentFrame == 4)
				attacking = true;
			else
				attacking = false;
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

		if (health <= 0 && !dead)
			die(false);
	}

	@Override
	public void applyKnockback(float velX, float velY) {
		if (invulnerable)
			return;

		knockedBack = true;
		this.velX = velX;
		this.velY = velY;
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
