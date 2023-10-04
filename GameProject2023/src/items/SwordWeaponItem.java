package items;

import java.awt.image.BufferedImage;

import abstracts.Weapon;
import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import player_weapons.SwordWeapon;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

public class SwordWeaponItem extends WeaponItem {

	private SwordWeapon swordWeapon;
	
	public SwordWeaponItem(float x, float y, ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(x, y, (int) (TILE_SIZE * 1.5f), (int) (TILE_SIZE * 1.5f), objectHandler, Name.SwordWeaponItem);
		swordWeapon = new SwordWeapon(objectHandler, keyInput, mouseInput);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.SwordWeaponIcon)[0];
		animation = new Animation(textureLoader.getTextures(TextureName.SwordItem), 8, false);
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
