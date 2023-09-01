package items;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import abstract_objects.GameObject;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.GameConstants.ScaleConstants;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import game_objects.Player;
import player_weapons.Weapon;
import window.Animation;

public abstract class Item extends GameObject {

	protected Animation animation = null;
	protected ObjectHandler objectHandler;
	
	private boolean jumping = true;
	private float velY = -3f;

	public Item(float x, float y, ObjectHandler objectHandler, Name objectName) {
		super(x, y, ScaleConstants.PLAYER_WIDTH, ScaleConstants.PLAYER_HEIGHT, new ObjectId(Category.Item, objectName));
		this.objectHandler = objectHandler;
	}
	
	/**
	 * Create the Weapon object corresponding to this item.
	 * @return the Weapon object
	 */
	protected abstract Weapon createWeaponFromItem();
	
	@Override
	public void tick() {
		y += velY;

		if (jumping) {
			velY += GameConstants.CreatureConstants.GRAVITY;

			if (velY > GameConstants.CreatureConstants.TERMINAL_VELOCITY)
				velY = GameConstants.CreatureConstants.TERMINAL_VELOCITY;
		}
		
		checkGroundCollision();
		
		if (animation != null)
			animation.runAnimation();		
	}

	@Override
	public void render(Graphics g) {
		if (animation != null)
		animation.drawAnimation(g, (int) x, (int) y, width, height);
	}
	
	private void checkGroundCollision() {
		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			if (other.equals(this))
				continue;
			
			if (other.getObjectId().getCategory() == Category.Block) {
				if (getBottomBounds().intersects(other.getBounds())) {
					y = other.getY() - height;
					velY = 0;
					jumping = false;
				}
			}
		}
	}
	
	private Rectangle getBottomBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		float yOffset = 4 * this.height / 5f;
		return new Rectangle((int) (x + xOffset), (int) (y + yOffset), (int) width, (int) height);
	}

	/**
	 * Equip the Item as a Weapon and drop the current Weapon as an Item.
	 */
	public void pickupItem() {
		Player player = objectHandler.getPlayer();
		Weapon oldWeapon = player.getWeapon();
		
		// Equip the new weapon
		Weapon newWeapon = createWeaponFromItem();
		player.setWeapon(newWeapon);
		
		// Remove the item which from the game after it was picked up
		objectHandler.removeObject(this);
		
		// Drop the player's current weapon in Item form
		// so that it can be picked up again later
		
		float itemX = player.getX() + player.getWidth() / 2 - width / 2;
		float itemY = player.getY() - height / 2;
		Item oldItem = oldWeapon.createItemFromWeapon(itemX, itemY);
		if (oldItem.getObjectId().getName() != Name.FisticuffsItem)
			objectHandler.addObject(oldItem, ObjectHandler.MIDDLE_LAYER);
	}

}
