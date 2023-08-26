package player_weapons;

import framework.ObjectHandler;
import game_objects.Player;
import window.Animation;

public abstract class Weapon {

	public enum WeaponType {
		Unarmed,
		Sword,
		Hammer,
	}
	protected WeaponType weaponType;
	
	protected ObjectHandler objectHandler;
	protected Player player;
	
	public Weapon(ObjectHandler objectHandler, WeaponType weaponType) {
		this.objectHandler = objectHandler;
		this.weaponType = weaponType;
		player = objectHandler.getPlayer();
	}
	
	public abstract void useAbility(int index);
	public abstract WeaponAbility getAbility(int index);
	
	public abstract Animation[] getIdleAnimation();
	public abstract Animation[] getRunAnimation();
	public abstract Animation[] getAttackAnimation();
	
	public WeaponType getWeaponType() {
		return weaponType;
	}
	
}
