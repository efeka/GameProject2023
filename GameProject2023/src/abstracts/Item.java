package abstracts;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import framework.Animation;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader.TextureName;
import ui.Inventory;
import visual_effects.OneTimeAnimation;

public abstract class Item extends GameObject {

	protected Animation animation = null;
	protected ObjectHandler objectHandler;

	private boolean falling = true;
	protected float velX;
	protected float velY = -3f;

	public Item(float x, float y, ObjectHandler objectHandler, Name objectName) {
		super(x, y, 4 * TILE_SIZE / 5, 4 * TILE_SIZE / 5, new ObjectId(Category.Item, objectName));
		this.objectHandler = objectHandler;
	}
	
	public Item(float x, float y, ObjectHandler objectHandler, ObjectId objectId) {
		super(x, y, 4 * TILE_SIZE / 5, 4 * TILE_SIZE / 5, objectId);
		this.objectHandler = objectHandler;
	}
	
	public Item(float x, float y, int width, int height, ObjectHandler objectHandler, Name objectName) {
		super(x, y, width, height, new ObjectId(Category.Item, objectName));
		this.objectHandler = objectHandler;
	}
	
	public Item(float x, float y, int width, int height, ObjectHandler objectHandler, ObjectId objectId) {
		super(x, y, width, height, objectId);
		this.objectHandler = objectHandler;
	}

	/**
	 * Handles the behaviour of this object when the player picks it up.
	 */
	public void pickupItem() {
		playPickupAnimation();
		Inventory inventory = objectHandler.getInventory();
		boolean pickupSuccessful = inventory.addItem(this);
		if (pickupSuccessful)
			objectHandler.removeObject(this);
	}
	
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
	 * @return The buffered image for how this item looks in the inventory.
	 */
	public abstract BufferedImage getItemIcon();
	
	/**
	 * @return True if this item is equippable, false otherwise.
	 */
	public abstract boolean isEquippable();
	
	@Override
	public void tick() {
		x += velX;
		y += velY;

		if (falling) {
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
		falling = true;
		
		List<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			if (other.equals(this))
				continue;
			
			if (other.compareCategory(Category.Block)) {
				if (getGroundCheckBounds().intersects(other.getBounds()))
					falling = false;
				
				if (getBottomBounds().intersects(other.getBounds())) {
					y = other.getY() - height;
					velX = velY = 0;
				}
				if (getHorizontalBounds().intersects(other.getBounds())) {
					velX = 0;
					if (x < other.getX())
						x = other.getX() - width;
					else
						x = other.getX() + other.getWidth(); 
				}
			}
			
			if (other.compareCategory(Category.JumpThroughBlock)) {
				if (getGroundCheckBounds().intersects(other.getBounds()))
					falling = false;
				
				if (getBottomBounds().intersects(other.getBounds())) {
					y = other.getY() - height;
					velX = velY = 0;
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
	
	protected Rectangle getGroundCheckBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		float yOffset = this.height;
		return new Rectangle((int) (x + xOffset), (int) (y + yOffset), (int) width, (int) height);
	}
	
	protected void playPickupAnimation() {
		OneTimeAnimation sparkleAnimation = new OneTimeAnimation(x - width / 2, y - width / 2, width * 2, height * 2,
				TextureName.SparkleEffect, 6, objectHandler);
		objectHandler.addObject(sparkleAnimation, ObjectHandler.MIDDLE_LAYER);
	}

}
