package framework;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextureLoader {
	
	private static TextureLoader instance = null;
	
	private BufferedImage playerSheet = null;
	private BufferedImage blockSheet = null;
	
	public BufferedImage missingSprite;
	public BufferedImage[] playerSprites;
	public BufferedImage[] stoneSprites;
	public BufferedImage[] diagonalStoneSprites;
	
	private TextureLoader() {
		try {
			playerSheet = loadSheet("player_sheet_32x32.png");
			blockSheet = loadSheet("block_sheet_16x16.png");
		} catch(IOException e) {
			System.err.println("Failed to load a sprite sheet file.");
			e.printStackTrace();
		}
		
		loadTextures();
	};
	
	public static TextureLoader getInstance() {
		if (instance == null)
			instance = new TextureLoader();
		return instance;
	}
	
	// Load the image file with the given file name to use as a sprite sheet.
	private BufferedImage loadSheet(String filename) throws IOException {
		BufferedImage image = null;
		image = ImageIO.read(getClass().getResource("/resources/" + filename));
		return image;
	}
	
	private void loadTextures() {
		missingSprite = blockSheet.getSubimage(208, 0, 16, 16);
		
		stoneSprites = new BufferedImage[13];
		for (int i = 0; i < stoneSprites.length; i++)
			stoneSprites[i] = blockSheet.getSubimage(i * 16, 0, 16, 16);
		
		playerSprites = new BufferedImage[9];
		playerSprites[0] = playerSheet.getSubimage(1, 1, 32, 32);
		for (int i = 1; i < playerSprites.length; i++)
			playerSprites[i] = playerSheet.getSubimage(1 + i * 33, 34, 32, 32);
		
		diagonalStoneSprites = new BufferedImage[4];
		for (int i = 0; i < diagonalStoneSprites.length; i++)
			diagonalStoneSprites[i] = blockSheet.getSubimage(i * 16, 32, 16, 16);
	}
	
}