package player_weapons;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import abstracts.Creature;
import abstracts.GameObject;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.DamageNumberPopup;
import game_objects.Explosion;
import ui.CreatureHealthBar;
import visual_effects.OneTimeAnimation;
import window.Animation;

public class DarkSummon extends Creature {

	private boolean summonComplete = false;

	private Animation[] summonAnimation;
	private Animation[] runAnimation;
	private Animation[] attackAnimation;
	private BufferedImage[] jumpSprites;

	private int explosionDamage;
	private float runningSpeed = 2f;
	private float jumpingSpeed = -4.5f;

	private HashSet<GameObject> enemiesHit;
	private boolean attacking = false;
	private int attackCooldown = 1000;
	private long lastAttackTimer = 0;

	public DarkSummon(int x, int y, int direction, int damage, int explosionDamage, int maxHealth, ObjectHandler objectHandler) {
		super(x, y, TILE_SIZE, TILE_SIZE, damage, maxHealth, objectHandler, new ObjectId(Category.FriendlySummon, Name.Missing));
		this.direction = direction;
		this.explosionDamage = explosionDamage;
		enemiesHit = new HashSet<>();
		setupAnimations();
	}

	@Override
	public void tick() {
		if (!summonComplete) {
			int directionIndex = direction == 1 ? 0 : 1;
			if (summonAnimation[directionIndex].isPlayedOnce() || summonAnimation[directionIndex].isPlayedOnce())
				summonComplete = true;
			else {
				summonAnimation[directionIndex].runAnimation();
				return;
			}
		}

		if (attacking && attackAnimation[0].isPlayedOnce() || attackAnimation[1].isPlayedOnce())
			resetAnimations();

		x += velX;
		y += velY;

		if (!knockedBack) {
			if (velX < 0)
				direction = -1;
			else if (velX > 0)
				direction = 1;
			
			velX = runningSpeed * direction;
		}
		
		if (falling || jumping) {
			velY += GRAVITY;

			if (velY > TERMINAL_VELOCITY)
				velY = TERMINAL_VELOCITY;
		}

		invulnerable = !(invulnerable && (System.currentTimeMillis() - lastInvulnerableTimer >= invulnerableDuration));

		handleCollisions();
		runAnimations();
		healthBar.tick();
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.white);
		g2d.draw(getBounds());
		int directionIndex = direction == 1 ? 0 : 1;
		
		final int renderWidth = (int) (width * 1.5f);
		final int renderHeight = (int) (height * 1.5f);
		final int renderX = (int) (x - (renderWidth - width) / 2);
		final int renderY = (int) (y - (renderHeight - height));
		
		if (!summonComplete)
			summonAnimation[directionIndex].drawAnimation(g, renderX, renderY, renderWidth, renderHeight);
		else if (attacking)
			attackAnimation[directionIndex].drawAnimation(g, renderX, renderY, renderWidth, renderHeight);
		else if (falling)
			g.drawImage(jumpSprites[directionIndex], renderX, renderY, renderWidth, renderHeight, null);
		else
			runAnimation[directionIndex].drawAnimation(g, renderX, renderY, renderWidth, renderHeight);
		
