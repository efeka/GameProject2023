package items;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.Weapon;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

public class HammerItem extends WeaponItem {

	public HammerItem(float x, float y, KeyInput keyInput, MouseInput mouseInput, ObjectHandler objectHandler) {
		super(x, y, keyInput, mouseInput, objectHandler, Name.HammerItem);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.HammerItem)[0];
		
		int spinDelay = 6;
		animation = new Animation(textureLoader.getTextures(TextureName.HammerItem), spinDelay, false);
	}

	@Override
	public Weapon createWeaponFromItem() {
		//return new HammerWeapon(objectHandler);
		return null;
	}
	
}
