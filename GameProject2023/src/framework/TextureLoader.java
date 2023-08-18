package framework;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class TextureLoader {
	
	private static TextureLoader instance = null;
	
	private BufferedImage playerSheet = null;
	private BufferedImage blockSheet = null;
	
	public BufferedImage missingSprite;
	public BufferedImage[] playerRunIdleSprites;
	public BufferedImage[] playerAttackSprites;
	public BufferedImage[] playerJumpSprites;
	public BufferedImage[] stoneSprites;
	public BufferedImage[] diagonalStoneSprites;
	
	private TextureLoader() {
		try {
			FileIO fileIO = new FileIO();
			playerSheet = fileIO.loadSheet("player_sheet_32x32.png");
			blockSheet = fileIO.loadSheet("block_sheet_16x16.png");
		} 
		catch(IOException e) {
			System.err.println("Failed to load an image file from /res.");
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
		missingSprite = blockSheet.getSubimage(208, 0, 16, 16);
		
		stoneSprites = new BufferedImage[13];
		for (int i = 0; i < stoneSprites.length; i++)
			stoneSprites[i] = blockSheet.getSubimage(i * 16, 0, 16, 16);
		
		playerRunIdleSprites = new BufferedImage[36];
		for (int i = 0; i < 10; i++)
			playerRunIdleSprites[i] = playerSheet.getSubimage(331 + i * 33, 1, 32, 32);
		for (int i = 10; i < 20; i++)
			playerRunIdleSprites[i] = playerSheet.getSubimage(331 + (i - 10) * 33, 34, 32, 32);
		for (int i = 20; i < 28; i++)
			playerRunIdleSprites[i] = playerSheet.getSubimage(265 + (i - 20) * 33, 67, 32, 32);
		for (int i = 28; i < 36; i++)
			playerRunIdleSprites[i] = playerSheet.getSubimage(265 + (i - 28) * 33, 100, 32, 32);
		
		playerAttackSprites = new BufferedImage[12];
		for (int i = 0; i < 6; i++) 
			playerAttackSprites[i] = playerSheet.getSubimage(1 + i * 65, 133, 64, 64);
		for (int i = 6; i < 12; i++)
			playerAttackSprites[i] = playerSheet.getSubimage(1 + (i - 6) * 65, 198, 64, 64);
			
		playerJumpSprites = new BufferedImage[4];
		for (int i = 0; i < 2; i++)
			playerJumpSprites[i] = playerSheet.getSubimage(529 + i * 33, 67, 32, 32);
		for (int i = 2; i < 4; i++)
			playerJumpSprites[i] = playerSheet.getSubimage(529 + (i - 2) * 33, 100, 32, 32);
		
		diagonalStoneSprites = new BufferedImage[4];
		for (int i = 0; i < diagonalStoneSprites.length; i++)
			diagonalStoneSprites[i] = blockSheet.getSubimage(i * 16, 32, 16, 16);
	}
	
}
