package player_weapons;

import window.Animation;

public class WeaponAbility {

	private int damage;
	
	private int cooldown;
	private long lastUsedTimer;
	
	private Animation[] animation;
	
	public WeaponAbility(int cooldownMillis, Animation[] animation) {
		cooldown = cooldownMillis;
		lastUsedTimer = -cooldownMillis;
		this.animation = animation;
		
		damage = 0;
	}
	
	public WeaponAbility(int cooldownMillis, int damage, Animation[] animation) {
		cooldown = cooldownMillis;
		lastUsedTimer = -cooldownMillis;
		this.animation = animation;
		this.damage = damage;
	}
	
	/**
	 * Checks if enough time has passed to satisfy the cooldown condition.
	 * @return if the ability is ready to be used again
	 */
	public boolean isOnCooldown() {
		return System.currentTimeMillis() - lastUsedTimer < cooldown;
	}
	
	/**
	 * Returns the animation at the given index.
	 * @param index the index of the animation
	 * 		  index = 0 should correspond to an animation that faces right
	 * 		  index = 1 should correspond to an animation that faces left
	 * @return the animation at the given index
	 */
	public Animation getAnimation(int index) {
		return animation[index];
	}
	
	public boolean isAbilityBeingUsed() {
		return isOnCooldown() && !animation[0].isPlayedOnce() && !animation[1].isPlayedOnce();
	}
	
	/**
	 * Resets the animation and the usage timer of the ability.
	 */
	public void startAbility() {
		if (isOnCooldown())
			return;
		lastUsedTimer = System.currentTimeMillis();
		animation[0].resetAnimation();
		animation[1].resetAnimation();
	}
	
	public int timeLeftUntilReady() {
		return (int) (cooldown - (System.currentTimeMillis() - lastUsedTimer)); 
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public int getDamage() {
		return damage;
	}
	
}
