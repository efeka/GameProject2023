package items;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import abstract_templates.GameObject;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader.TextureName;
import visual_effects.OneTimeAnimation;
import window.Animation;

public abstract class Item extends GameObject {

	protected Animation animation = null;
	protected ObjectHandler objectHandler;

	private boolean jumping = true;
	protected float velX;
	protected float velY = -3f;

	public Item(float x, float y, ObjectHandler objectHandler, Name objectName) {
		super(x, y, 4 * TILE_SIZE / 5, 4 * TILE_SIZE / 5, new ObjectId(Category.Item, objectName));
		this.objectHandler = objectHandler;
	}
	
	public Item(float x, float y, int width, int height, ObjectHandler objectHandler, Name objectName) {
		super(x, y, width, height, new ObjectId(Category.Item, objectName));
		this.objectHandler = objectHandler;
	}

	/**
	 * Handles the behaviour of this object when the player picks it up.
	 */
	public abstract void pickupItem();
	
	/**
	 * Handles the behaviour of this object when the player uses it.
	 */
	public abstract void useItem();
	
	/**
	 * @return The maximum number of items of this type that can fit inside one inventory slot.
	 */
	public abstract int getMaxStackSize();
	
	/**
	 * The image that represents this item in the inventory.
	 * @return the buffered image
	 */
	public abstract BufferedImage getItemIcon();
	
	@Override
	public void tick() {
		x += velX;
		y += velY;

		if (jumping) {
			velY += GameConstants.PhysicsConstants.GRAVITY;

			if (velY > GameConstants.PhysicsConstants.TERMINAL_VELOCITY)
				velY = GameConstants.PhysicsConstants.TERMINAL_VELOCITY;
		}
		
		checkCollision();
		
		if (animation != null)
			animation.runAnimation();		
	}

	@Override
	public void render(Graphics g) {
		if (animation != null)
			animation.drawAnimation(g, (int) x, (int) y, width, height);
		else
			g.drawImage(texture, (int) x, (int) y, width, height, null);
	}
	
	private void checkCollision() {
		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			if (other.equals(this))
				continue;
			
			if (other.getObjectId().getCategory() == Category.Block) {
				if (getHorizontalBounds().intersects(other.getBounds())) {
					velX = 0;
					if (x < other.getX())
						x = other.getX() - width;
					else
						x = other.getX() + other.getWidth(); 
				}
				
				if (getBottomBounds().intersects(other.getBounds())) {
					y = other.getY() - height;
					velX = velY = 0;
					jumping = false;
				}
			}
		}
	}
	
	@Override
	public Rectangle getBounds() {
		if (velY == 0)
			return super.getBounds();
		else
			return new Rectangle(0, 0, 0, 0);
	}
	
	private Rectangle getBottomBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		float yOffset = 4 * this.height / 5f;
		return new Rectangle((int) (x + xOffset), (int) (y + yOffset + velY), (int) width, (int) height);
	}
	
	protected Rectangle getHorizontalBounds() {
		float height = 3 * this.height / 5f;
		float yOffset = this.height / 5f; 
		return new Rectangle((int) (x + velX), (int) (y + yOffset), width, (int) height);
	}
	
	protected void playPickupAnimation() {
		OneTimeAnimation sparkleAnimation = new OneTimeAnimation(x - width / 2, y - width / 2, width * 2, height * 2,
				TextureName.SparkleEffect, 6, objectHandler);
		objectHandler.addObject(sparkleAnimation, ObjectHandler.MIDDLE_LAYER);
	}

}
