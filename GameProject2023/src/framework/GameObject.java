package framework;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * The GameObject class is the base class for all objects in the game that require frequent updating and rendering.
 * It provides essential attributes and methods common to all game objects.
 */
public abstract class GameObject {
	
	private int x, y;
	private int width, height;
	private ObjectId objectId;
	private BufferedImage texture;
	
	public GameObject(int x, int y, int width, int height, ObjectId objectId) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.objectId = objectId;
	}
	
	/**
	 * Update this game object's current state depending on its behaviour.
	 */
	public abstract void update();
	
	/**
	 * Draw the object onto the screen.
	 * @param g the graphics object to draw into.
	 */
	public abstract void render(Graphics g);
	
	/**
	 * Get an image which represents this game object.
	 * This is for coupling objects with textures, which is used in the Level Designer program.
	 * This image does not have to exactly match how the object is rendered into the game.
	 * @return image representing this object.
	 */
	public abstract BufferedImage getTexture();

	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, width, height);
	}

	// Getters and setters
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

}