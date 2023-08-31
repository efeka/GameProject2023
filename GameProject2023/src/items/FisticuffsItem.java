package items;

import framework.GameConstants.ScaleConstants;
import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.FisticuffsWeapon;
import player_weapons.Weapon;
import window.Animation;

public class FisticuffsItem extends Item {

	public FisticuffsItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, ScaleConstants.TILE_SIZE * 2, ScaleConstants.TILE_SIZE * 2, objectHandler, Name.FisticuffsItem);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.FisticuffsItem)[0];
		
		int spinDelay = 9;
		animation = new Animation(textureLoader.getTextures(TextureName.FisticuffsItem), spinDelay, false);
	}

	@Override
	public Weapon createWeaponFromItem() {
		return new FisticuffsWeapon(objectHandler);
	}
	
}
