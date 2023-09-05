package items;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import framework.ObjectHandler;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import visual_effects.SparkleEffect;
import window.Animation;

public class Coin extends Item {

	public Coin(float x, float y, ObjectHandler objectHandler) {
		super(x, y, TILE_SIZE / 2, TILE_SIZE / 2, objectHandler, Name.Coin);
		
		velX = (float) (Math.random() * 6 - 3);
		velY = (float) (Math.random() * -1 * 5);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.GoldCoin)[0];
		
		/*
		 * Bronze is worth the least 45% chance to appear
		 * Gold is worth the most, 20% chance to appear
		 * Silver has 35% chance to appear
		 */
		int randomCoin = (int) (Math.random() * 100);
		TextureName textureName;
		if (randomCoin < 45)
			textureName = TextureName.BronzeCoin;
		else if (randomCoin >= 45 && randomCoin < 80)
			textureName = TextureName.SilverCoin;
		else
			textureName = TextureName.GoldCoin;
		
		int spinDelay = 7;
		animation = new Animation(textureLoader.getTextures(textureName), spinDelay, false);
	}

	@Override
	public void pickupItem() {
		// TODO increase player's coin count
		SparkleEffect sparkleEffect = new SparkleEffect(x, y, objectHandler);
		sparkleEffect.setX(x - width / 2);
		sparkleEffect.setY(y - height / 2);
		objectHandler.addObject(sparkleEffect, ObjectHandler.MIDDLE_LAYER);
		objectHandler.removeObject(this);
	}

}
