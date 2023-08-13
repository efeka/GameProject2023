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
	
	/**
	 * Provides sprite animation functionality.
	 * @param delay the delay between every frame of animation to draw, dependent on UPS.
	 * @param args image array that holds a sequence of sprites which make up the animation.
	 */
	public Animation(int delay, BufferedImage ... args) {
		 this.delay = delay;
		 
		 frameIndex = delayCounter = 0;
		 frameCount = args.length;
		 
		 images = new BufferedImage[frameCount];
		 for (int i = 0; i < frameCount; i++)
			 images[i] = args[i];
		 
		 currentImage = images[0];
	}
	
	/**
	 * Proceeds to the next frame of animation if enough time has passed to satisfy the delay parameter.
	 */
	public void runAnimation() {	
		delayCounter++;
		if (delayCounter > delay) {
			delayCounter = 0;
			nextFrame();
		}
	}
	
	private void nextFrame() {
		frameIndex = (frameIndex + 1) % frameCount;
		currentImage = images[frameIndex];
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

}
