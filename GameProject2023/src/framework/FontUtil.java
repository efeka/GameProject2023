package framework;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

public class FontUtil {

	/**
	 * Calculates the position for rendering text with center alignment based on a given center point.
	 *
	 * @param g            The Graphics context for rendering.
	 * @param font         The Font used for the text.
	 * @param centerPoint  The center point around which the text will be aligned.
	 * @param text         The text to be rendered.
	 * @return A Point representing the position for rendering the text with center alignment.
	 */
	public static Point getTextPositionForCenterAlignment(Graphics g, Font font, Point centerPoint, String text) {
	    FontMetrics metrics = g.getFontMetrics(font);
	    int textWidth = metrics.stringWidth(text);
	    int textHeight = metrics.getHeight();

	    int verticalPosition = centerPoint.y + (textHeight / 2) - metrics.getDescent();
	    int horizontalPosition = centerPoint.x - (textWidth / 2);
	    return new Point(horizontalPosition, verticalPosition);
	}
	
	/**
	 * Calculates the position for rendering text with left alignment based on a given starting point.
	 *
	 * @param g         The Graphics context for rendering.
	 * @param font      The Font used for the text.
	 * @param leftPoint The starting point for rendering the text.
	 * @param text      The text to be rendered.
	 * @return A Point representing the position for rendering the text with left alignment.
	 */
	public static Point getTextPositionForLeftAlignment(Graphics g, Font font, Point leftPoint, String text) {
	    FontMetrics metrics = g.getFontMetrics(font);
	    int textHeight = metrics.getHeight();

	    int verticalPosition = leftPoint.y + (textHeight / 2) - metrics.getDescent();
	    int horizontalPosition = leftPoint.x;
	    return new Point(horizontalPosition, verticalPosition);
	}
	
}
