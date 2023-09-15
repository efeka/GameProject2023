package visual_effects;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;

import abstract_templates.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class OneTimeAnimation extends GameObject {

	private ObjectHandler objectHandler;
	private Animation animation;

	public OneTimeAnimation(float x, float y, int width, int height, TextureName textureName,
			int animationDelay, ObjectHandler objectHandler) {
		super(x, y, width, height, new ObjectId(Category.Missing, Name.Missing));
		this.objectHandler = objectHandler;

		animation = new Animation(TextureLoader.getInstance().getTextures(textureName), animationDelay, true);
	}
	
	public OneTimeAnimation(float x, float y, TextureName textureName,
			int animationDelay, ObjectHandler objectHandler) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(Category.Missing, Name.Missing));
		this.objectHandler = objectHandler;

		animation = new Animation(TextureLoader.getInstance().getTextures(textureName), animationDelay, true);
	}

	@Override
	public void tick() {
		if (animation.isPlayedOnce())
			objectHandler.removeObject(this);
		else
			animation.runAnimation();
	}

	@Override
	public void render(Graphics g) {
		animation.drawAnimation(g, (int) x, (int) y, width, height);
	}

}
