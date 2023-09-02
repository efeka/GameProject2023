package game_objects;

import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class PlayerAnimationHandler {

	private Player player;
	
	private Animation[] doubleJumpAnimations;

	public PlayerAnimationHandler(Player player) {
		this.player = player;
		setupAnimations();
	}
	
	private void setupAnimations() {
		int doubleJumpDelay = 8;
		doubleJumpAnimations = new Animation[2];
		doubleJumpAnimations[0] = new Animation(TextureLoader.getInstance().getTexturesByDirection(TextureName.PlayerDoubleJump,
				1), doubleJumpDelay, false);
		doubleJumpAnimations[1] = new Animation(TextureLoader.getInstance().getTexturesByDirection(TextureName.PlayerDoubleJump,
				-1), doubleJumpDelay, false);
	}
	
	public Animation getIdleAnimation() {
		return player.getWeapon().getIdleAnimation()[getIndexFromDirection()];
	}

	public Animation getRunAnimation() {
		return player.getWeapon().getRunAnimation()[getIndexFromDirection()];
	}
	
	public Animation getDoubleJumpAnimation() {
		return doubleJumpAnimations[getIndexFromDirection()];
	}
	
	/**
	 * Maps the player's direction to array indices: (-1,1) becomes (1,0).
	 * This is possible because sprites that are facing right are loaded in the 0th index,
	 * while the sprites that are facing left are always loaded in the 1st index of the animation arrays.
	 */
	public int getIndexFromDirection() {
		return (-player.getDirection() + 1) / 2;
	}
	
	public Animation getDoubleJumpAnimation(int direction) {
		if (direction == 1)
			return doubleJumpAnimations[0];
		else
			return doubleJumpAnimations[1];
	}
	
	public boolean isDoubleJumpAnimationFinished() {
		return doubleJumpAnimations[0].isPlayedOnce() || doubleJumpAnimations[1].isPlayedOnce();
	}
	
	public void resetDoubleJumpAnimations() {
		doubleJumpAnimations[0].resetAnimation();
		doubleJumpAnimations[1].resetAnimation();
	}

}
