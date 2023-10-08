package ui;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import abstracts.GameObject;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;

public class HUD extends GameObject {

	private Player player;

	private int prevPlayerHealth;
	private float healthDiff = 0, deltaHealthDiff = 0.5f;

	private BufferedImage[] hudTextures;

	public HUD(float x, float y, int width, int height, Player player) {
		super(x, y, width, height, new ObjectId(Category.Menu, Name.Missing));
		this.player = player;

		hudTextures = TextureLoader.getInstance().getTextures(TextureName.HUD);
	}

	@Override
	public void tick() {
		if (player == null)
			return;

		if (prevPlayerHealth != player.getHealth()) {
			healthDiff = player.getHealth() - prevPlayerHealth; 
			prevPlayerHealth = player.getHealth();
		}
		if (healthDiff < -0.1f)
			healthDiff += deltaHealthDiff;
		else
			healthDiff = 0;
	}

	@Override
	public void render(Graphics g) {
		if (player == null)
			return;

		final int x = (int) this.x;
		final int y = (int) this.y;
		final int portraitSize = (int) (TILE_SIZE * 1.8f);
		final int healthBarWidth = (int) (TILE_SIZE * 3.5f);
		final int healthBarHeight = (int) (TILE_SIZE * 0.6f);
		final int moneyBarWidth = (int) (TILE_SIZE * 2.4f);
		final int moneyBarHeight = (int) (TILE_SIZE * 0.8f);

		// Player portrait
		g.drawImage(hudTextures[0], x, y, portraitSize, portraitSize, null);
		g.drawImage(hudTextures[1], x, y, portraitSize, portraitSize, null);

		// Player health bar background
		g.drawImage(hudTextures[2], x + portraitSize, y, healthBarWidth, healthBarHeight, null);

		// Health bar damage effect
		if (healthDiff < 0) {
			float playerHealthRatio = (float) (player.getHealth() - healthDiff) / player.getMaxHealth();
			BufferedImage damageImage = getClippedImage(hudTextures[4], playerHealthRatio,
					healthBarWidth, healthBarHeight);
			g.drawImage(damageImage, x + portraitSize, y, null);
		}

		// Player health bar
		if (player.getHealth() > 0) {
			float playerHealthRatio = (float) player.getHealth() / player.getMaxHealth();
			BufferedImage healthBarImage = getClippedImage(hudTextures[3], playerHealthRatio,
					healthBarWidth, healthBarHeight);
			g.drawImage(healthBarImage, x + portraitSize, y, null);
		}

		// Player money background
		g.drawImage(hudTextures[5], x + portraitSize, y + healthBarHeight, moneyBarWidth, moneyBarHeight, null);
	}

	// Scale and clip the health bar image to match the player's actual health
	private BufferedImage getClippedImage(BufferedImage image, float ratio, int width, int height) {
		Image healthScaledImage = image.getScaledInstance(width, height, 0);
		image = toBufferedImage(healthScaledImage);

		int newWidth = (int) ((image.getWidth()) * ratio);
		int newX = width - newWidth;
		image = image.getSubimage(
				Math.min(newX, width - 1),
				0,
				Math.max(newWidth, 1),
				image.getHeight());
		return image;
	}

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	private BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage)
			return (BufferedImage) img;

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}
}
