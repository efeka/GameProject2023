package game_objects;

import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class PlayerAnimationHandler {

	private Player player;

	private Animation[] doubleJumpAnimations;
	private Animation[] dodgeAnimations;
	private Animation[] landAnimations;

	public PlayerAnimationHandler(Player player) {
		this.player = player;
		setupAnimations();
	}

	private void setupAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();

		int doubleJumpDelay = 8;
		doubleJumpAnimations = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDoubleJump, 1),
						doubleJumpDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDoubleJump, -1),
						doubleJumpDelay, true),
		};

		int dodgeAnimationDelay = 6;
		dodgeAnimations = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDodge, 1),
						dodgeAnimationDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDodge, -1),
						dodgeAnimationDelay, true),
		};

		int landAnimationDelay = 8; 
		landAnimations = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerLand, 1), 
						landAnimationDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerLand, -1), 
						landAnimationDelay, true),
		};
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

	public Animation getDodgeAnimation() {
		return dodgeAnimations[getIndexFromDirection()];
	}

	public Animation getDodgeAnimation(int direction) {
		if (direction == 1)
			return dodgeAnimations[0];
		else
			return dodgeAnimations[1];
	}

	public boolean isDodgeAnimationFinished() {
		return dodgeAnimations[0].isPlayedOnce() || dodgeAnimations[1].isPlayedOnce();
	}

	public void resetDodgeAnimations() {
		dodgeAnimations[0].resetAnimation();
		dodgeAnimations[1].resetAnimation();
	}
	
	public Animation getLandAnimation() {
		return landAnimations[getIndexFromDirection()];
	}
	
	public Animation getLandAnimation(int direction) {
		if (direction == 1)
			return landAnimations[0];
		else
			return landAnimations[1];
	}
	
	public boolean isLandAnimationFinished() {
		return landAnimations[0].isPlayedOnce() || landAnimations[1].isPlayedOnce();
	}
	
	public void resetLandAnimations() {
		landAnimations[0].resetAnimation();
		landAnimations[1].resetAnimation();
	}

	/**
	 * Maps the player's direction to array indices: {-1,1} becomes {1,0}.
	 * This is possible because sprites that are facing right are loaded in the 0th index,
	 * while the sprites that are facing left are always loaded in the 1st index of the animation arrays.
	 */
	public int getIndexFromDirection() {
		return (-player.getDirection() + 1) / 2;
	}

}
