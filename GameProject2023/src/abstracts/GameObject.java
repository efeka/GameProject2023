package abstracts;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import framework.ObjectId;
import framework.ObjectId.Category;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

/**
 * The GameObject class is the base class for all objects in the game that require frequent updating and rendering.
 * It provides essential attributes and methods common to all game objects.
 */
public abstract class GameObject {
	
	protected float x, y;
	protected int width, height;
	protected ObjectId objectId;
	protected BufferedImage texture;
	
	public GameObject(float x, float y, int width, int height, ObjectId objectId) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.objectId = objectId;
	}

	/**
	 * Update this game object's current state depending on its behaviour.
	 */
	public abstract void tick();
	
	/**
	 * Draw the object onto the screen.
	 * @param g The graphics object to draw into.
	 */
	public abstract void render(Graphics g);

	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, width, height);
	}
	
	/**
	 * Returns an image which represents this game object.
	 * This is for coupling GameObjects with textures, which is used in the Level Designer program.
	 * This image does not always have to exactly match how the object is rendered into the game.
	 * @return The image representing this object.
	 */
	public BufferedImage getTexture() {
		if (texture == null)
			texture = TextureLoader.getInstance().getTextures(TextureName.Missing)[0];
		return texture;
	}
	
	/**
	 * Compares this object's Name with the given object.
	 * @param gameObject The GameObject to compare Names with.
	 * @return True if the names match, false otherwise.
	 */
	public boolean compareObjectName(GameObject gameObject) {
		return objectId.getName() == gameObject.objectId.getName();
	}
	
	/**
	 * Compares this object's Category with the given Category.
	 * @param category The Category to be compared
	 * @return True if the categories match, false otherwise.
	 */
	public boolean compareCategory(Category category) {
		return objectId != null && objectId.getCategory() == category;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ObjectId getObjectId() {
		return objectId;
	}
	
}