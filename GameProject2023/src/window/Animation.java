package window;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Animation {

	private int delay;
	private int delayCounter;

	private int frameCount;
	private int frameIndex;

	private BufferedImage[] images;
	private BufferedImage currentImage;

	private boolean onlyPlayOnce;
	private boolean playedOnce = false;

	/**
	 * Provides sprite animation functionality.
	 * @param delay the delay between every frame of animation to draw, dependent on UPS.
	 * @param args image array that holds a sequence of sprites which make up the animation.
	 */
	public Animation(int delay, boolean onlyPlayOnce, BufferedImage ... args) {
		this.delay = delay;
		this.onlyPlayOnce = onlyPlayOnce;

		frameIndex = delayCounter = 0;
		frameCount = onlyPlayOnce ? args.length + 1 : args.length;
		
		// If onlyPlayOnce is true, the last frame needs to be played twice.
		// Without this adjustment, the delay cannot be applied to the last frame
		// resulting in it being skipped immediately. 
		images = new BufferedImage[frameCount];
		for (int i = 0; i < frameCount; i++) {
			if (onlyPlayOnce && i == frameCount - 1)
				images[i] = args[args.length - 1];
			else
				images[i] = args[i];
		}

		currentImage = images[0];
	}
	
	public Animation(BufferedImage[] sprites, int delay, boolean onlyPlayOnce) {
		this.delay = delay;
		this.onlyPlayOnce = onlyPlayOnce;

		frameIndex = delayCounter = 0;
		frameCount = onlyPlayOnce ? sprites.length + 1 : sprites.length;
		
		// If onlyPlayOnce is true, the last frame needs to be played twice.
		// Without this adjustment, the delay cannot be applied to the last frame
		// resulting in it being skipped immediately. 
		images = new BufferedImage[frameCount];
		for (int i = 0; i < frameCount; i++) {
			if (onlyPlayOnce && i == frameCount - 1)
				images[i] = sprites[sprites.length - 1];
			else
				images[i] = sprites[i];
		}

		currentImage = images[0];
	}

	/**
	 * Proceeds to the next frame of animation if enough time has passed to satisfy the delay parameter.
	 */
	public void runAnimation() {
		if (onlyPlayOnce && playedOnce)
			return;

		delayCounter++;
		if (delayCounter > delay) {
			delayCounter = 0;
			nextFrame();
		}
	}

	private void nextFrame() {
		frameIndex = (frameIndex + 1) % frameCount;
		currentImage = images[frameIndex];

		if (frameIndex == frameCount - 1)
			playedOnce = true;
	}

	/**
	 * Draws the current frame of the animation.
	 * @param g the graphics object to draw with.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 */
	public void drawAnimation(Graphics g, int x, int y) {
		g.drawImage(currentImage, x, y, null);
	}

	/**
	 * Draws the current frame of the animation.
	 * @param g the graphics object to draw with.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param width the scaling on width.
	 * @param height the scaling on height.
	 */
	public void drawAnimation(Graphics g, int x, int y, int width, int height) {
		g.drawImage(currentImage, x, y, width, height, null);
	}

	public void resetAnimation() {
		playedOnce = false;
		frameIndex = delayCounter = 0;
		currentImage = images[0];
	}

	public boolean isPlayedOnce() {
		return playedOnce;
	}
	
	public int getCurrentFrame() {
		return frameIndex;
	}

}
