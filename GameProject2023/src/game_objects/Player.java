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
import player_weapons.Sword;
import player_weapons.Weapon;
import window.Animation;
import window.KeyInput;
import window.MouseInput;

public class Player extends Creature {

	private ObjectHandler objectHandler;
	private KeyInput keyInput;
	private MouseInput mouseInput;
	
	private Weapon weapon;

	private float runningSpeed = 3f;
	private float jumpingSpeed = -8.5f;

	private int invulnerableDuration = 500;
	private long lastInvulnerableTimer = -invulnerableDuration; 
	private int attackCooldown = 500;
	private long lastAttackTimer = -attackCooldown;
	
	private Animation[] idleAnimation;
	private Animation[] runAnimation;
	private Animation[] attackAnimation;
	private BufferedImage[] jumpingSprites;

	public Player(int x, int y, ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, 40, 100, 70, new ObjectId(Category.Player, Name.Player));
		this.objectHandler = objectHandler;
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		objectHandler.setPlayer(this);
		
		weapon = new Sword(objectHandler);
		
		texture = TextureLoader.getInstance().playerRunIdleSprites[0];
		setupAnimations();
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

		if (invulnerable && (System.currentTimeMillis() - lastInvulnerableTimer >= invulnerableDuration))
			invulnerable = false;

		handleMovement();
		handleAttacking();
		if (!attacking)
			handleAbilities();
		handleCollision();

		runAnimations();
	}

	@Override
	public void render(Graphics g) {
		drawAnimations(g);

		// Debug
		if (keyInput.debugPressed) {
			g.setFont(new Font("Calibri", Font.PLAIN, 15));
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(10, 40, 170, 155);
			
			g.setColor(new Color(220, 80, 80));
			g.drawString("Health......................" + (int) health, 20, 60);
			g.setColor(new Color(80, 220, 80));
			g.drawString("Stamina...................." + (int) stamina, 20, 80);
			
			g.setColor(Color.CYAN);
			g.drawString("velX: " + velX, 20, 100);
			g.drawString("velY: " + velY, 20, 120);
			
			g.setColor(Color.white);
			g.drawString("Attacking................." + attacking, 20, 140);
			g.drawString("Invulnerable............" + invulnerable, 20, 160);
			g.drawString("Knocked back.........." + knockedBack, 20, 180);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.white);
			g2d.draw(getBottomBounds());
			g2d.draw(getHorizontalBounds());
			g2d.draw(getTopBounds());
			g2d.setColor(new Color(255, 255, 255, 70));
			g2d.draw(getBounds());
			g2d.setColor(new Color(255, 0, 0, 100));
			g2d.draw(getGroundAttackBounds());
		}
	}

	// Use the keyboard inputs of the user to move the player
	private void handleMovement() {
		if (attacking) {
			velX = 0;
			return;
		}
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
	
	private void handleAbilities() {
		if (keyInput.isFirstAbilityKeyPressed() || weapon.getAbility(0).isAbilityBeingUsed())
			weapon.useAbility(0);
		if (keyInput.isSecondAbilityKeyPressed() || weapon.getAbility(1).isAbilityBeingUsed())
			weapon.useAbility(1);
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

	private void handleCollision() {
		ArrayList<GameObject> midLayer = objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER);
		for (int i = midLayer.size() - 1; i >= 0; i--) {
			GameObject other = midLayer.get(i);
			if (other.equals(this))
				continue;
			
			// Collision with Blocks
			if (other.getObjectId().getCategory() == Category.Block)
				checkBlockCollision(other);
			if (other.getObjectId().getCategory() == Category.DiagonalBlock)
				checkDiagonalBlockCollision(other);

			// Attack collision with enemies
			if (other.getObjectId().getCategory() == Category.Enemy) { 
				if (getGroundAttackBounds().intersects(other.getBounds())) {
					Creature otherCreature = (Creature) other;
					if (attacking) {
						otherCreature.takeDamage(damage);
						otherCreature.applyKnockback(3 * direction, -4);
					}
				}
				
			}
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

			// Reset knock back after hitting the ground
			if (knockedBack) {
				knockedBack = false;
				velX = 0;
			}
		}
		else
			falling = true;

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
			falling = false;
			jumping = false;
		}
		else
			falling = true;
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

	private Rectangle getGroundAttackBounds() {
		int attackX;
		int attackWidth = (int) (4f * width / 5);
		if (direction == 1)
			attackX = (int) x + width / 2;
		else
			attackX = (int) x - attackWidth / 2;
		return new Rectangle(attackX, (int) y, attackWidth, height);
	}

	private void setupAnimations() {
		idleAnimation = new Animation[2];
		runAnimation = new Animation[2];
		attackAnimation = new Animation[2];

		TextureLoader textureLoader = TextureLoader.getInstance();
		BufferedImage[] sprites = textureLoader.playerRunIdleSprites;
		BufferedImage[] attackSprites = textureLoader.playerAttackSprites;

		jumpingSprites = textureLoader.playerJumpSprites;

		final int idleDelay = 8;
		final int runDelay = 8;
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
			if (weapon.getAbility(0).isAbilityBeingUsed())
				weapon.getAbility(0).getAnimation(0).runAnimation();
			else if (weapon.getAbility(1).isAbilityBeingUsed())
				weapon.getAbility(1).getAnimation(0).runAnimation();
			// Attacking
			else if (attacking)
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
			if (weapon.getAbility(0).isAbilityBeingUsed())
				weapon.getAbility(0).getAnimation(1).runAnimation();
			else if (weapon.getAbility(1).isAbilityBeingUsed())
				weapon.getAbility(1).getAnimation(1).runAnimation();
			// Attacking
			else if (attacking)
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
			if (weapon.getAbility(0).isAbilityBeingUsed())
				weapon.getAbility(0).getAnimation(0).drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			else if (weapon.getAbility(1).isAbilityBeingUsed())
				weapon.getAbility(1).getAnimation(0).drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			// Attacking
			else if (attacking)
				attackAnimation[0].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			// Jumping
			else if (jumping) {
				// Going up / reached peak
				if (velY <= 0)
					g.drawImage(jumpingSprites[0], (int) x, (int) y, width, height, null);
				// Going down
				else if (velY > 0)
					g.drawImage(jumpingSprites[1], (int) x, (int) y, width, height, null);
			}
			// Not moving
			else if (velX == 0)
				idleAnimation[0].drawAnimation(g, (int) x, (int) y, width, height);
			// Moving right
			else
				runAnimation[0].drawAnimation(g, (int) x, (int) y, width, height);
		}
		// Looking left
		else if (direction == -1) {
			if (weapon.getAbility(0).isAbilityBeingUsed())
				weapon.getAbility(0).getAnimation(1).drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			else if (weapon.getAbility(1).isAbilityBeingUsed())
				weapon.getAbility(1).getAnimation(1).drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			// Attacking
			else if (attacking)
				attackAnimation[1].drawAnimation(g, (int) x - width / 2, (int) y - height / 2, width * 2, height * 2);
			// Jumping
			else if (jumping) {
				// Going up / reached peak
				if (velY <= 0)
					g.drawImage(jumpingSprites[2], (int) x, (int) y, width, height, null);
				// Going down
				else if (velY > 0)
					g.drawImage(jumpingSprites[3], (int) x, (int) y, width, height, null);
			}
			// Not moving
			else if (velX == 0)
				idleAnimation[1].drawAnimation(g, (int) x, (int) y, width, height);
			// Moving left
			else
				runAnimation[1].drawAnimation(g, (int) x, (int) y, width, height);
		}
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

}
