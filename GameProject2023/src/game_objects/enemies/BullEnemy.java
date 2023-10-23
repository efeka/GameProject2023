package game_objects.enemies;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import abstracts.Creature;
import abstracts.GameObject;
import framework.Animation;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.CreatureAnimationManager.AnimationType;
import main.Game;
import ui.CreatureHealthBar;
import visual_effects.DamageNumberPopup;

public class BullEnemy extends Creature {
	
	private ObjectHandler objectHandler;

	private float runningSpeed = 3f;
	private boolean stunnedByWall = false;

	public BullEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 25, 150, objectHandler, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BullEnemy));		
		this.objectHandler = objectHandler;

		velX = -runningSpeed;
		
		texture = TextureLoader.getInstance().getTextures(TextureName.BullEnemyRun)[0];
		setupAnimations();
	}

	@Override
	public void tick() {
		if (!animationManager.isAnimationPlayedOnce(AnimationType.Spawn)) {
			animationManager.runAnimation(AnimationType.Spawn);
			return;
		}
		
		x += velX;
		y += velY;

		if (falling || jumping) {
			velY += GRAVITY;

			if (velY > TERMINAL_VELOCITY)
				velY = TERMINAL_VELOCITY;
		}

		if (!stunnedByWall)
			direction = velX < 0 ? -1 : 1;
		else {
			// Get rid of the stun if the stun animation is finished.
			if (animationManager.isAnimationPlayedOnce(AnimationType.Stun)) {
				stunnedByWall = false;
				direction *= -1;
			}
		}
		
		if (invulnerable && (System.currentTimeMillis() - lastInvulnerableTimer >= invulnerabilityDuration))
			invulnerable = false;
		
		if (!stunnedByWall)
			handleAttacking();
		handleBlockCollision();
		
		runAnimations();
		healthBar.tick();
	}
	
	@Override
	public void render(Graphics g) {
		drawAnimations(g);
		healthBar.render(g);
	}

	private void handleAttacking() {
		ArrayList<Creature> targetList = objectHandler.getSummonsList();
		for (int i = targetList.size() - 1; i >= 0; i--) {
			Creature target = targetList.get(i);
			if (getBounds().intersects(target.getBounds())) {
				target.applyKnockback(direction * runningSpeed / 2, -7f);
				target.takeDamage(25, DEFAULT_INVULNERABILITY_DURATION);
			}
		}
	}
	
	private void handleBlockCollision() {
		if (!stunnedByWall)
			velX = direction * runningSpeed;
		
		for (GameObject other : objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER)) {
			// Collision with Blocks
			if (other.compareCategory(Category.Block)) {
				Rectangle otherBounds = other.getBounds();

				// Bottom collision
				if (getBottomBounds().intersects(otherBounds)) {
					y = other.getY() - height;
					velY = 0;
					falling = false;
					jumping = false;
				}
				else
					falling = true;
				
				if (!stunnedByWall && getHorizontalBounds().intersects(otherBounds)) {
					// If the both the head and the body hit a wall, get stunned.
					if (getWallCollisionBounds().intersects(otherBounds)) {
						stunnedByWall = true;
						Game.shakeCamera(15, 10);
						animationManager.resetAnimation(AnimationType.Stun);
						velX = 0;
					}
					// If only the body hits a wall, turn back and keep running. 
					else {
						direction = -direction;
						velX = direction * runningSpeed;
					}
				}
			}
		}
	}
	
	@Override
	public void takeDamage(int damageAmount, int invulnerabilityDuration) {
		if (invulnerable)
			return;
		 
		this.invulnerabilityDuration = invulnerabilityDuration;
		if (invulnerabilityDuration != 0) {
			lastInvulnerableTimer = System.currentTimeMillis();
			invulnerable = true;
		}
		
		setHealth(health - damageAmount);
		objectHandler.addObject(new DamageNumberPopup(x + width / 3, y - height / 5, damageAmount, objectHandler), ObjectHandler.MENU_LAYER);
		
		if (health <= 0)
			die();
	}
	
	// This enemy cannot be knocked back.
	@Override
	public void applyKnockback(float velX, float velY) {}
	
	private Rectangle getWallCollisionBounds() {
		int boundsWidth = width / 2;
		int boundsHeight = height / 4;
		int boundsY = (int) y - boundsHeight;
		int boundsX;
		if (direction == 1)
			boundsX = (int) (x + width);
		else
			boundsX = (int) (x - boundsWidth);
		
		return new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);
	}

	private void setupAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();

		final int runDelay = 13;
		Animation[] runAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BullEnemyRun, 1),
						runDelay, false),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BullEnemyRun, -1),
						runDelay, false),
		};
		animationManager.addAnimation(AnimationType.Run, runAnimation);
		
		final int stunDelay = 12;
		Animation[] stunAnimation = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.BullEnemyStunned, 1),
						stunDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.BullEnemyStunned, -1),
						stunDelay, true),
		};
		animationManager.addAnimation(AnimationType.Stun, stunAnimation);
	}
	
	private void runAnimations() {
		if (!stunnedByWall)
			animationManager.runAnimation(AnimationType.Run);
		else
			animationManager.runAnimation(AnimationType.Stun);
	}

	private void drawAnimations(Graphics g) {
		final int x = (int) this.x - width / 2;
		final int y = (int) this.y - height / 2;
		final int width = this.width * 2;
		final int height = this.height * 2;
		
		if (animationManager.getCurrentAnimationFrame(AnimationType.Spawn) >= 13) {
			if (!stunnedByWall)
				animationManager.drawAnimation(AnimationType.Run, g, direction, invulnerable,
						 x, y, width, height);
			else
				animationManager.drawAnimation(AnimationType.Stun, g, direction, invulnerable,
						 x, y, width, height);
		}
		
		if (!animationManager.isAnimationPlayedOnce(AnimationType.Spawn)) {
			int spawnWidth = (int) (width * 0.8f);
			int spawnHeight = (int) (height * 0.8f);
			int spawnX = (int) (x + (width - spawnWidth) / 2);
			int spawnY = (int) (y + (height - spawnHeight) / 2); 
			animationManager.drawAnimation(AnimationType.Spawn, g, 1, invulnerable,
					 spawnX, spawnY, spawnWidth, spawnHeight);
		}
	}

	public int getIndexFromDirection() {
		return (-direction + 1) / 2;
	}
	
	@Override
	public void setupHealthBar() {
		healthBar = new CreatureHealthBar(this, -GameConstants.ScaleConstants.TILE_SIZE / 2);
	}

}
