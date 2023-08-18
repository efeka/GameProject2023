package object_templates;

import framework.GameConstants;
import framework.GameObject;
import framework.ObjectId;

public abstract class Creature extends GameObject {

	protected final float GRAVITY = GameConstants.CreatureConstants.GRAVITY;
	protected final int TERMINAL_VELOCITY = GameConstants.CreatureConstants.TERMINAL_VELOCITY;
	
	protected float velX, velY;
	protected boolean falling, jumping, attacking;
	// 1 for right, -1 for left
	protected int direction = 1;
	
	protected int maxHealth, maxStamina;
	protected float health, stamina;
	protected float staminaRegen;
	
	public Creature(int x, int y, int width, int height, int maxHealth, int maxStamina, ObjectId objectId) {
		super(x, y, width, height, objectId);
		
		health = this.maxHealth = maxHealth;
		stamina = this.maxStamina = maxStamina;
		staminaRegen = (int) (stamina / 200f);
		
		falling = true;
		jumping = false;
		attacking = false;
	}

	public void regenerateStamina() {
		if (stamina < maxStamina)
			stamina += staminaRegen;
		if (stamina > maxStamina)
			stamina = maxStamina;
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
		if (health >= 0 && health < maxHealth)
			this.health = health;
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
		if (stamina >= 0 && stamina < maxStamina)
			this.stamina = stamina;
	}

}
