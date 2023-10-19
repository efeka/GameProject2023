package ui;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import abstracts.GameObject;
import framework.BufferedImageUtil;
import framework.FontUtil;
import framework.GameConstants.FontConstants;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.player.Player;

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
			int newBarWidth = (int) (healthBarWidth * playerHealthRatio);
			BufferedImage damagedBarImage = BufferedImageUtil.getScaledInstance(
					hudTextures[4],
					healthBarWidth,
					healthBarHeight);
			damagedBarImage = BufferedImageUtil.getLeftClippedImage(damagedBarImage, newBarWidth);
			g.drawImage(damagedBarImage, x + portraitSize, y, null);
		}

		// Player health bar
		if (player.getHealth() > 0) {
			float playerHealthRatio = (float) player.getHealth() / player.getMaxHealth();
			int newBarWidth = (int) (healthBarWidth * playerHealthRatio);
			BufferedImage healthBarImage = BufferedImageUtil.getScaledInstance(
					hudTextures[3],
					healthBarWidth,
					healthBarHeight);
			healthBarImage = BufferedImageUtil.getLeftClippedImage(healthBarImage, newBarWidth);
			g.drawImage(healthBarImage, x + portraitSize, y, null);
		}

		// Player money background
		g.drawImage(hudTextures[5], x + portraitSize, y + healthBarHeight, moneyBarWidth, moneyBarHeight, null);
		
		// Player money amount
		g.setColor(FontConstants.WHITE_FONT_COLOR);
		Font font = FontConstants.UI_FONT;
		g.setFont(font);
		String coinText = player.getCoinCount() + "";
		Point textCenter = new Point(x + portraitSize + moneyBarWidth / 4, y + healthBarHeight + moneyBarHeight / 2);
		Point alignedTextPosition = FontUtil.getTextPositionForLeftAlignment(g, font, textCenter, coinText);
		g.drawString(coinText, alignedTextPosition.x, alignedTextPosition.y - 2);
	}

}
