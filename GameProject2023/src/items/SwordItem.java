package items;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.SwordWeapon;
import player_weapons.Weapon;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

public class SwordItem extends WeaponItem {

	public SwordItem(float x, float y, KeyInput keyInput, MouseInput mouseInput, ObjectHandler objectHandler) {
		super(x, y, keyInput, mouseInput, objectHandler, Name.SwordItem);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.SwordItem)[0];
		
		int spinDelay = 7;
		animation = new Animation(textureLoader.getTextures(TextureName.SwordItem), spinDelay, false);
	}

	@Override
	protected Weapon createWeaponFromItem() {
		return new SwordWeapon(objectHandler, keyInput, mouseInput);
	}
	
}
