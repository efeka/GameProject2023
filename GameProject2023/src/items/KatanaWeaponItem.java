package items;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.image.BufferedImage;

import abstracts.Weapon;
import abstracts.WeaponItem;
import framework.Animation;
import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.KatanaWeapon;
import window.KeyInput;
import window.MouseInput;

public class KatanaWeaponItem extends WeaponItem {

	private KatanaWeapon katanaWeapon;
	
	public KatanaWeaponItem(float x, float y, ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(x, y, (int) (TILE_SIZE * 1.5f), (int) (TILE_SIZE * 1.5f), objectHandler, Name.KatanaWeaponItem);
		katanaWeapon = new KatanaWeapon(objectHandler, keyInput, mouseInput);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.KatanaWeaponIcon)[0];
		animation = new Animation(textureLoader.getTextures(TextureName.KatanaItem), 8, false);
	}

	@Override
	public void useItem() {}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public BufferedImage getItemIcon() {
		return TextureLoader.getInstance().getTextures(TextureName.KatanaWeaponIcon)[0];
	}

	@Override
	public boolean isEquippable() {
		return true;
	}

	@Override
	public Weapon getWeapon() {
		return katanaWeapon;
	}

}
