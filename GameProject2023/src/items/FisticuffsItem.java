package items;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.FisticuffsWeapon;
import player_weapons.Weapon;
import window.Animation;

public class FisticuffsItem extends WeaponItem {

	public FisticuffsItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, objectHandler, Name.FisticuffsItem);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.FisticuffsItem)[0];
		
		int animDelay = 9;
		animation = new Animation(textureLoader.getTextures(TextureName.FisticuffsItem), animDelay, false);
	}

	@Override
	protected Weapon createWeaponFromItem() {
		return new FisticuffsWeapon(objectHandler);
	}
	
}
