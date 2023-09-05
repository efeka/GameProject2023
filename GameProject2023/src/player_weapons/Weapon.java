package player_weapons;

import framework.ObjectHandler;
import game_objects.Player;
import items.WeaponItem;
import window.Animation;

public abstract class Weapon {
	
	protected ObjectHandler objectHandler;
	protected Player player;
	
	/**
	 * The abilities for this weapon.
	 * The ability on index 0 should always be the regular attack.
	 */
	protected WeaponAbility[] abilities;
	
	public Weapon(ObjectHandler objectHandler) {
		this.objectHandler = objectHandler;
		player = objectHandler.getPlayer();
	}
	
	/**
	 * All abilities related to this weapon should be initialized inside this method.
	 */
	protected abstract void setupAbilities();
	
	/**
	 * Use the weapon ability at the given index.
	 * The functionality of the ability should be implemented here.
	 * 
	 * This method should be called constantly until the abilities
	 * animation is done playing.
	 * @param index the index of the ability to be used
	 */
	public abstract void useAbility(int index);
	
	public WeaponAbility getAbility(int index) {
		if (!isAbilityIndexValid(index))
			return null;
		return abilities[index];
	}
	
	/**
	 * The idle and run animations should be initialized inside this method.
	 */
	protected abstract void setupAnimations();
	public abstract Animation[] getIdleAnimation();
	public abstract Animation[] getRunAnimation();
	
	protected boolean isAbilityIndexValid(int index) {
		return index >= 0 && index < abilities.length && abilities[index] != null;
	}
	
	/**
	 * Create the Item object corresponding to this wepon.
	 * @param x the x coordinate of the Item
	 * @param y the y coordinate of the Item
	 * @return the Item object
	 */
	public abstract WeaponItem createItemFromWeapon(float x, float y);
	
}
