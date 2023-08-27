package game_objects;

import player_weapons.Weapon;
import window.Animation;

public class PlayerAnimationHandler {

	private Player player;

	public PlayerAnimationHandler(Player player) {
		this.player = player;
	}
	
	public Animation getIdleAnimation() {
		return player.getWeapon().getIdleAnimation()[getIndexFromDirection()];
	}

	public Animation getRunAnimation() {
		return player.getWeapon().getRunAnimation()[getIndexFromDirection()];
	}

	public Animation getAttackAnimation() {
		return player.getWeapon().getAttackAnimation()[getIndexFromDirection()];
	}
	
	public void resetAttackAnimations() {
		player.getWeapon().getAttackAnimation()[0].resetAnimation();
		player.getWeapon().getAttackAnimation()[1].resetAnimation();
	}
	
	public boolean areAttackAnimationsFinished() {
		Weapon weapon = player.getWeapon();
		return weapon.getAttackAnimation()[0].isPlayedOnce() || weapon.getAttackAnimation()[1].isPlayedOnce();
	}
	
	/**
	 * Maps the player's direction to array indices: (-1,1) becomes (1,0).
	 * This is possible because sprites that are facing right are loaded in the 0th index,
	 * while the sprites that are facing left are always loaded in the 1st index of the animation arrays.
	 */
	public int getIndexFromDirection() {
		return (-player.getDirection() + 1) / 2;
	}

}