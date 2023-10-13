package game_objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import abstracts.Weapon;
import framework.Animation;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class PlayerAnimationHandler {

	private Player player;

	private Animation[] doubleJumpAnimations;
	private Animation[] dodgeAnimations;
	private Animation[] landAnimations;
	
	private boolean flashWhiteToggle;
	private long flashWhiteToggleTimer;
	private int flashWhiteToggleCooldownMillis = 100; 
	
	public PlayerAnimationHandler(Player player) {
		this.player = player;
		setupAnimations();
	}
	
	public void runPlayerAnimations() {
		final Weapon weapon = player.getWeapon();
		final float velX = player.getVelX();
		
		// Using Weapon Ability
		if (weapon.isUsingAbility() && weapon.getCurrentAnimation() != null)
			weapon.getCurrentAnimation().runAnimation();
		// Using Weapon Ability
		else if (player.isDodging()) {
			dodgeAnimations[0].runAnimation();
			dodgeAnimations[1].runAnimation();
		}
		// Landing
		else if (player.isLanding()) {
			landAnimations[0].runAnimation();
			landAnimations[1].runAnimation();
		}
		// Double Jumping
		else if (player.isDoubleJumping()) {
			doubleJumpAnimations[0].runAnimation();
			doubleJumpAnimations[1].runAnimation();
		}
		// Idle
		else if (velX == 0)
			getIdleAnimation().runAnimation();
		// Running
		else if (velX != 0)
			getRunAnimation().runAnimation();
	}
	
	public void drawPlayerAnimations(Graphics g) {
		int imageX = (int) (player.getX() - player.getWidth() / 2);
		int imageY = (int) (player.getY() - player.getHeight() / 2);
		int imageWidth = player.getWidth() * 2;
		int imageHeight = player.getHeight() * 2;
		
		if (System.currentTimeMillis() - flashWhiteToggleTimer >= flashWhiteToggleCooldownMillis) {
			flashWhiteToggle = !flashWhiteToggle;
			flashWhiteToggleTimer = System.currentTimeMillis();
		}
		
		// If the player is invulnerable, display a periodic white flash as an indicator
		BufferedImage currentImage = getCurrentAnimationImage();
		if (player.isInvulnerable() && flashWhiteToggle)
			currentImage = getImageInWhite((Graphics2D) g, currentImage);
		
		g.drawImage(currentImage, imageX, imageY, imageWidth, imageHeight, null);
	}
	
	private BufferedImage getCurrentAnimationImage() {
		BufferedImage currentAnimationImage = null;
		
		final int directionToIndex = getIndexFromDirection();
		final Weapon weapon = player.getWeapon();
		final float velX = player.getVelX();
		final float velY = player.getVelY();
		
		// Using Weapon Ability
		if (weapon.isUsingAbility() && weapon.getCurrentAnimation() != null)
			currentAnimationImage = weapon.getCurrentAnimation().getCurrentImage();
		// Dodging
		else if (player.isDodging())
			currentAnimationImage = dodgeAnimations[directionToIndex].getCurrentImage();
		// Landing
		else if (player.isLanding())
			currentAnimationImage = landAnimations[directionToIndex].getCurrentImage();
		// Jumping / Double Jumping
		else if (player.isFalling() || player.isJumping() || player.isDoubleJumping()) {
			if (!player.isDoubleJumping()) {
				BufferedImage[] jumpSprites = getJumpSprites();
				if (velY <= 0)
					currentAnimationImage = jumpSprites[directionToIndex * 2];
				else if (velY > 0)
					currentAnimationImage = jumpSprites[directionToIndex * 2 + 1];
			}
			else
				currentAnimationImage = doubleJumpAnimations[directionToIndex].getCurrentImage();
		}
		// Idle
		else if (velX == 0)
			currentAnimationImage = getIdleAnimation().getCurrentImage();
		// Running
		else if (velX != 0)
			currentAnimationImage = getRunAnimation().getCurrentImage();
		
		return currentAnimationImage;
	}

	private void setupAnimations() {
		TextureLoader textureLoader = TextureLoader.getInstance();

		int doubleJumpDelay = 8;
		doubleJumpAnimations = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDoubleJump, 1),
						doubleJumpDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDoubleJump, -1),
						doubleJumpDelay, true),
		};

		int dodgeAnimationDelay = 6;
		dodgeAnimations = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDodge, 1),
						dodgeAnimationDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerDodge, -1),
						dodgeAnimationDelay, true),
		};

		int landAnimationDelay = 8; 
		landAnimations = new Animation[] {
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerLand, 1), 
						landAnimationDelay, true),
				new Animation(textureLoader.getTexturesByDirection(TextureName.PlayerLand, -1), 
						landAnimationDelay, true),
		};
	}

	public Animation getIdleAnimation() {
		return player.getWeapon().getIdleAnimation()[getIndexFromDirection()];
	}

	public Animation getRunAnimation() {
		return player.getWeapon().getRunAnimation()[getIndexFromDirection()];
	}
	
	public BufferedImage[] getJumpSprites() {
		return player.getWeapon().getJumpSprites();
	}

	public boolean isDoubleJumpAnimationFinished() {
		return doubleJumpAnimations[0].isPlayedOnce() || doubleJumpAnimations[1].isPlayedOnce();
	}

	public void resetDoubleJumpAnimations() {
		doubleJumpAnimations[0].resetAnimation();
		doubleJumpAnimations[1].resetAnimation();
	}

	public boolean isDodgeAnimationFinished() {
		return dodgeAnimations[0].isPlayedOnce() || dodgeAnimations[1].isPlayedOnce();
	}

	public void resetDodgeAnimations() {
		dodgeAnimations[0].resetAnimation();
		dodgeAnimations[1].resetAnimation();
	}

	public boolean isLandAnimationFinished() {
		return landAnimations[0].isPlayedOnce() || landAnimations[1].isPlayedOnce();
	}
	
	public void resetLandAnimations() {
		landAnimations[0].resetAnimation();
		landAnimations[1].resetAnimation();
	}

	/**
	 * Paints the non-transparent pixels of the given image to a white color.
	 * @param g2d the Graphics2D object
	 * @param image the image to be painted white
	 * @return the white image
	 */
	private BufferedImage getImageInWhite(Graphics2D g2d, BufferedImage image) {
	    BufferedImage whiteImage = new BufferedImage(image.getWidth(), image.getHeight(), 
	    		BufferedImage.TYPE_INT_ARGB);

	    for (int y = 0; y < image.getHeight(); y++) {
	        for (int x = 0; x < image.getWidth(); x++) {
	            int pixel = image.getRGB(x, y);
	            int alpha = (pixel >> 24) & 0xFF;
	            if (alpha != 0)
	                whiteImage.setRGB(x, y, new Color(199, 207, 204).getRGB());
	        }
	    }

	    return whiteImage;
	}
	
	/**
	 * Maps the player's direction to array indices: {-1,1} becomes {1,0}.
	 * This is possible because sprites that are facing right are loaded in the 0th index,
	 * while the sprites that are facing left are always loaded in the 1st index of the animation arrays.
	 */
	public int getIndexFromDirection() {
		return (-player.getDirection() + 1) / 2;
	}

}
