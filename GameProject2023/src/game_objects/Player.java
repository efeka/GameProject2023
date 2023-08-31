package game_objects;

import static framework.GameConstants.ScaleConstants.PLAYER_HEIGHT;
import static framework.GameConstants.ScaleConstants.PLAYER_WIDTH;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import abstract_objects.Creature;
import abstract_objects.DiagonalTileBlock;
import abstract_objects.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import items.Item;
import player_weapons.SwordWeapon;
import player_weapons.Weapon;
import window.KeyInput;
import window.MouseInput;

public class Player extends Creature {

	private ObjectHandler objectHandler;
	private KeyInput keyInput;
	private MouseInput mouseInput;

	private Weapon weapon;

	private float runningSpeed = 3f;
	private float jumpingSpeed = -8.5f;
	
	private boolean canInteract = true;
	private long lastInteractTimer;

	private PlayerAnimationHandler animationHandler;
	private BufferedImage[] jumpSprites;

	public Player(int x, int y, ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 40, 100, 70, new ObjectId(Category.Player, Name.Player));
		this.objectHandler = objectHandler;
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		objectHandler.setPlayer(this);

		weapon = new SwordWeapon(objectHandler);

		invulnerableDuration = 500;
		
		animationHandler = new PlayerAnimationHandler(this);
		jumpSprites = TextureLoader.getInstance().getTextures(TextureName.PlayerJump);
		texture = TextureLoader.getInstance().getTextures(TextureName.PlayerIdle)[0];
	}

	@Override
	public void tick() {
		regenerateStamina();

		x += velX;
		y += velY;

		if (falling || jumping) {
			velY += GRAVITY;

			if (velY > TERMINAL_VELOCITY)
				velY = TERMINAL_VELOCITY;
		}

		long now = System.currentTimeMillis();
		if (invulnerable && (now - lastInvulnerableTimer >= invulnerableDuration))
			invulnerable = false;
		if (!canInteract && (now - lastInteractTimer >= 1000))
			canInteract = true;

		// Handle ability usage
		if (mouseInput.isAttackButtonPressed() || weapon.getAbility(0).isAbilityBeingUsed())
			weapon.useAbility(0);
		if (keyInput.isFirstAbilityKeyPressed() || weapon.getAbility(1).isAbilityBeingUsed())
			weapon.useAbility(1);
		
		handleMovement();		
		handleObjectInteraction();

		runAnimations();
	}

	@Override
	public void render(Graphics g) {
		drawAnimations(g);

		// Debug
		if (keyInput.debugPressed) {
			int consoleX = 180, consoleY = 10;
			g.setFont(new Font("Calibri", Font.PLAIN, 15));
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(consoleX - 5, consoleY, 170, 195);

			g.setColor(new Color(220, 80, 80));
			g.drawString("Health......................" + (int) health, consoleX, consoleY += 20);
			g.setColor(new Color(80, 220, 80));
			g.drawString("Stamina...................." + (int) stamina, consoleX, consoleY += 20);

			g.setColor(Color.CYAN);
			g.drawString("velX: " + velX, consoleX, consoleY += 20);
			g.drawString("velY: " + velY, consoleX, consoleY += 20);

			g.setColor(Color.white);
			g.drawString("Falling....................." + falling, consoleX, consoleY += 20);
			g.drawString("Jumping.................." + jumping, consoleX, consoleY += 20);
			g.drawString("Invulnerable............" + invulnerable, consoleX, consoleY += 20);
			g.drawString("Knocked back.........." + knockedBack, consoleX, consoleY += 20);
			g.drawString("Can Interact............" + canInteract, consoleX, consoleY += 20);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.white);
			g2d.draw(getBottomBounds());
			g2d.draw(getHorizontalBounds());
			g2d.draw(getTopBounds());
			g2d.setColor(new Color(255, 255, 255, 70));
			g2d.draw(getBounds());
			g2d.setColor(new Color(255, 0, 0, 100));
			g2d.setColor(Color.MAGENTA);
			g2d.draw(getGroundCheckBounds());
		}
	}

	// Use the keyboard inputs of the user to move the player
	private void handleMovement() {
		if (knockedBack)
			return;

		if (velX > 0)
			direction = 1;
		else if (velX < 0)
			direction = -1;

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

	@Override
	public void takeDamage(int damageAmount) {
		if (invulnerable)
			return;

		lastInvulnerableTimer = System.currentTimeMillis();
		invulnerable = true;

		setHealth(health - damageAmount);
	}

	@Override
	public void applyKnockback(float velX, float velY) {
		knockedBack = true;
		this.velX = velX;
		this.velY = velY;
	}

	private void handleObjectInteraction() {
		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		falling = true;
		
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			if (other.equals(this))
				continue;

			// Check Collisions
			if (other.getObjectId().getCategory() == Category.Block)
				checkBlockCollision(other);
			if (other.getObjectId().getCategory() == Category.DiagonalBlock)
				checkDiagonalBlockCollision(other);
			
			// Check Item Pickup
			if (canInteract && keyInput.isInteractKeyPressed() && other.getObjectId().getCategory() == Category.Item) {
				if (getBounds().intersects(other.getBounds())) {
					((Item) other).pickupItem();
					
					canInteract = false;
					lastInteractTimer = System.currentTimeMillis();
				}
			}
		}
	}

	private void checkBlockCollision(GameObject other) {
		Rectangle otherBounds = other.getBounds();

		// Check if the player is grounded or not
		if (getGroundCheckBounds().intersects(otherBounds))
			falling = false;
		
		// Bottom collision
		if (getBottomBounds().intersects(otherBounds)) {
			y = other.getY() - height;
			velY = 0;
			jumping = false;

			// Reset knock back after hitting the ground
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
			jumping = false;
		}
	}

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
	
	private Rectangle getGroundCheckBounds() {
		float width = 3 * this.width / 5f;
		float xOffset = (this.width - width) / 2;
		float height = this.height / 5f;
		float yOffset = this.height;
		return new Rectangle((int) (x + xOffset), (int) (y + yOffset), (int) width, (int) height);
	}

	private void runAnimations() {
		int directionToIndex = animationHandler.getIndexFromDirection();

		// Using first ability
		if (weapon.getAbility(0).isAbilityBeingUsed())
			weapon.getAbility(0).getAnimation(directionToIndex).runAnimation();
		// Using second ability
		else if (weapon.getAbility(1).isAbilityBeingUsed())
			weapon.getAbility(1).getAnimation(directionToIndex).runAnimation();
		// Idle
		else if (velX == 0)
			animationHandler.getIdleAnimation().runAnimation();
		// Running
		else if (velX != 0)
			animationHandler.getRunAnimation().runAnimation();
	}

	private void drawAnimations(Graphics g) {
		int directionToIndex = animationHandler.getIndexFromDirection();

		// Using first ability
		if (weapon.getAbility(0).isAbilityBeingUsed())
			weapon.getAbility(0).getAnimation(directionToIndex).drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
		// Using second ability
		else if (weapon.getAbility(1).isAbilityBeingUsed())
			weapon.getAbility(1).getAnimation(directionToIndex).drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
		// Jumping
		else if (jumping) {
			// Going up
			if (velY <= 0)
				g.drawImage(jumpSprites[directionToIndex * 2], (int) x - width / 2, (int) y - height / 2, width * 2, height * 2, null);
			// Going down
			else if (velY > 0)
				g.drawImage(jumpSprites[directionToIndex * 2 + 1], (int) x - width / 2, (int) y - height / 2, width * 2, height * 2, null);	
		}
		// Idle
		else if (velX == 0)
			animationHandler.getIdleAnimation().drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
		// Running
		else if (velX != 0)
			animationHandler.getRunAnimation().drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
	}
	
	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

}
