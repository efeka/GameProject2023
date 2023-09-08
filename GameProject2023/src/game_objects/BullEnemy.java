package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Graphics;
import java.awt.Rectangle;

import abstract_templates.Creature;
import abstract_templates.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import main.Game;
import window.Animation;

public class BullEnemy extends Creature {
	
	private ObjectHandler objectHandler;
	private Player player;

	private float runningSpeed = 3f;

	private boolean stunnedByWall = false;
	
	private Animation[] runAnimation;
	private Animation[] stunAnimation;
	
	public BullEnemy(int x, int y, ObjectHandler objectHandler) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 25, 150, 70, objectHandler, new ObjectId(ObjectId.Category.Enemy, ObjectId.Name.BullEnemy));		
		this.objectHandler = objectHandler;
		player = objectHandler.getPlayer();

		velX = -runningSpeed;
		
		texture = TextureLoader.getInstance().getTextures(TextureName.BullEnemyRun)[0];
		setupAnimations();
	}

	@Override
	public void tick() {
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
			if (stunAnimation[getIndexFromDirection()].isPlayedOnce()) {
				stunnedByWall = false;
				direction *= -1;
			}
		}
		
		if (invulnerable && (System.currentTimeMillis() - lastInvulnerableTimer >= invulnerableDuration))
			invulnerable = false;
		
		if (!stunnedByWall)
			handleAttacking();
		handleBlockCollision();
		
		runAnimations();
	}
	
	@Override
	public void render(Graphics g) {
		drawAnimations(g);
	}

	private void handleAttacking() {
		if (getBounds().intersects(player.getBounds())) {
			player.takeDamage(25, player.getDefaultInvulnerabilityDuration());
			player.applyKnockback(direction * runningSpeed / 2, -7f);
		}
	}
	
	private void handleBlockCollision() {
		if (!stunnedByWall)
			velX = direction * runningSpeed;
		
		for (GameObject other : objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER)) {
			// Collision with Blocks
			if (other.getObjectId().getCategory() == Category.Block) {
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
						resetStunAnimations();
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
		runAnimation = new Animation[2];
		runAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.BullEnemyRun, 1),
				runDelay, false);
		runAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.BullEnemyRun, -1),
				runDelay, false);
		
		final int stuckDelay = 12;
		stunAnimation = new Animation[2];
		stunAnimation[0] = new Animation(textureLoader.getTexturesByDirection(TextureName.BullEnemyStunned, 1),
				stuckDelay, true);
		stunAnimation[1] = new Animation(textureLoader.getTexturesByDirection(TextureName.BullEnemyStunned, -1),
				stuckDelay, true);
	}
	
	private void runAnimations() {
		int directionToIndex = getIndexFromDirection();
		
		if (!stunnedByWall)
			runAnimation[directionToIndex].runAnimation();
		else
			stunAnimation[directionToIndex].runAnimation();
	}

	private void drawAnimations(Graphics g) {
		int directionToIndex = getIndexFromDirection();
		
		if (!stunnedByWall)
			runAnimation[directionToIndex].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
		else
			stunAnimation[directionToIndex].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
	}
	
	private void resetStunAnimations() {
		stunAnimation[0].resetAnimation();
		stunAnimation[1].resetAnimation();
	}
	
	public int getIndexFromDirection() {
		return (-direction + 1) / 2;
	}

}
