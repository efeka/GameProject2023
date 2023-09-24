package items;

import java.awt.image.BufferedImage;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;
import ui.Inventory;
import visual_effects.OneTimeAnimation;

public class BigHealthPotionItem extends Item {

	private int healAmount = 50;
	
	public BigHealthPotionItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, objectHandler, Name.BigHealthPotionItem);
		texture = TextureLoader.getInstance().getTextures(TextureName.BigHealthPotionIcon)[0];
	}

	@Override
	public void pickupItem() {
		playPickupAnimation();
		Inventory inventory = objectHandler.getInventory();
		boolean pickupSuccessful = inventory.addItem(this);
		if (pickupSuccessful)
			objectHandler.removeObject(this);
	}

	@Override
	public BufferedImage getItemIcon() {
		return TextureLoader.getInstance().getTextures(TextureName.BigHealthPotionIcon)[0];
	}

	@Override
	public void useItem() {
		Player player = objectHandler.getPlayer();
		player.setHealth(player.getHealth() + healAmount);
	}

	@Override
	public int getMaxStackSize() {
		return 4;
	}

}
