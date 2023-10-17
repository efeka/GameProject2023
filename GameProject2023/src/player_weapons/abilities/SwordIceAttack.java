package player_weapons.abilities;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import abstracts.Creature;
import abstracts.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class SwordIceAttack extends GameObject {

	private final float TWO_PI = (float) (2 * Math.PI);

	private ObjectHandler objectHandler;
	private GameObject centerObject;

	private int damage;
	private int swordCount;
	private int radius;
	private float angleIncrement;
	private float[] swordAngles;
	
	private int durationMillis;
	private long startTime;
	
	// Enemies hit by each sword is stored in this list.
	// This is for preventing an enemy to get hit twice by the same sword in the same rotation.
	private ArrayList<HashSet<GameObject>> enemiesHit;

	/**
	 * Creates swords that revolve around the given GameObject.
	 * The swords deal damage in intervals calculated from {@code swordCount} and {@code angleIncrement}.
	 * 
	 * @param centerObject the object which the swords will revolve around
	 * @param damage the damage that the swords will deal to enemies
	 * @param duarionMillis the duration for how long the swords will stay active
	 * @param swordCount the total number of swords in the sword circle
	 * @param sizeScale the scale that will be multiplied by TILE_SIZE to determine the size of each sword
	 * @param radius the radius of the sword circle
	 * @param angleIncrement the angle that each sword will be rotated by after every tick
	 * @param objectHandler reference to the ObjectHandler
	 */
	public SwordIceAttack(GameObject centerObject, int damage, int durationMillis, int swordCount, float sizeScale, 
			int radius, float angleIncrement, ObjectHandler objectHandler) {
		super(0, 0, (int) (TILE_SIZE * sizeScale), (int) (TILE_SIZE * sizeScale),
				new ObjectId(Category.Missing, Name.Missing));
		this.centerObject = centerObject;
		this.swordCount = swordCount;
		this.damage = damage;
		this.durationMillis = durationMillis;
		this.radius = radius;
		this.angleIncrement = angleIncrement;
		this.objectHandler = objectHandler;
		
		startTime = System.currentTimeMillis();
		
		enemiesHit = new ArrayList<>();
		for (int i = 0; i < swordCount; i++)
			enemiesHit.add(new HashSet<GameObject>());
		
		swordAngles = new float[swordCount];
		for (int i = 0; i < swordCount; i++)
			swordAngles[i] = i * (TWO_PI / swordCount);
		
		texture = TextureLoader.getInstance().getTextures(TextureName.IceSword)[0];
	}

	@Override
	public void tick() {
		if (System.currentTimeMillis() - startTime >= durationMillis) {
			objectHandler.removeObject(this);
			return;
		}
		
		x = (float) centerObject.getBounds().getCenterX() - width / 2;
		y = (float) centerObject.getBounds().getCenterY() - height / 2 - radius;
		
		for (int i = 0; i < swordCount; i++) {
			// Calculate the angle of rotation for each sword
			swordAngles[i] = (float) ((swordAngles[i] + angleIncrement) % TWO_PI);
			
			// Reset the "hit-enemies set" if a sword completed a full rotation
			if (swordAngles[i] <= 0.1f)
				enemiesHit.get(i).clear();
		}
		
		checkEnemyCollision();
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		float centerX = (float) centerObject.getBounds().getCenterX();
		float centerY = (float) centerObject.getBounds().getCenterY();
		for (int i = 0; i < swordCount; i++) {
			g2d.rotate(swordAngles[i], centerX, centerY);
			g.drawImage(texture, (int) x, (int) y, width, height, null);
			g2d.rotate(-swordAngles[i], centerX, centerY);
		}
	}
	
	private void checkEnemyCollision() {
		List<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			
			if (other.getObjectId().getCategory() == Category.Enemy) {
				Rectangle[] swordBounds = getSwordBounds();
				for (int j = 0; j < swordCount; j++) {
					Rectangle bounds = swordBounds[j];
					if (!enemiesHit.get(j).contains(other) && bounds.intersects(other.getBounds())) {
						enemiesHit.get(j).add(other);
						Creature otherCreature = (Creature) other;
						otherCreature.takeDamage(damage, 0);
					}
				}
			}
		}
	}

	public Rectangle[] getSwordBounds() {
		Rectangle[] bounds = new Rectangle[swordCount];
		float centerX = (float) centerObject.getBounds().getCenterX();
		float centerY = (float) centerObject.getBounds().getCenterY();
		
		for (int i = 0; i < swordCount; i++) {
			float xx = (float) (centerX + radius * Math.cos(swordAngles[i] - TWO_PI / 4) - width / 2);
			float yy = (float) (centerY + radius * Math.sin(swordAngles[i] - TWO_PI / 4) - height / 2);
			bounds[i] = new Rectangle((int) xx, (int) yy, width, height);
		}	
		return bounds;
	}
	
	@Override
	public Rectangle getBounds() {
		int w = centerObject.getWidth() + radius + width;
		int h = centerObject.getHeight() + radius + height;
		int xx = (int) (centerObject.getX() - radius / 2 - width / 2);
		int yy = (int) (centerObject.getY() - radius / 2 - height / 2);
		return new Rectangle(xx, yy, w, h);
	}

}
