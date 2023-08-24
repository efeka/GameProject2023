package abstract_objects;

import framework.GameConstants;
import framework.ObjectId;

public abstract class Creature extends GameObject {

	protected final float GRAVITY = GameConstants.CreatureConstants.GRAVITY;
	protected final int TERMINAL_VELOCITY = GameConstants.CreatureConstants.TERMINAL_VELOCITY;
	
	protected boolean falling, jumping, attacking, knockedBack, invulnerable;
	
	// 1 for right, -1 for left
	protected int direction = 1;
	protected float velX, velY;
	
	protected int damage;
	protected int maxHealth, maxStamina;
	protected float health, stamina;
	protected float staminaRegen;
	
	public Creature(int x, int y, int width, int height, int damage, int maxHealth, int maxStamina, ObjectId objectId) {
		super(x, y, width, height, objectId);
		
		health = this.maxHealth = maxHealth;
		stamina = this.maxStamina = maxStamina;
		staminaRegen = (int) (stamina / 200f);
		this.damage = damage;
		
		falling = true;
		jumping = false;
		attacking = false;
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
	public abstract void applyKnockback(GameObject attacker, float velX, float velY);
	
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
	
	private float clamp(float min, float num, float max) {
		if (num < min)
			num = min;
		else if (num > max)
			num = max;
		return num;
	}

}
