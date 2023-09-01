package abstract_objects;

import java.awt.Rectangle;

import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;

public abstract class Creature extends GameObject {

	protected final float GRAVITY = GameConstants.CreatureConstants.GRAVITY;
	protected final int TERMINAL_VELOCITY = GameConstants.CreatureConstants.TERMINAL_VELOCITY;
	
	protected boolean falling, jumping, knockedBack, invulnerable;
	
	// 1 for right, -1 for left
	protected int direction = 1;
	protected float velX, velY;
	
	protected int damage;
	protected int maxHealth, maxStamina;
	protected float health, stamina;
	protected float staminaRegen;
	
	protected int invulnerableDuration = 500;
	protected long lastInvulnerableTimer = 0; 
	
	public Creature(int x, int y, int width, int height, int damage, int maxHealth, int maxStamina, ObjectId objectId) {
		super(x, y, width, height, objectId);
		
		health = this.maxHealth = maxHealth;
		stamina = this.maxStamina = maxStamina;
		staminaRegen = (int) (stamina / 200f);
		this.damage = damage;
		
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
	
	public abstract void takeDamage(int damageAmount);
	public abstract void applyKnockback(float velX, float velY);
	
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
		if (direction == 1)
			attackX = (int) x + width / 2;
		else
			attackX = (int) x - width / 2;
		return new Rectangle(attackX, (int) y, width, height);
	}
	
	protected void basicBlockCollision(ObjectHandler objectHandler) {
		for (GameObject other : objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER)) {
			// Collision with Blocks
			if (other.getObjectId().getCategory() == Category.Block) {
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
					// Creature is to the left of the object
					if (xDiff < 0)
						x = other.getX() - width;
					// Creature is to the right of the object
					else
						x = other.getX() + other.getWidth();
				}

				// Top collision
				if (getTopBounds().intersects(otherBounds)) {
					y = other.getY() + other.getHeight();
					velY = 0;
				}
			}
		}
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
