package items;

import java.awt.image.BufferedImage;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.SwordWeapon;
import player_weapons.Weapon;
import window.KeyInput;
import window.MouseInput;

public class SwordWeaponItem extends WeaponItem {

	private SwordWeapon swordWeapon;
	
	public SwordWeaponItem(float x, float y, ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(x, y, objectHandler, Name.SwordWeaponItem);
		texture = TextureLoader.getInstance().getTextures(TextureName.SwordWeaponIcon)[0];
		swordWeapon = new SwordWeapon(objectHandler, keyInput, mouseInput);
	}

	@Override
	public void useItem() {}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public BufferedImage getItemIcon() {
		return TextureLoader.getInstance().getTextures(TextureName.SwordWeaponIcon)[0];
	}

	@Override
	public boolean isEquippable() {
		return true;
	}

	@Override
	public Weapon getWeapon() {
		return swordWeapon;
	}

}
