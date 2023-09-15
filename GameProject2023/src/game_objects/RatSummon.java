package game_objects;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstract_templates.Creature;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import visual_effects.PoisonSmokeEffect;
import window.Animation;

public class RatSummon extends Creature {

	private boolean summonComplete = false;

	private Animation summoningAnimation;
	private Animation[] runAnimation;
	private BufferedImage[] jumpSprites;

	// TODO temp
	private int explodeTimer = 300;
	private int jumpTimer = 1500;
	private boolean jumped = false;
	private long startTime;

	public RatSummon(int x, int y, int damage, int maxHealth, ObjectHandler objectHandler) {
		super(x, y, TILE_SIZE, TILE_SIZE, damage, maxHealth, 0, objectHandler, new ObjectId(Category.Missing, Name.Missing));

		velX = 2f;

		TextureLoader textureLoader = TextureLoader.getInstance();
		int summonDelay = 6;
		summoningAnimation = new Animation(textureLoader.getTextures(TextureName.PoisonRatSummon), summonDelay, true);
		int runDelay = 6;
		runAnimation = new Animation[] {
			new Animation(textureLoader.getTexturesByDirection(TextureName.PoisonRatRun, 1), runDelay, false),
			new Animation(textureLoader.getTexturesByDirection(TextureName.PoisonRatRun, -1), runDelay, false),
		};
		
		jumpSprites = new BufferedImage[] {
			textureLoader.getTextures(TextureName.PoisonRatRun)[2],
			textureLoader.getTextures(TextureName.PoisonRatRun)[6],
		};
	}

	@Override
	public void tick() {
		if (!summonComplete) {
			if (summoningAnimation.isPlayedOnce()) {
				summonComplete = true;
				startTime = System.currentTimeMillis();
			}
			else {
				summoningAnimation.runAnimation();
				return;
			}
		}

		// TODO Temp. Walk for some time, then jump.
		if (!jumped && System.currentTimeMillis() - startTime >= jumpTimer) {
			jumped = true;
			startTime = System.currentTimeMillis();
			velY = -7f;
		}
		// TODO Temp. Explode some time after jumping.
		if (jumped && System.currentTimeMillis() - startTime >= explodeTimer) {
			objectHandler.removeObject(this);

			int smokeSize = (int) (TILE_SIZE * 2f);
			int smokeX = (int) ((x + width / 2) - smokeSize / 2);
			int smokeY = (int) ((y + height / 2) - smokeSize / 2);
			objectHandler.addObject(new PoisonSmokeEffect(smokeX, smokeY, smokeSize, smokeSize, objectHandler), ObjectHandler.MIDDLE_LAYER);
		}
		
		x += velX;
		y += velY;
		if (velX < 0)
			direction = -1;
		else if (velX > 0)
			direction = 1;
		
		if (falling || jumping) {
			velY += GRAVITY;

			if (velY > TERMINAL_VELOCITY)
				velY = TERMINAL_VELOCITY;
		}

		if (invulnerable && (System.currentTimeMillis() - lastInvulnerableTimer >= invulnerableDuration))
			invulnerable = false;

		basicBlockCollision(objectHandler);
		runAnimations();
	}

	@Override
	public void render(Graphics g) {
		if (!summonComplete)
			summoningAnimation.drawAnimation(g, (int) x, (int) y, width, height);
		else if (jumped) {
			int directionIndex = direction == 1 ? 0 : 1;
			g.drawImage(jumpSprites[directionIndex], (int) x, (int) y, width, height, null);
		}
		else {
			int directionIndex = direction == 1 ? 0 : 1;
			runAnimation[directionIndex].drawAnimation(g, (int) x, (int) y, width, height);
		}
	}

	@Override
	public void takeDamage(int damageAmount, int invulnerabilityDuration) {}

	@Override
	public void applyKnockback(float velX, float velY) {}

	@Override
	public void die() {}
	
	private void runAnimations() {
		int directionIndex = direction == 1 ? 0 : 1;
		runAnimation[directionIndex].runAnimation();
	}

}
