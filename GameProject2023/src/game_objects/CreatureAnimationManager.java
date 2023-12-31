package game_objects;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import framework.Animation;
import framework.BufferedImageUtil;

public class CreatureAnimationManager {

	public enum AnimationType {
		Idle,
		Run,
		Attack1,
		Attack2,
		Jump,
		Hurt,
		Death,
		Dodge,
		Summon,
		Spawn,
		Stun,
	}
	
	// Each animation type is mapped to an animation array.
	// The animation array stores the left and right direction variations of the same animation.
	private Map<AnimationType, Animation[]> animationMap;

	// Invulnerability state causes the enemy to periodically flash white
	private boolean flashWhiteToggle;
	private long flashWhiteToggleTimer;
	private int flashWhiteToggleCooldownMillis = 100; 
	
	/**
	 * Stores this creature's animations and handles necessary logic.
	 */
	public CreatureAnimationManager() {
		animationMap = new HashMap<>();
	}
	
	/**
	 * Stores the given animation with the given type.
	 * Cannot store multiples of the same type.
	 * @param type		 The type of the animation.
	 * @param animations An array of animations that contain both directions of the same animation.
	 */
	public void addAnimation(AnimationType type, Animation[] animations) {
		animationMap.put(type, animations);
	}
	
	/**
	 * Runs the animations with the given type.
	 * @param type The type of the animation.
	 */
	public void runAnimation(AnimationType type) {
		Animation[] animations = animationMap.get(type);
		for (Animation anim : animations)
			anim.runAnimation();
	}
	
	/**
	 * Draws the animations with the given type.
	 * 
	 * @param type			The type of the animation.
	 * @param g				Reference to the Graphics object.
	 * @param direction 	The direction of the animation to draw.
	 * @param invulnerable	The invulnerability state of the creature.
	 * @param x				The x coordinate of the animation's location.
	 * @param y				The y coordinate of the animation's location.
	 * @param width			The width scaling of the animation.
	 * @param height		The height scaling of the animation.
	 */
	public void drawAnimation(AnimationType type, Graphics g, int direction, boolean invulnerable, 
			int x, int y, int width, int height) {
		Animation[] animations = animationMap.get(type);
		int directionIndex = direction == 1 ? 0 : 1;

		if (System.currentTimeMillis() - flashWhiteToggleTimer >= flashWhiteToggleCooldownMillis) {
			flashWhiteToggle = !flashWhiteToggle;
			flashWhiteToggleTimer = System.currentTimeMillis();
		}
		
		BufferedImage currentImage = animations[directionIndex].getCurrentImage();
		if (invulnerable && flashWhiteToggle) 
			currentImage = BufferedImageUtil.getImageInWhite((Graphics2D) g, currentImage);
		g.drawImage(currentImage, x, y, width, height, null);
	}
	
	/**
	 * Resets the animation with the given type.
	 * @param type The type of the animation.
	 */
	public void resetAnimation(AnimationType type) {
		Animation[] animations = animationMap.get(type);
		for (Animation anim : animations)
			anim.resetAnimation();
	}
	
	/**
	 * Checks whether the animation with the given type has finished playing once.
	 * @param type The type of the animation.
	 * @return True if the animation finished playing once, false otherwise.
	 */
	public boolean isAnimationPlayedOnce(AnimationType type) {
		Animation[] animations = animationMap.get(type);
		for (Animation anim : animations)
			if (anim.isPlayedOnce())
				return true;
		return false;
	}
	
	/**
	 * Returns the current frame number of the animation with the given type.
	 * @param type The type of the animation.
	 * @return The current frame number that the animation is on.
	 */
	public int getCurrentAnimationFrame(AnimationType type) {
		Animation[] animations = animationMap.get(type);
		int currentFrame = 0;
		for (Animation anim : animations)
			if (anim.getCurrentFrame() > currentFrame)
				currentFrame = anim.getCurrentFrame();
		return currentFrame;
	}
	
}
