package player_weapons;

import window.Animation;

public class PlayerAbility {

	private int cooldown;
	private long lastUsedTimer;
	
	private Animation[] animation;
	
	public PlayerAbility(int cooldownMillis, Animation[] animation) {
		cooldown = cooldownMillis;
		lastUsedTimer = -cooldownMillis;
		this.animation = animation;
	}
	
	/**
	 * Checks if enough time has passed to satisfy the cooldown condition.
	 * @return if the ability is ready to be used again
	 */
	public boolean isOnCooldown() {
		return System.currentTimeMillis() - lastUsedTimer < cooldown;
	}
	
	public Animation getAnimation(int index) {
		return animation[index];
	}
	
	public boolean isAbilityBeingUsed() {
		return !(animation[0].isPlayedOnce() || animation[1].isPlayedOnce());
	}
	
	/**
	 * Resets the animation and the cooldown of the ability.
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
	
}
