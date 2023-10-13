package abstracts;

import java.awt.Rectangle;
import java.util.ArrayList;

import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public abstract class Creature extends GameObject {

	protected final float GRAVITY = GameConstants.PhysicsConstants.GRAVITY;
	protected final int TERMINAL_VELOCITY = GameConstants.PhysicsConstants.TERMINAL_VELOCITY;

	protected ObjectHandler objectHandler; 

	protected boolean falling, jumping, knockedBack, invulnerable, dead;
	// 1 for right, -1 for left
	protected int direction = 1;
	protected float velX, velY;

	protected int damage;
	protected int maxHealth, maxStamina;
	protected float health, stamina;
	protected float staminaRegen;

	protected int invulnerableDuration = 700;
	protected long lastInvulnerableTimer = 0;

	public final int DEFAULT_INVULNERABILITY_DURATION = 700;

	public Creature(int x, int y, int width, int height, int damage, int maxHealth, ObjectHandler objectHandler, ObjectId objectId) {
		super(x, y, width, height, objectId);
		this.objectHandler = objectHandler; 
		this.damage = damage;
		health = this.maxHealth = maxHealth;

		falling = true;
		jumping = false;
		knockedBack = false;
		invulnerable = false;
	}

	public void regenerateStamina() {
		if (stamina < maxStamina)
			stamina += staminaRegen;
		if (stamina > maxStamina)
			stamina = maxStamina;
	}

	/**
	 * Reduces this creatures health by the given damage amount.
	 * @param damageAmount the amount of health to be reduced on this creature.
	 * @param invulnerabilityDuration duration of the invulnerability state after the damage is taken.
	 */
	public abstract void takeDamage(int damageAmount, int invulnerabilityDuration);

	/**
	 * Applies force in the given direction to this creature.
	 * Important: Depending on the implementation, it might be necessary
	 * to call this method BEFORE {@code takeDamage} because of invulerability.
	 * @param velX the x component of the force.
	 * @param velY the y component of the force.
	 */
	public abstract void applyKnockback(float velX, float velY);

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

	protected void basicBlockCollision() {
		falling = true;

		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);

			// Collision with Blocks
			if (other.getObjectId().getCategory() == Category.Block) {
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
			else if (other.getObjectId().getCategory() == Category.JumpThroughBlock && getVelY() >= 0) {
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

	public int getMaxStamina() {
		return maxStamina;
	}

	public void setMaxStamina(int maxStamina) {
		this.maxStamina = maxStamina;
	}

	public int getStamina() {
		return (int) stamina;
	}

	public void setStamina(float stamina) {
		this.stamina = clamp(0f, stamina, maxStamina);
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

	public float getStaminaRegen() {
		return staminaRegen;
	}

	public void setStaminaRegen(float staminaRegen) {
		this.staminaRegen = staminaRegen;
	}

	private float clamp(float min, float num, float max) {
		if (num < min)
			num = min;
		else if (num > max)
			num = max;
		return num;
	}

}
