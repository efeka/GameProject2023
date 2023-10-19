package player_weapons.abilities;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import abstracts.GameObject;
import framework.Animation;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class ChainSpikeAttack extends GameObject {

	private ObjectHandler objectHandler;
	private Animation spikeAnimation;

	private boolean isOnBlock = false;
	private boolean isInsideBlock = false;

	private int direction;
	private boolean spawnedNextSpike = false;

	public ChainSpikeAttack(float x, float y, int direction, ObjectHandler objectHandler) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(Category.Missing, Name.Missing));
		this.objectHandler = objectHandler;
		this.direction = direction;

		checkBlockCollisions();

		if (isOnBlock)
			spikeAnimation = new Animation(TextureLoader.getInstance().getTextures(TextureName.SpikeAttack), 5, true);
		else
			objectHandler.removeObject(this);
	}

	@Override
	public void tick() {
		if (!isOnBlock)
			return;
		if (spikeAnimation.isPlayedOnce())
			objectHandler.removeObject(this);

		if (spikeAnimation.getCurrentFrame() == 2 && !spawnedNextSpike) {
			spawnedNextSpike = true;

			if (!isInsideBlock)
				objectHandler.addObject(new ChainSpikeAttack(x + width * direction, y, direction, objectHandler),
						ObjectHandler.MIDDLE_LAYER);
		}

		spikeAnimation.runAnimation();
	}

	@Override
	public void render(Graphics g) {
		if (!isOnBlock)
			return;
		spikeAnimation.drawAnimation(g, (int) x, (int) y, width, height);
	}

	private void checkBlockCollisions() {
		List<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);

			if (other.compareCategory(Category.Block)) {
				if (getGroundCheckBounds().intersects(other.getBounds())) {
					isOnBlock = true;
					x = other.getX();
				}
				else if (getBounds().intersects(other.getBounds()))
					isInsideBlock = true;
			}	
		}
	}

	private Rectangle getGroundCheckBounds() {
		int width = this.width / 2;
		int height = this.height / 2;
		int x = (int) (this.x + width / 2);
		int y = (int) (this.y + this.height);
		return new Rectangle(x, y, width, height);
	}

}
