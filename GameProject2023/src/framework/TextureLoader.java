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
	
	private TextureLoader() {
		try {
			playerSheet = loadSheet("player_sheet_48x48.png");
			blockSheet = loadSheet("block_sheet_32x32.png");
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		loadTextures();
	};
	
	public static TextureLoader getInstance() {
		if (instance == null)
			instance = new TextureLoader();
		return instance;
	}
	
	private void loadTextures() {
		missingSprite = blockSheet.getSubimage(397, 166, 32, 32);
		
		stoneSprites = new BufferedImage[13];
		for (int i = 0; i < stoneSprites.length; i++)
			stoneSprites[i] = blockSheet.getSubimage(1 + i * 33, 1, 32, 32);
		
		playerSprites = new BufferedImage[9];
		playerSprites[0] = playerSheet.getSubimage(1, 1, 48, 48);
		for (int i = 1; i < playerSprites.length; i++)
			playerSprites[i] = playerSheet.getSubimage(1 + i * 48, 1, 48, 48);
	}
	
	// Load the image file with the given file name to use as a sprite sheet.
	private BufferedImage loadSheet(String filename) throws IOException {
		BufferedImage image = null;
		image = ImageIO.read(getClass().getResource("/resources/" + filename));
		return image;
	}
	
}
