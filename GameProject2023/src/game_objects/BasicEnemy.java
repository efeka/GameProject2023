package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import abstract_objects.Creature;
import abstract_objects.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class BasicEnemy extends Creature {

	private ObjectHandler objectHandler;
	
	private boolean attacking = false;
	private boolean startedAttacking = false;
	private int attackCooldown = 2000;
	private long lastAttackTimer = attackCooldown;

	private int invulnerableDuration = 700;
	private long lastInvulnerableTimer = invulnerableDuration; 
	
	private float runningSpeed = 3f;
	private float jumpingSpeed = -9f;
	
	private Animation[] idleAnimation;
	private Animation[] runAnimation;
	private Animation[] attackAnimation;
	private BufferedImage[] jumpingSprites;
	
	public BasicEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 25, 100, 70, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BasicEnemy));
		
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
		handleCollision();
		
		runAnimations();
	}

	@Override
	public void render(Graphics g) {
		drawAnimations(g);
	}
	
	private void handleCollision() {
		for (GameObject other : objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER)) {
			// Collision with Blocks
			if (other.getObjectId().getCategory() == Category.Block)
				checkBlockCollision(other);
		}
	}

	private void checkBlockCollision(GameObject other) {
		Rectangle otherBounds = other.getBounds();

		// Bottom collision
		if (getBottomBounds().intersects(otherBounds)) {
			y = other.getY() - height;
			velY = 0;
			falling = false;
			jumping = false;
			
			// Reset knock back after hitting the ground
			if (knockedBack) {
				knockedBack = false;
				velX = 0;
			}
		}
		else
			falling = true;

		// Horizontal collision
		if (getHorizontalBounds().intersects(otherBounds)) {
			int xDiff = (int) (x - other.getX());
			// Player is to the left of the object
			if (xDiff < 0)
				x = other.getX() - width;
			// Player is to the right of the object
			else
				x = other.getX() + other.getWidth();
		}

		// Top collision
		if (getTopBounds().intersects(otherBounds)) {
			y = other.getY() + other.getHeight();
			velY = 0;
		}
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
				player.takeDamage(damage);
				player.applyKnockback(4 * direction, -5);
			}
		}
	}

	@Override
	public void takeDamage(int damageAmount) {
		if (invulnerable)
			return;
		
		lastInvulnerableTimer = System.currentTimeMillis();
		invulnerable = true;
		
		setHealth(health - damageAmount);
		objectHandler.addObject(new DamageNumberPopup(x + width / 2, y, damageAmount, objectHandler), ObjectHandler.MENU_LAYER);
		
		if (health <= 0)
			objectHandler.removeObject(this);
	}
	
	@Override
	public void applyKnockback(float velX, float velY) {
		knockedBack = true;
		this.velX = velX;
		this.velY = velY;
	}
	
	private Rectangle getHorizontalBounds() {
		float height = 3 * this.height / 5f;
		float yOffset = this.height / 5f; 
		return new Rectangle((int) (x + velX), (int) (y + yOffset), width, (int) height);
	}

	private Rectangle getTopBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		return new Rectangle((int) (x + xOffset), (int) y, (int) width, (int) height);
	}

	private Rectangle getBottomBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		float yOffset = 4 * this.height / 5f;
		return new Rectangle((int) (x + xOffset), (int) (y + yOffset), (int) width, (int) height);
	}
	
	private Rectangle getGroundAttackBounds() {
		int attackX;
		if (direction == 1)
			attackX = (int) x + width / 2;
		else
			attackX = (int) x - width / 2;
		return new Rectangle(attackX, (int) y, width, height);
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
