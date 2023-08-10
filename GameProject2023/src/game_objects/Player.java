package game_objects;

import java.awt.Graphics;
import java.awt.Rectangle;

import framework.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import general_object_types.Creature;
import window.KeyInput;

public class Player extends Creature {

	private ObjectHandler objectHandler;
	private KeyInput keyInput;
	
	private float runningSpeed = 5f;
	private float jumpingSpeed = -12f;
	
	public Player(int x, int y, int width, int height, ObjectHandler objectHandler, KeyInput keyInput) {
		super(x, y, width, height, new ObjectId(Category.Player, Name.Player));
		this.objectHandler = objectHandler;
		this.keyInput = keyInput;
		
		texture = TextureLoader.getInstance().playerSprites[0];
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
		
		handleMovement();
		handleCollision();
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}
	
	// Use the keyboard inputs of the user to move the player
	private void handleMovement() {
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
	
	// TODO Handle collision with other game objects
	private void handleCollision() {
		for (GameObject other : objectHandler.getLayer(ObjectHandler.MIDDLE_LAYER)) {
			Rectangle otherBounds = other.getBounds();
			
			// Collision with Blocks
			if (other.getObjectId().getCategory() == Category.Block) {
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
		}
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

}
