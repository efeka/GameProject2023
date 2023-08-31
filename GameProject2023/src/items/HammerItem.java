package items;

import framework.GameConstants.ScaleConstants;
import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.HammerWeapon;
import player_weapons.Weapon;
import window.Animation;

public class HammerItem extends Item {

	public HammerItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, ScaleConstants.TILE_SIZE * 2, ScaleConstants.TILE_SIZE * 2, objectHandler, Name.HammerItem);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.HammerItem)[0];
		
		int spinDelay = 6;
		animation = new Animation(textureLoader.getTextures(TextureName.HammerItem), spinDelay, false);
	}

	@Override
	public Weapon createWeaponFromItem() {
		return new HammerWeapon(objectHandler);
	}
	
}
