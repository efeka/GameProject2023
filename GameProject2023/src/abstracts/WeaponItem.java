package abstracts;

import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public abstract class WeaponItem extends Item {

	protected Weapon weapon;
	
	public WeaponItem(float x, float y, ObjectHandler objectHandler, Name objectName) {
		super(x, y, objectHandler, new ObjectId(Category.WeaponItem, objectName));
	}
	
	public WeaponItem(float x, float y, int width, int height, ObjectHandler objectHandler, Name objectName) {
		super(x, y, width, height, objectHandler, new ObjectId(Category.WeaponItem, objectName));
	}
	
	public abstract Weapon getWeapon();

}
