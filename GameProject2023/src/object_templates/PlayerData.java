package object_templates;

public class PlayerData {

	private int maxHealth;
	private float currentHealth;
	private int maxStamina;
	private float currentStamina;
	private float staminaRegen;
	
	public PlayerData(int maxHealth, int currentHealth, int maxStamina, int currentStamina) {
		this.maxHealth = maxHealth;
		this.currentHealth = currentHealth;
		this.maxStamina = maxStamina;
		this.currentStamina = currentStamina;
		staminaRegen = maxStamina / 100f;
	}
	
	public PlayerData(int maxHealth, int maxStamina) {
		this.maxHealth = maxHealth;
		this.maxStamina = maxStamina;
		
		currentHealth = maxHealth;
		currentStamina = maxStamina;
		staminaRegen = maxStamina / 200f;
	}
	
	public void regenerateStamina() {
		setCurrentStamina(currentStamina + staminaRegen);
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getCurrentHealth() {
		return (int) currentHealth;
	}

	public void setCurrentHealth(float health) {
		float newHealth = clamp(0f, health, maxHealth);
		this.currentHealth = newHealth;
	}

	public int getMaxStamina() {
		return maxStamina;
	}

	public void setMaxStamina(int maxStamina) {
		this.maxStamina = maxStamina;
	}

	public float getCurrentStamina() {
		return currentStamina;
	}

	public void setCurrentStamina(float stamina) {
		float newStamina = clamp(0f, stamina, maxStamina);
		this.currentStamina = newStamina;
	}

	public float getStaminaRegen() {
		return staminaRegen;
	}

	public void setStaminaRegen(float staminaRegen) {
		this.staminaRegen = staminaRegen;
	}

	private float clamp(float min, float num, float max) {
		if (min - num > 0f)
			num = min;
		else if (max - num < 0f)
			num = max;
		return num;
	}
	
}
