package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstract_objects.Creature;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class BasicEnemy extends Creature {

	private ObjectHandler objectHandler;
	
	private boolean attacking = false;
	private boolean startedAttacking = false;
	private int attackCooldown = 2000;
	private long lastAttackTimer = attackCooldown;

//	private float runningSpeed = 3f;
//	private float jumpingSpeed = -9f;
	
	private Animation[] idleAnimation;
	private Animation[] runAnimation;
	private Animation[] attackAnimation;
	private BufferedImage[] jumpingSprites;
	
	public BasicEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 25, 100, 70, objectHandler, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BasicEnemy));		
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
		
		//if (!knockedBack)
		//handleMovement();
		handleAttacking();
		basicBlockCollision(objectHandler);
		
		runAnimations();
	}

	@Override
	public void render(Graphics g) {
		drawAnimations(g);
	}
	
	private void handleAttacking() {
		if (startedAttacking && !attacking) { 
			if ((attackAnimation[0].getCurrentFrame() >= 4 && attackAnimation[0].getCurrentFrame() < 7) ||
					attackAnimation[1].getCurrentFrame() >= 4 && attackAnimation[1].getCurrentFrame() < 7)
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
		
		Player player = objectHandler.getPlayer();
		if (player.getBounds().intersects(getGroundAttackBounds())) {
			if (System.currentTimeMillis() - lastAttackTimer >= attackCooldown) {
				startedAttacking = true;
				lastAttackTimer = System.currentTimeMillis();
			}
			
			// Damage the player
			if (attacking) {
				player.takeDamage(damage, true);
				player.applyKnockback(4 * direction, -5);
			}
		}
	}

	@Override
	public void takeDamage(int damageAmount, boolean activateInvulnerability) {
		if (invulnerable)
			return;
		
		if (activateInvulnerability) {
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
	
	private void setupAnimations() {
		idleAnimation = new Animation[2];
		runAnimation = new Animation[2];
		attackAnimation = new Animation[2];

		TextureLoader textureLoader = TextureLoader.getInstance();
		jumpingSprites = textureLoader.getTextures(TextureName.BasicEnemyJump);
		
		final int idleDelay = 8;
		final int runDelay = 8;
		final int attackDelay = 10;
		
		idleAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyIdle, 1),
				idleDelay, false);
		idleAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyIdle, -1),
				idleDelay, false);
		runAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyRun, 1),
				runDelay, false);
		runAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyRun, -1),
				runDelay, false);
		attackAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyAttack, 1),
				attackDelay, true);
		attackAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.BasicEnemyAttack, -1),
				attackDelay, true);
	}
	
	private void runAnimations() {
		int directionToIndex = getIndexFromDirection();
		
		// Attacking
		if (startedAttacking)
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
		
		// Attacking
		if (startedAttacking)
			attackAnimation[directionToIndex].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
		// Jumping
		else if (jumping) {
			// Going up
			if (velY <= 0)
				g.drawImage(jumpingSprites[directionToIndex * 2], (int) x - width / 2, (int) y - height / 2, width * 2, height * 2, null);
			// Going down
			else if (velY > 0)
				g.drawImage(jumpingSprites[directionToIndex * 2 + 1], (int) x - width / 2, (int) y - height / 2, width * 2, height * 2, null);
		}
		// Idle
		else if (velX == 0)
			idleAnimation[directionToIndex].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
		// Running
		else
			runAnimation[directionToIndex].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
	}
	
	public int getIndexFromDirection() {
		return (-direction + 1) / 2;
	}

}
