package game_objects;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;

import abstract_templates.Projectile;
import framework.ObjectHandler;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.Animation;

public class SwordSlashProjectile extends Projectile {

	private Animation animation;
	
	public SwordSlashProjectile(float x, float y, float velX, int range, int damage, ObjectHandler objectHandler) {
		super(x, y, TILE_SIZE, TILE_SIZE, velX, 0f, damage, objectHandler);

		TextureLoader textureLoader = TextureLoader.getInstance();
		int animDelay = 6;
		// TODO animation direction, better animation
		animation = new Animation(textureLoader.getTextures(TextureName.SwordSlashProjectile), animDelay, false);
	}

	@Override
	public void tick() {
		x += velX;
		
		animation.runAnimation();
	}

	@Override
	public void render(Graphics g) {
		animation.drawAnimation(g, (int) x, (int) y, width, height);
	}
	
}
