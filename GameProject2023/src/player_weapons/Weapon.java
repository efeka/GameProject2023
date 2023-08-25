package player_weapons;

import framework.ObjectHandler;
import game_objects.Player;

public abstract class Weapon {

	protected ObjectHandler objectHandler;
	protected Player player;
	
	public Weapon(ObjectHandler objectHandler) {
		this.objectHandler = objectHandler;
		player = objectHandler.getPlayer();
	}
	
	public abstract void useAbility(int index);
	public abstract PlayerAbility getAbility(int index);
	
}
