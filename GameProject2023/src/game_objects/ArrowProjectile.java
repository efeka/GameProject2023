package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import abstracts.Creature;
import abstracts.GameObject;
import abstracts.Projectile;
import framework.Animation;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class ArrowProjectile extends Projectile {

	private boolean stopUpdating = false;
	private float alpha = 1;
	private float fadingRate = 0.01f;
	
	private Animation arrowAnimation;
	
	private int lifetimeAfterLandingMillis = 10000;
	private long landingTime;
	private boolean landed = false;
	
	public ArrowProjectile(float x, float y, float velX, float velY, int damage, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2, velX, velY, damage, objectHandler);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		arrowAnimation = new Animation(textureLoader.getTextures(TextureName.ArrowProjectile), 5, false);
		texture = textureLoader.getTextures(TextureName.ArrowProjectile)[0];
	}

	@Override
	public void tick() {
		// If the arrow landed on the ground, wait for a certain amount
		// of time and then remove it from the game.
		if (landed) {
			if (System.currentTimeMillis() - landingTime >= lifetimeAfterLandingMillis) {
				if (alpha > fadingRate) 
					alpha -= fadingRate;
				else
					objectHandler.removeObject(this);
				return;
			}
		}
		
		if (stopUpdating)
			return;
		
		x += velX;
		y += velY;
		velY += GameConstants.PhysicsConstants.GRAVITY;
		
		checkCollisions();
		
		arrowAnimation.runAnimation();
	}
	
	private void checkCollisions() {
		// Player or player summon collision
		ArrayList<Creature> targetList = objectHandler.getSummonsList();
		for (int i = targetList.size() - 1; i >= 0; i--) {
			Creature target = targetList.get(i);
			if (getBounds().intersects(target.getBounds())) {
				target.applyKnockback(velX / 2, -3f);
				target.takeDamage(damage, target.DEFAULT_INVULNERABILITY_DURATION);
				objectHandler.removeObject(this);
			}
		}
		
		// Block collision
		List<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			if (other.equals(this))
				continue;

			if (other.getObjectId().getCategory() == ObjectId.Category.Block) {
				if (getBounds().intersects(other.getBounds())) {
					stopUpdating = true;
					
					landingTime = System.currentTimeMillis();
					landed = true;
				}
			}
		}
	}

	@Override
	public void render(Graphics g) {
	    float centerX = (float) getBounds().getCenterX();
	    float centerY = (float) getBounds().getCenterY();
	    double rotationAngle = Math.atan2(velY, velX);
	    
	    Graphics2D g2d = (Graphics2D) g;
	    g2d.rotate(rotationAngle, centerX, centerY);
	    g2d.setComposite(makeTransparent(alpha));
	    arrowAnimation.drawAnimation(g2d, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
	    g2d.setComposite(makeTransparent(1));
	    g2d.rotate(-rotationAngle, centerX, centerY);
	}
	
	private AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}

}
