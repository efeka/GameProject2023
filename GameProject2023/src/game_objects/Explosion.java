package game_objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;

import abstracts.Creature;
import abstracts.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import visual_effects.OneTimeAnimation;

public class Explosion extends GameObject {

	private ObjectHandler objectHandler;
	private OneTimeAnimation oneTimeAnimation;
	private int[] animationDamageFrames;
	
	private Category[] targets;
	private HashSet<GameObject> targetsHit;
	private int damage;
	
	/**
	 * Creates an explosion with the given one time animation.
	 * Only damages objects within the given categories.
	 * @param oneTimeAnimation the OneTimeAnimation to be played for the explosion.
	 *  	The explosion's coordinates and dimensions are inferred from this object.
	 * @param damage the damage that the explosion will deal to its targets.
	 * @param targetCategories the categories of the objects that will be effected by the explosion.
	 * @param objectHandler reference to the ObjectHandler.
	 */
	public Explosion(OneTimeAnimation oneTimeAnimation, int damage, Category[] targetCategories, ObjectHandler objectHandler) {
		super(oneTimeAnimation.getX(), oneTimeAnimation.getY(), oneTimeAnimation.getWidth(), oneTimeAnimation.getHeight(),
				new ObjectId(Category.Missing, Name.Missing));
		this.objectHandler = objectHandler;
		this.targets = targetCategories;
		this.damage = damage;
		this.oneTimeAnimation = oneTimeAnimation;
		targetsHit = new HashSet<>();		
		objectHandler.addObject(oneTimeAnimation, ObjectHandler.TOP_LAYER);
	}
	
	/**
	 * Creates an explosion with the given one time animation.
	 * Only damages objects within the given categories.
	 * @param oneTimeAnimation the OneTimeAnimation to be played for the explosion.
	 *  	The explosion's coordinates and dimensions are inferred from this object.
	 * @param animationDamageFrames the indices of the animation where this explosion will deal damage at.
	 * @param damage the damage that the explosion will deal to its targets.
	 * @param targetCategories the categories of the objects that will be effected by the explosion.
	 * @param objectHandler reference to the ObjectHandler.
	 */
	public Explosion(OneTimeAnimation oneTimeAnimation, int[] animationDamageFrames,
			int damage, Category[] targetCategories, ObjectHandler objectHandler) {
		super(oneTimeAnimation.getX(), oneTimeAnimation.getY(), oneTimeAnimation.getWidth(), oneTimeAnimation.getHeight(),
				new ObjectId(Category.Missing, Name.Missing));
		this.objectHandler = objectHandler;
		this.targets = targetCategories;
		this.damage = damage;
		this.oneTimeAnimation = oneTimeAnimation;
		this.animationDamageFrames = animationDamageFrames;
		targetsHit = new HashSet<>();		
		objectHandler.addObject(oneTimeAnimation, ObjectHandler.TOP_LAYER);
	}

	@Override
	public void tick() {
		if (oneTimeAnimation.isFinished()) {
			objectHandler.removeObject(this);
			return;
		}
		
		// Check the animations current frame.
		// If the current frame is not in the animationDamageFrame array, do not check collisions
		// with targets to avoid dealing damage at the wrong time of the animation.
		if (animationDamageFrames != null) {
			int currentAnimFrame = oneTimeAnimation.getAnimation().getCurrentFrame();
			boolean canDoDamage = false; 
			for (Integer i : animationDamageFrames)
				if (i == currentAnimFrame)
					canDoDamage = true;
			
			if (!canDoDamage)
				return;
		}
		
		handleExplosionCollisions();
	}
	
	private void handleExplosionCollisions() {
		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			for (Category category : targets) {
				// Make sure a target can only be hit once
				if (targetsHit.contains(other))
					continue;
				
				// Damage objects with the given category
				if (other.getObjectId().getCategory() == category) {
					if (getBounds().intersects(other.getBounds())) {
						((Creature) other).takeDamage(damage, 0);
						targetsHit.add(other);
					}
				}
			}
		}
	}

	@Override
	public void render(Graphics g) {}

}
