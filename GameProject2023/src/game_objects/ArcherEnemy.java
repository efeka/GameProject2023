package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Color;
import java.awt.Graphics;

import abstract_objects.Creature;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class ArcherEnemy extends Creature {

	private ObjectHandler objectHandler;

	private final int shootCooldown = 2000;
	private long lastShotTimer = shootCooldown;

	public ArcherEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 25, 100, 70, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BasicEnemy));		
		this.objectHandler = objectHandler;

		invulnerableDuration = 700;

		texture = TextureLoader.getInstance().getTextures(TextureName.BasicEnemyIdle)[0];
	}

	@Override
	public void takeDamage(int damageAmount) {}

	@Override
	public void applyKnockback(float velX, float velY) {}

	@Override
	public void tick() {
		if (System.currentTimeMillis() - lastShotTimer >= shootCooldown) {
			lastShotTimer = System.currentTimeMillis();
			shootArrow(7f);
		}
	}

	private void shootArrow(float velX) {
		Player player = objectHandler.getPlayer();
		float distanceX = (float) player.getBounds().getCenterX() - x;
		float distanceY = (float) player.getBounds().getCenterY() - y;

		if (distanceX < 0)
			velX = -velX;

		float timeToTargetX = distanceX / velX;
		velX = distanceX / timeToTargetX;
		velY = (distanceY - 0.5f * GRAVITY * timeToTargetX * timeToTargetX) / timeToTargetX;

		objectHandler.addObject(new ArrowProjectile(x, y, velX, velY, 15, objectHandler), ObjectHandler.MIDDLE_LAYER);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect((int) x, (int) y, width, height);
	}

}
