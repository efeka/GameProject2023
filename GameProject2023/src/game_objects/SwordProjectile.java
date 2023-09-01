package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Graphics;
import java.util.ArrayList;

import abstract_objects.Creature;
import abstract_objects.GameObject;
import abstract_objects.Projectile;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import items.SwordItem;
import window.Animation;

public class SwordProjectile extends Projectile {

	private Animation spinAnimation;

	public SwordProjectile(float x, float y, float velX, float velY, int damage, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2, velX, velY, damage, objectHandler);

		TextureLoader textureLoader = TextureLoader.getInstance();
		int spinDelay = 6;
		spinAnimation = new Animation(textureLoader.getTextures(TextureName.SwordProjectile), spinDelay, false);
	}

	@Override
	public void tick() {
		x += velX;

		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			if (other.equals(this))
				continue;
			
			if (other.getObjectId().getCategory() == ObjectId.Category.Block ||
					other.getObjectId().getCategory() == ObjectId.Category.Enemy) {
				if (getBounds().intersects(other.getBounds())) {
					if (other.getObjectId().getCategory() == ObjectId.Category.Enemy)
						((Creature) other).takeDamage(damage);
					
					SwordItem swordItem = null;
					if (x < other.getX())
						swordItem = new SwordItem(other.getX() - width * 2, y, objectHandler);
					else
						swordItem = new SwordItem(other.getX() + other.getWidth(), y, objectHandler);

					objectHandler.addObject(swordItem, ObjectHandler.MIDDLE_LAYER);
					objectHandler.removeObject(this);
					break;
				}
			}
		}

		spinAnimation.runAnimation();
	}

	@Override
	public void render(Graphics g) {
		spinAnimation.drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
	}

}
