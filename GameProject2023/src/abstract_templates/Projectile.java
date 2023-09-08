package abstract_templates;

import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public abstract class Projectile extends GameObject {

	protected ObjectHandler objectHandler;
	
	protected float velX;
	protected float velY;
	
	protected int damage;
	
	public Projectile(float x, float y, int width, int height, int damage, ObjectHandler objectHandler) {
		super(x, y, width, height, new ObjectId(Category.Projectile, Name.Missing));
		this.objectHandler = objectHandler;
		this.damage = damage;
	}
	
	public Projectile(float x, float y, int width, int height, float velX, float velY, int damage, ObjectHandler objectHandler) {
		super(x, y, width, height, new ObjectId(Category.Projectile, Name.Missing));
		this.objectHandler = objectHandler;
		this.velX = velX;
		this.velY = velY;
		this.damage = damage;
	}

}
