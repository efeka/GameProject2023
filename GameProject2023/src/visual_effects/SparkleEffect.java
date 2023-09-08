package visual_effects;

import framework.GameConstants;
import framework.ObjectHandler;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class SparkleEffect extends OneTimeAnimationEffect {

	public SparkleEffect(float x, float y, ObjectHandler objectHandler) {
		super(x, y, GameConstants.ScaleConstants.TILE_SIZE, GameConstants.ScaleConstants.TILE_SIZE, objectHandler);
	}
	
	public SparkleEffect(float x, float y, int width, int height, ObjectHandler objectHandler) {
		super(x, y, width, height, objectHandler);
	}

	@Override
	public Animation getAnimation() {
		int sparkleDelay = 8;
		return new Animation(TextureLoader.getInstance().getTextures(TextureName.SparkleEffect), sparkleDelay, true);
	}

	
	
}
