package items;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.image.BufferedImage;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;
import ui.Inventory;

public class HealthPotionItem extends Item {

	private int healAmount = 30;
	
	public HealthPotionItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, TILE_SIZE, TILE_SIZE, objectHandler, Name.HealthPotionItem);
		texture = TextureLoader.getInstance().getTextures(TextureName.ItemIcons)[9];
	}

	@Override
	public void pickupItem() {
		Inventory inventory = objectHandler.getInventory();
		boolean pickupSuccessful = inventory.addItem(this);
		if (pickupSuccessful)
			objectHandler.removeObject(this);
	}

	@Override
	public BufferedImage getItemIcon() {
		return TextureLoader.getInstance().getTextures(TextureName.ItemIcons)[9];
	}

	@Override
	public void useItem() {
		Player player = objectHandler.getPlayer();
		player.setHealth(player.getHealth() + healAmount);
	}

	@Override
	public int getStackSize() {
		return 4;
	}

}
