package abstracts;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import framework.Animation;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import window.KeyInput;
import window.MouseInput;

public abstract class Weapon extends GameObject {

	protected ObjectHandler objectHandler;
	protected KeyInput keyInput;
	protected MouseInput mouseInput;
	
	protected Animation[] idleAnimation = new Animation[2];
	protected Animation[] runAnimation = new Animation[2];
	protected BufferedImage[] jumpSprites;
	
	public Weapon(ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(0, 0, 0, 0, new ObjectId(Category.Missing, Name.Missing));
		this.objectHandler = objectHandler;
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		
		setupBaseAnimations();
	}
	
	public abstract void tick();

	@Override
	public void render(Graphics g) {}
	
	/**
	 * Checks whether an ability of this weapon is being used or not.
	 * @return true if an ability is currently being used.
	 */
	public abstract boolean isUsingAbility();
	
	public abstract Animation getCurrentAnimation();
	
	/**
	 * Setup the base animations and textures:
	 * 	Idle
	 * 	Run
	 * 	Jump
	 * 	TODO double jump
	 * 	TODO dodge roll	
	 */
	protected abstract void setupBaseAnimations();
	
	protected void checkEnemyCollision(Rectangle attackBounds, HashSet<GameObject> enemiesHit, int damage, float knockbackVelX,
			float knockbackVelY, int enemyInvulnerabilityDuration) {
		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);

			if (enemiesHit != null && enemiesHit.contains(other))
				continue;

			if (attackBounds.intersects(other.getBounds()) && other.getObjectId().getCategory() == Category.Enemy) {
				Creature otherCreature = (Creature) other;
				if (knockbackVelX != 0 || knockbackVelY != 0)
					otherCreature.applyKnockback(knockbackVelX, knockbackVelY);
				otherCreature.takeDamage(damage, enemyInvulnerabilityDuration);

				if (enemiesHit != null)
					enemiesHit.add(other);
			}
		}
	}
	
	public Animation[] getIdleAnimation() {
		return idleAnimation;
	}
	
	public Animation[] getRunAnimation() {
		return runAnimation;
	}
	
	public BufferedImage[] getJumpSprites() {
		return jumpSprites;
	}

}
