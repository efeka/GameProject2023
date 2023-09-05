package visual_effects;

import java.awt.Graphics;

import abstract_objects.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import window.Animation;

public abstract class VisualEffect extends GameObject {

	private ObjectHandler objectHandler;
	private Animation animation;
	
	public VisualEffect(float x, float y, int width, int height, ObjectHandler objectHandler) {
		super(x, y, width, height, new ObjectId(Category.Missing, Name.Missing));
		this.objectHandler = objectHandler;
	}
	
	public abstract Animation getAnimation();
	
	@Override
	public void tick() {
		if (animation == null)
			animation = getAnimation();
		else {
			if (animation.isPlayedOnce())
				objectHandler.removeObject(this);
			else
				animation.runAnimation();
		}
	}
	
	@Override
	public void render(Graphics g) {
		if (animation == null)
			animation = getAnimation();
		else
			animation.drawAnimation(g, (int) x, (int) y, width, height);
	}

}
