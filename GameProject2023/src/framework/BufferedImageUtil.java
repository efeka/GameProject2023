package framework;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class BufferedImageUtil {

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage)
			return (BufferedImage) img;

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}
	
	/**
	 * Scales the given BufferedImage to the specified dimensions.
	 *
	 * @param image 		The original BufferedImage to be scaled.
	 * @param scaledWidth 	The desired width of the scaled image.
	 * @param scaledHeight 	The desired height of the scaled image.
	 * @return A new BufferedImage representing the scaled version of the original image.
	 */
	public static BufferedImage getScaledInstance(BufferedImage image, int scaledWidth, int scaledHeight) {
		Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, 0);
		return toBufferedImage(scaledImage);
	}
	
	/**
	 * Returns a left-clipped BufferedImage by trimming the left side of the original image.
	 *
	 * @param image 		The original BufferedImage to be clipped.
	 * @param clippedWidth 	The desired width of the resulting image after clipping.
	 * @return A new BufferedImage representing the left-clipped portion of the original image.
	 */
	public static BufferedImage getLeftClippedImage(BufferedImage image, int clippedWidth) {
		int originalWidth = image.getWidth();
		int newX = originalWidth - clippedWidth;
		image = image.getSubimage(
				Math.min(newX, originalWidth - 1),
				0,
				Math.max(clippedWidth, 1),
				image.getHeight());
		return image;
	}
	
	/**
	 * Paints the non-transparent pixels of the given image to a white color.
	 * Mainly used for invulnerability effects.
	 * 
	 * @param g2d 	The Graphics2D object
	 * @param image The image to be painted white
	 * @return The white image
	 */
	public static BufferedImage getImageInWhite(Graphics2D g2d, BufferedImage image) {
	    BufferedImage whiteImage = new BufferedImage(image.getWidth(), image.getHeight(), 
	    		BufferedImage.TYPE_INT_ARGB);

	    for (int y = 0; y < image.getHeight(); y++) {
	        for (int x = 0; x < image.getWidth(); x++) {
	            int pixel = image.getRGB(x, y);
	            int alpha = (pixel >> 24) & 0xFF;
	            if (alpha != 0)
	                whiteImage.setRGB(x, y, new Color(199, 207, 204).getRGB());
	        }
	    }

	    return whiteImage;
	}
	
}
