package items;

import java.awt.image.BufferedImage;

import abstracts.Item;
import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;

public class SmallHealthPotionItem extends Item {

	private int healAmount = 20;
	
	public SmallHealthPotionItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, objectHandler, Name.SmallHealthPotionItem);
		texture = TextureLoader.getInstance().getTextures(TextureName.SmallHealthPotionIcon)[0];
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

	@Override
	public boolean isEquippable() {
		return true;
	}

}
