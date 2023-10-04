package items;

import java.awt.image.BufferedImage;

import abstracts.Item;
import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;

public class BigHealthPotionItem extends Item {

	private int healAmount = 50;
	
	public BigHealthPotionItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, objectHandler, Name.BigHealthPotionItem);
		texture = TextureLoader.getInstance().getTextures(TextureName.BigHealthPotionIcon)[0];
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

	@Override
	public boolean isEquippable() {
		return true;
	}

}
