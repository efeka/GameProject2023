package items;

import java.awt.image.BufferedImage;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;
import ui.Inventory;
import visual_effects.OneTimeAnimation;

public class SmallHealthPotionItem extends Item {

	private int healAmount = 20;
	
	public SmallHealthPotionItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, objectHandler, Name.SmallHealthPotionItem);
		texture = TextureLoader.getInstance().getTextures(TextureName.SmallHealthPotionIcon)[0];
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
		return TextureLoader.getInstance().getTextures(TextureName.SmallHealthPotionIcon)[0];
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
