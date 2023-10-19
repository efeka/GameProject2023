package abstracts;

import java.awt.Rectangle;
import java.util.List;

import framework.Animation;
import framework.CreatureAnimationManager;
import framework.CreatureAnimationManager.AnimationType;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import ui.CreatureHealthBar;

public abstract class Creature extends GameObject {

	protected final float GRAVITY = GameConstants.PhysicsConstants.GRAVITY;
	protected final int TERMINAL_VELOCITY = GameConstants.PhysicsConstants.TERMINAL_VELOCITY;

	protected ObjectHandler objectHandler; 

	protected boolean falling, jumping, knockedBack, invulnerable, dead;
	// 1 for right, -1 for left
	protected int direction = 1;
	protected float velX, velY;

	protected int damage;
	protected int maxHealth;
	protected float health;

	public final int DEFAULT_INVULNERABILITY_DURATION = 700;
	protected int invulnerableDuration = 700;
	protected long lastInvulnerableTimer = 0;

	protected CreatureAnimationManager animationManager;
	protected CreatureHealthBar healthBar;
	protected Animation spawnAnimation;

	/**
	 * The creature class is a base for all objects that can move, attack and be killed.
	 * @param x				The x coordinate of the creature.
	 * @param y				The y coordinate of the creature.
	 * @param width			The width of the creature.
	 * @param height		The height of the creature.
	 * @param damage		The damage dealth by the creature.
	 * @param maxHealth		The maximum health of the creature.
	 * @param objectHandler Reference to the ObjectHandler.
	 * @param objectId		The object id for the creature.
	 */
	public Creature(int x, int y, int width, int height, int damage, int maxHealth, ObjectHandler objectHandler, ObjectId objectId) {
		super(x, y, width, height, objectId);
		this.objectHandler = objectHandler; 
		this.damage = damage;
		health = this.maxHealth = maxHealth;

		animationManager = new CreatureAnimationManager();
		spawnAnimation = new Animation(TextureLoader.getInstance().getTextures(TextureName.EnemySpawnEffect), 10, true);
		animationManager.addAnimation(AnimationType.Spawn, new Animation[] {spawnAnimation, spawnAnimation});
		
		falling = true;
		jumping = false;
		knockedBack = false;
		invulnerable = false;
		
		setupHealthBar();
	}

	/**
	 * Reduces this creature's health by the given damage amount.
	 * 
	 * @param damageAmount 			  The amount of health to be reduced on this creature.
	 * @param invulnerabilityDuration The duration for how long this creature stays invulnerable
	 * 								  after taking this damage.
	 */
	public abstract void takeDamage(int damageAmount, int invulnerabilityDuration);

	/**
	 * Applies force in the given direction to this creature.
	 * Important: Depending on the implementation, knock back may not happen due to invulnerability.
	 * Calling this before {@code takeDamage} solves this problem.
	 * 
	 * @param velX The x component of the force.
	 * @param velY The y component of the force.
	 */
	public abstract void applyKnockback(float velX, float velY);

	/**
	 * Initialize the CreatureHealthBar based on this object.
	 */
	public abstract void setupHealthBar();
	
	public void die() {
		dead = true;
		objectHandler.removeObject(this);
		dropCoins();
	}
	
	public void die(boolean removeObject) {
		dead = true;
		if (removeObject)
			objectHandler.removeObject(this);
		dropCoins();
	}
	
	private void dropCoins() {
		int coinsToDrop = (int) (Math.random() * 6) + 1;
		for (int i = 0; i < coinsToDrop; i++)
			objectHandler.addObject(objectHandler.createObjectByName(Name.Coin, (int) x, (int) y), ObjectHandler.MIDDLE_LAYER);
	}

	/**
	 * Handles collision with blocks in all 4 directions.
	 */
	protected void basicBlockCollision() {
		falling = true;

		List<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);

			// Collision with Blocks
			if (other.compareCategory(Category.Block)) {
				Rectangle otherBounds = other.getBounds();

				if (getGroundCheckBounds().intersects(otherBounds))
					falling = false;

				// Bottom collision
				if (getBottomBounds().intersects(otherBounds)) {
					y = other.getY() - height;
					velY = 0;
					jumping = false;

					// Reset knock back status after hitting the ground
					if (knockedBack) {
						knockedBack = false;
						velX = 0;
					}
				}

				// Horizontal collision
				if (getHorizontalBounds().intersects(otherBounds)) {
					int xDiff = (int) (x - other.getX());
					// Player is to the left of the object
					if (xDiff < 0)
						x = other.getX() - getHorizontalBounds().width;
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
			// Collision with Jump Through Blocks
			else if (other.compareCategory(Category.JumpThroughBlock) && getVelY() >= 0) {
				Rectangle otherBounds = other.getBounds();

				if (getGroundCheckBounds().intersects(otherBounds))
					falling = false;

				// Bottom collision
				if (getBottomBounds().intersects(otherBounds)) {
					y = other.getY() - height;
					velY = 0;
					jumping = false;

					// Reset knock back status after hitting the ground
					if (knockedBack) {
						knockedBack = false;
						velX = 0;
					}
				}
			}
		}
	}

	protected Rectangle getHorizontalBounds() {
		float height = 3 * this.height / 5f;
		float yOffset = this.height / 5f; 
		return new Rectangle((int) (x + velX), (int) (y + yOffset), width, (int) height);
	}

	protected Rectangle getTopBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		return new Rectangle((int) (x + xOffset), (int) y, (int) width, (int) height);
	}

	protected Rectangle getBottomBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		float yOffset = 4 * this.height / 5f;
		return new Rectangle((int) (x + xOffset), (int) (y + yOffset), (int) width, (int) height);
	}

	protected Rectangle getGroundAttackBounds() {
		int attackX;
		int attackWidth = 3 * width / 5;
		if (direction == 1)
			attackX = (int) (x + width / 2);
		else
			attackX = (int) (x + width / 2 - attackWidth);
		return new Rectangle(attackX, (int) y, attackWidth, height);
	}

	protected Rectangle getGroundCheckBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		float yOffset = this.height;
		return new Rectangle((int) (x + xOffset), (int) (y + yOffset), (int) width, (int) height);
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getHealth() {
		return (int) health;
	}

	public void setHealth(float health) {
		this.health = clamp(0f, health, maxHealth);
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public boolean isKnockedBack() {
		return knockedBack;
	}

	public void setKnockedBack(boolean knockedBack) {
		this.knockedBack = knockedBack;
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public float getVelX() {
		return velX;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}

	private float clamp(float min, float num, float max) {
		if (num < min)
			num = min;
		else if (num > max)
			num = max;
		return num;
	}

}
