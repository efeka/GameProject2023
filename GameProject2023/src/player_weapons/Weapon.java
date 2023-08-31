package player_weapons;

import framework.ObjectHandler;
import game_objects.Player;
import items.Item;
import window.Animation;

/*
 * Weapons can have an ability pool.
 * The player should only have access to 2 or 3 abilities of a single weapon.
 * They can find the same type of weapon throughout a run.
 * Each of these weapons would have a selection of abilities pulled randomly from their pool.
 * Maybe there could be a reroll station which allows the player to reroll an ability of their weapon,
 * which could cost in game currency.
 */

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
			throw new IndexOutOfBoundsException("Index " + index + " is invalid for " + getClass());
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
	public abstract Item createItemFromWeapon(float x, float y);
	
}
