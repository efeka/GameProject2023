package abstract_objects;

import framework.ObjectHandler;
import game_objects.Player;

public class PlayerAbility {

	private int cooldown;
	private long lastUsedTimer;
	
	private ObjectHandler objectHandler;
	private Player player;
	
	public PlayerAbility(ObjectHandler objectHandler, int cooldown) {
		this.cooldown = cooldown;
		this.objectHandler = objectHandler;
		this.player = objectHandler.getPlayer();
	}
	
}