		healthBar.render(g);
	}

	private void handleCollisions() {
		falling = true;

		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			Rectangle otherBounds = other.getBounds();

			// Attack enemies when they get in range
			if (!knockedBack && getGroundAttackBounds().intersects(otherBounds) && other.getObjectId().getCategory() == Category.Enemy) {
				velX = 0;
				if (x > other.getX())
					direction = -1;
				else if (x < other.getX())
					direction = 1;

				if (isAttackReady()) {
					int currentAnimFrame = direction == 1 ? attackAnimation[0].getCurrentFrame() : attackAnimation[1].getCurrentFrame();
					if (!enemiesHit.contains(other) && attacking && currentAnimFrame == 3) {
						((Creature) other).takeDamage(damage, 0);
						enemiesHit.add(other);
					}
					else
						attacking = true;
				}
			}

			// Collision with Blocks
			if (other.getObjectId().getCategory() == Category.Block ||
					(other.getObjectId().getCategory() == Category.JumpThroughBlock && getVelY() > 0)) {

				if (getGroundCheckBounds().intersects(otherBounds))
					falling = false;

				// Attempt to jump over blocks when they get in a certain range
				if (!jumping && !falling && getJumpCheckBounds().intersects(otherBounds)) {
					jumping = true;
					velY = jumpingSpeed;
				}

				// Bottom collision
				if (getBottomBounds().intersects(otherBounds)) {
					y = other.getY() - height;
					velY = 0;
					jumping = false;

					// Reset knock back status after hitting the ground
					if (knockedBack) {
						knockedBack = false;
						velX = 0;
					}
				}

				// Horizontal collision
				if (getHorizontalBounds().intersects(otherBounds)) {
					int xDiff = (int) (x - other.getX());
					// Player is to the left of the object
					if (xDiff < 0)
						x = other.getX() - getHorizontalBounds().width;
					// Player is to the right of the object
					else
						x = other.getX() + other.getWidth();

					velX *= -1;
				}

				// Top collision
				if (getTopBounds().intersects(otherBounds)) {
					y = other.getY() + other.getHeight();
					velY = 0;
				}
			}
		}
	}

	@Override
	public void takeDamage(int damageAmount, int invulnerabilityDuration) {
		if (invulnerable)
			return;
		invulnerableDuration = invulnerabilityDuration; 

		if (invulnerableDuration != 0) {
			lastInvulnerableTimer = System.currentTimeMillis();
			invulnerable = true;
		}

		setHealth(health - damageAmount);
		objectHandler.addObject(new DamageNumberPopup(x + width / 3, y - height / 5, damageAmount, objectHandler), ObjectHandler.MENU_LAYER);

		if (health <= 0)
			die();
	}

	@Override
	public void applyKnockback(float velX, float velY) {
		if (invulnerable)
			return;

		resetAnimations();

		knockedBack = true;
		this.velX = velX;
		this.velY = velY;
	}

	@Override
	public void die() {
		objectHandler.removeObject(this);

		int smokeSize = (int) (TILE_SIZE * 2f);
		int smokeX = (int) ((x + width / 2) - smokeSize / 2);
		int smokeY = (int) ((y + height / 2) - smokeSize / 2);
		OneTimeAnimation darkSmokeAnimation = new OneTimeAnimation(smokeX, smokeY, smokeSize, smokeSize,
				TextureName.DarkSmokeEffect, 8, objectHandler);
		Explosion explosion = new Explosion(darkSmokeAnimation, explosionDamage, 
				new Category[] {Category.Enemy}, objectHandler);
		objectHandler.addObject(explosion, ObjectHandler.MIDDLE_LAYER);
	}

	private Rectangle getJumpCheckBounds() {
		int boundsWidth = (int) (width);
		int boundsX;
		if (direction == 1)
			boundsX = (int) (x + width);
		else
			boundsX = (int) (x - boundsWidth);

		return new Rectangle(boundsX, (int) y, boundsWidth, height);
	}

	@Override
	protected Rectangle getGroundAttackBounds() {
		int boundsWidth = (int) (width * 1.1f);
		int boundsX = (int) (x + (width - boundsWidth) / 2);
		return new Rectangle(boundsX, (int) y, boundsWidth, height);
	}

	private void setupAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();
		int summonDelay = 6;
		summonAnimation = new Animation[] {
			new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDarkSummonSpawn, 1), summonDelay, true),
			new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDarkSummonSpawn, -1), summonDelay, true),
		};

		int runDelay = 8;
		runAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDarkSummonRun, 1), runDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDarkSummonRun, -1), runDelay, false),
		};

		int attackDelay = 8;
		attackAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDarkSummonAttack, 1), attackDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDarkSummonAttack, -1), attackDelay, true),	
		};

		jumpSprites = new BufferedImage[] {
				textureLoader.getTextures(TextureName.PlayerDarkSummonRun)[1],
				textureLoader.getTextures(TextureName.PlayerDarkSummonRun)[9],
		};
	}

	private void runAnimations() {
		int directionIndex = direction == 1 ? 0 : 1;
		if (velX != 0)
			runAnimation[directionIndex].runAnimation();
		if (attacking)
			attackAnimation[directionIndex].runAnimation();
	}

	private boolean isAttackReady() {
		return System.currentTimeMillis() - lastAttackTimer >= attackCooldown;
	}

	private void resetAnimations() {
		attacking = false;
		lastAttackTimer = System.currentTimeMillis();
		enemiesHit.clear();
		attackAnimation[0].resetAnimation();
		attackAnimation[1].resetAnimation();
		runAnimation[0].resetAnimation();
		runAnimation[1].resetAnimation();
	}
	
	@Override
	public void setupHealthBar() {
		healthBar = new CreatureHealthBar(this, -GameConstants.ScaleConstants.TILE_SIZE / 3);
	}

}
