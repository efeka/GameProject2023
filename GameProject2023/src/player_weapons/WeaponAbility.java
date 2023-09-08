package player_weapons;

import window.Animation;

public class WeaponAbility {

	private int damage;
	
	private int cooldownMillis;
	private long lastUsedTimer;
	
	private Animation[] animations;
	
	public WeaponAbility(int cooldownMillis, int damage, Animation[] animations) {
		this.cooldownMillis = cooldownMillis;
		lastUsedTimer = 0;
		this.animations = animations;
		this.damage = damage;
	}
	
	public boolean isOnCooldown() {
		return System.currentTimeMillis() - lastUsedTimer < cooldownMillis;
	}
	
	public void startCooldown() {
		lastUsedTimer = System.currentTimeMillis();
	}
	
	public void resetCooldown() {
		lastUsedTimer = -cooldownMillis;
	}
	
	public Animation[] getAnimations() {
		return animations;
	}
	
	public void resetAnimations() {
		for (Animation anim : animations)
			anim.resetAnimation();
	}
	
	public int timeLeftUntilReady() {
		return (int) (cooldownMillis - (System.currentTimeMillis() - lastUsedTimer)); 
	}
	
	public int getCooldown() {
		return cooldownMillis;
	}
	
	public int getDamage() {
		return damage;
	}
	
}
