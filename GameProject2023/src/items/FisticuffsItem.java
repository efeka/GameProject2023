package items;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.Weapon;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

public class FisticuffsItem extends WeaponItem {

	public FisticuffsItem(float x, float y, KeyInput keyInput, MouseInput mouseInput, ObjectHandler objectHandler) {
		super(x, y, keyInput, mouseInput, objectHandler, Name.FisticuffsItem);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.FisticuffsItem)[0];
		
		int animDelay = 9;
		animation = new Animation(textureLoader.getTextures(TextureName.FisticuffsItem), animDelay, false);
	}

	// TODO
	@Override
	protected Weapon createWeaponFromItem() {
		//return new FisticuffsWeapon(objectHandler, keyInput, MouseInput);
		return null;
	}
	
}
