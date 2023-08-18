package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import framework.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import object_templates.Creature;
import object_templates.DiagonalTileBlock;
import object_templates.PlayerData;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

public class Player extends Creature {

	private PlayerData playerData;

	private ObjectHandler objectHandler;
	private KeyInput keyInput;
	MouseInput mouseInput;

	private float runningSpeed = 3f;
	private float jumpingSpeed = -8f;

	private Animation[] idleAnimation;
	private Animation[] runAnimation;
	private Animation[] attackAnimation;

	private int attackCooldown = 500;
	private long lastAttackTimer = attackCooldown;

	public Player(int x, int y, PlayerData playerData, ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, new ObjectId(Category.Player, Name.Player));
		this.playerData = playerData;
		this.objectHandler = objectHandler;
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;

		texture = TextureLoader.getInstance().playerRunIdleSprites[0];
		setupAnimations();
	}

	@Override
	public void tick() {
		playerData.regenerateStamina();

		x += velX;
		y += velY;

		if (falling || jumping) {
			velY += GRAVITY;

			if (velY > TERMINAL_VELOCITY)
				velY = TERMINAL_VELOCITY;
		}

		handleMovement();
		handleAttacking();
		handleCollision();

		runAnimations();
	}

	@Override
	public void render(Graphics g) {
		drawAnimations(g);
	}

	// Use the keyboard inputs of the user to move the player
	private void handleMovement() {
		if (velX > 0)
			direction = 1;
		else if (velX < 0)
			direction = -1;

		if (attacking) {
			velX = 0;
			return;
		}
		
		// Horizontal movement
		boolean rightPressed = keyInput.isMoveRightKeyPressed();
		boolean leftPressed = keyInput.isMoveLeftKeyPressed();

		if (rightPressed && !leftPressed)
			velX = runningSpeed;
		else if (leftPressed && !rightPressed)
			velX = -runningSpeed;
		else if ((rightPressed && leftPressed) || (!rightPressed && !leftPressed))
			velX = 0;

		// Vertical movement
		if (!jumping && keyInput.isJumpKeyPressed()) {
			velY = jumpingSpeed;
			jumping = true;
		}
	}

	private void handleAttacking() {
		if (attacking && (attackAnimation[0].isPlayedOnce() || attackAnimation[1].isPlayedOnce())) {
			attacking = false;
			
			attackAnimation[0].resetAnimation();
			attackAnimation[1].resetAnimation();
		}

		if (mouseInput.isAttackButtonPressed()) {
			if (System.currentTimeMillis() - lastAttackTimer >= attackCooldown) {
				attacking = true;
				lastAttackTimer = System.currentTimeMillis();
			}
		}
	}

	private void handleCollision() {
		for (GameObject other : objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER)) {
			// Collision with Blocks
			if (other.getObjectId().getCategory() == Category.Block)
				checkBlockCollision(other);
			if (other.getObjectId().getCategory() == Category.DiagonalBlock)
				checkDiagonalBlockCollision(other);
		}
	}

	private void checkBlockCollision(GameObject other) {
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

		// Horizontal collision
		if (getHorizontalBounds().intersects(otherBounds)) {
			if (velX > 0) 
				x = other.getX() - width;
			else if (velX < 0)
				x = other.getX() + other.getWidth();
			else {
				int xDiff = (int) x - (int) other.getX();
				if (xDiff < 0)
					x = other.getX() - width;
				else
					x = other.getX() + other.getWidth();
			}

		}

		// Top collision
		if (getTopBounds().intersects(otherBounds)) {
			y = other.getY() + other.getHeight();
			velY = 0;
		}
	}

	// TODO currently only works for the topleft diagonal
	private void checkDiagonalBlockCollision(GameObject other) {
		DiagonalTileBlock otherObj = (DiagonalTileBlock) other;
		Rectangle otherBounds = otherObj.getBounds();
		Rectangle bottomBounds = getBottomBounds();

		// Bottom collision
		if (bottomBounds.intersects(otherBounds)) {
			int bottomBoundsCollisionX = bottomBounds.x + bottomBounds.width;
			int bottomBoundsCollisionY = bottomBounds.y + bottomBounds.height;

			if (bottomBoundsCollisionX > otherBounds.x + otherBounds.width)
				return;

			// Output of f(x) = x function, where x is the collision point
			int diagonalDistance = bottomBoundsCollisionX - otherBounds.x;
			int diagonalCollisionY = otherBounds.y + otherBounds.height - diagonalDistance;
			int heightDiff = diagonalCollisionY - bottomBoundsCollisionY;

			y += heightDiff - 10;

			velY = 0;
			falling = false;
			jumping = false;
		}
		else
			falling = true;
	}

	// TODO Make sure collision is consistent in different game resolutions
	private Rectangle getHorizontalBounds() {
		float height = 3 * this.height / 5f;
		float yOffset = this.height / 5f; 
		return new Rectangle((int) (x + velX), (int) (y + yOffset), width, (int) height);
	}

	private Rectangle getTopBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		return new Rectangle((int) (x + xOffset), (int) y, (int) width, (int) height);
	}

	private Rectangle getBottomBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		float yOffset = 4 * this.height / 5f;
		return new Rectangle((int) (x + xOffset), (int) (y + yOffset), (int) width, (int) height);
	}

	private void setupAnimations() {
		idleAnimation = new Animation[2];
		runAnimation = new Animation[2];
		attackAnimation = new Animation[2];

		BufferedImage[] sprites = TextureLoader.getInstance().playerRunIdleSprites;
		BufferedImage[] attackSprites = TextureLoader.getInstance().playerAttackSprites;

		final int idleDelay = 8;
		final int runDelay = 10;
		final int attackDelay = 4;

		idleAnimation[0] = new Animation(idleDelay, false, sprites[0], sprites[1], sprites[2], sprites[3],
				sprites[4], sprites[5], sprites[6], sprites[7], sprites[8], sprites[9]);
		idleAnimation[1] = new Animation(idleDelay, false, sprites[10], sprites[11], sprites[12], sprites[13],
				sprites[14], sprites[15], sprites[16], sprites[17], sprites[18], sprites[19]);
		runAnimation[0] = new Animation(runDelay, false, sprites[20], sprites[21], sprites[22], sprites[23],
				sprites[24], sprites[25], sprites[26], sprites[27]);
		runAnimation[1] = new Animation(runDelay, false, sprites[28], sprites[29], sprites[30], sprites[31],
				sprites[32], sprites[33], sprites[34], sprites[35]);
		attackAnimation[0] = new Animation(attackDelay, true, attackSprites[0], attackSprites[1], attackSprites[2],
				attackSprites[3], attackSprites[4], attackSprites[5]);
		attackAnimation[1] = new Animation(attackDelay, true, attackSprites[6], attackSprites[7], attackSprites[8],
				attackSprites[9], attackSprites[10], attackSprites[11]);
	}

	private void runAnimations() {
		// Looking right
		if (direction == 1) {
			// Attacking
			if (attacking)
				attackAnimation[0].runAnimation();
			// Not moving
			else if (velX == 0)
				idleAnimation[0].runAnimation();
			// Moving right
			else
				runAnimation[0].runAnimation();
		}
		// Looking left
		else if (direction == -1) {
			// Attacking
			if (attacking)
				attackAnimation[1].runAnimation();
			// Not moving
			else if (velX == 0)
				idleAnimation[1].runAnimation();
			// Moving left
			else
				runAnimation[1].runAnimation();
		}
	}

	private void drawAnimations(Graphics g) {
		// Looking right
		if (direction == 1) {
			// Attacking
			if (attacking)
				attackAnimation[0].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			// Not moving
			else if (velX == 0)
				idleAnimation[0].drawAnimation(g, (int) x, (int) y, width, height);
			// Moving right
			else
				runAnimation[0].drawAnimation(g, (int) x, (int) y, width, height);
		}
		// Looking left
		else if (direction == -1) {
			// Attacking
			if (attacking)
				attackAnimation[1].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			// Not moving
			else if (velX == 0)
				idleAnimation[1].drawAnimation(g, (int) x, (int) y, width, height);
			// Moving left
			else
				runAnimation[1].drawAnimation(g, (int) x, (int) y, width, height);
		}
	}

}
