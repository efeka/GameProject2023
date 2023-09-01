package items;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.SwordWeapon;
import player_weapons.Weapon;
import window.Animation;

public class SwordItem extends Item {

	public SwordItem(float x, float y, ObjectHandler objectHandler) {
		super(x, y, objectHandler, Name.SwordItem);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.SwordItem)[0];
		
		int spinDelay = 7;
		animation = new Animation(textureLoader.getTextures(TextureName.SwordItem), spinDelay, false);
	}

	@Override
	protected Weapon createWeaponFromItem() {
		return new SwordWeapon(objectHandler);
	}
	
}
