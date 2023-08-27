package framework;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class TextureLoader {
	
	private static TextureLoader instance = null;
	
	private BufferedImage playerSheet = null;
	private BufferedImage blockSheet = null;
	private BufferedImage enemySheet = null;
	
	public BufferedImage missingSprite;

	public BufferedImage[] playerIdleSprites;
	public BufferedImage[] playerRunSprites;
	public BufferedImage[] playerAttackSprites;
	public BufferedImage[] playerJumpSprites;
	public BufferedImage[] playerSwordIdleSprites;
	public BufferedImage[] playerSwordRunSprites;
	public BufferedImage[] playerSwordAttackSprites;
	public BufferedImage[] playerSwordStabSprites;
	public BufferedImage[] hammerIdleSprites;
	
	public BufferedImage[] basicEnemyRunIdleSprites;
	public BufferedImage[] basicEnemyAttackSprites;
	public BufferedImage[] basicEnemyJumpSprites;
	
	public BufferedImage[] stoneSprites;
	public BufferedImage[] grassSprites;
	public BufferedImage[] rockSprites;
	public BufferedImage[] diagonalStoneSprites;
	public BufferedImage[] woodJumpThroughSprites;
	
	private TextureLoader() {
		try {
			FileIO fileIO = new FileIO();
			playerSheet = fileIO.loadSheet("player_sheet_64x64.png");
			enemySheet = fileIO.loadSheet("enemy_sheet_32x32.png");
			blockSheet = fileIO.loadSheet("block_sheet_16x16.png");
		} 
		catch(IOException e) {
			System.err.println("Failed to load an image file from the /res folder.");
			e.printStackTrace();
		}
		
		loadTextures();
	};
	
	public static TextureLoader getInstance() {
		if (instance == null)
			instance = new TextureLoader();
		return instance;
	}
	
	/* 
	 * TODO: Textures should not be loaded all at once.
	 * Because we dont want all textures to be stored in memory at all times.
	 * Since it is not guaranteed that all textures will be in use at one time.
	 */
	private void loadTextures() {
		missingSprite = blockSheet.getSubimage(222, 1, 16, 16);
		
		// Player Sprites_____________________________________________________
		playerIdleSprites = new BufferedImage[20];
		for (int i = 0; i < 10; i++) {
			playerIdleSprites[i] = playerSheet.getSubimage(1 + i * 65, 1, 64, 64);
			playerIdleSprites[i + 10] = playerSheet.getSubimage(1 + i * 65, 66, 64, 64);
		}
		playerRunSprites = new BufferedImage[16];
		for (int i = 0; i < 8; i++) {
			playerRunSprites[i] = playerSheet.getSubimage(1 + i * 65, 131, 64, 64);
			playerRunSprites[i + 8] = playerSheet.getSubimage(1 + i * 65, 196, 64, 64);
		}
		playerAttackSprites = new BufferedImage[16];
		for (int i = 0; i < 8; i++) {
			playerAttackSprites[i] = playerSheet.getSubimage(1 + i * 65, 261, 64, 64);
			playerAttackSprites[i + 8] = playerSheet.getSubimage(1 + i * 65, 326, 64, 64);
		}
		playerJumpSprites = new BufferedImage[4];
		for (int i = 0; i < 2; i++) {
			playerJumpSprites[i] = playerSheet.getSubimage(521 + i * 65, 131, 64, 64);
			playerJumpSprites[i + 2] = playerSheet.getSubimage(521 + i * 65, 196, 64, 64);
		}
		playerSwordIdleSprites = new BufferedImage[20];
		for (int i = 0; i < 10; i++) {
			playerSwordIdleSprites[i] = playerSheet.getSubimage(1 + i * 65, 391, 64, 64);
			playerSwordIdleSprites[i + 10] = playerSheet.getSubimage(1 + i * 65, 456, 64, 64);
		}
		playerSwordRunSprites = new BufferedImage[16];
		for (int i = 0; i < 8; i++) {
			playerSwordRunSprites[i] = playerSheet.getSubimage(1 + i * 65, 521, 64, 64);
			playerSwordRunSprites[i + 8] = playerSheet.getSubimage(1 + i * 65, 586, 64, 64);
		}
		playerSwordAttackSprites = new BufferedImage[12];
		for (int i = 0; i < 6; i++) {
			playerSwordAttackSprites[i] = playerSheet.getSubimage(1 + i * 65, 651, 64, 64);
			playerSwordAttackSprites[i + 6] = playerSheet.getSubimage(1 + i * 65, 716, 64, 64);
		}
		playerSwordStabSprites = new BufferedImage[14];
		for (int i = 0; i < 7; i++) {
			playerSwordStabSprites[i] = playerSheet.getSubimage(1 + i * 65, 781, 64, 64);
			playerSwordStabSprites[i + 7] = playerSheet.getSubimage(1 + i * 65, 846, 64, 64);
		}
		hammerIdleSprites = new BufferedImage[14]; 
		for (int i = 0; i < 7; i++) {
			hammerIdleSprites[i] = playerSheet.getSubimage(1 + i * 65, 911, 64, 64);
			hammerIdleSprites[i + 7] = playerSheet.getSubimage(1 + i * 65, 976, 64, 64);
		}
		
		// Regular Tile Sprites_______________________________________________
		stoneSprites = new BufferedImage[13];
		for (int i = 0; i < stoneSprites.length; i++)
			stoneSprites[i] = blockSheet.getSubimage(1 + i * 17, 1, 16, 16);
		grassSprites = new BufferedImage[13];
		for (int i = 0; i < grassSprites.length; i++)
			grassSprites[i] = blockSheet.getSubimage(1 + i * 17, 18, 16, 16);
		rockSprites = new BufferedImage[13];
		for (int i = 0; i < rockSprites.length; i++)
			rockSprites[i] = blockSheet.getSubimage(1 + i * 17, 35, 16, 16);
		
		// Jump Through Tile Sprites__________________________________________
		woodJumpThroughSprites = new BufferedImage[3];
		for (int i = 0; i < woodJumpThroughSprites.length; i++)
			woodJumpThroughSprites[i] = blockSheet.getSubimage(1 + i * 17, 69, 16, 16);
		
		// Diagonal Tile Sprites______________________________________________
		diagonalStoneSprites = new BufferedImage[4];
		for (int i = 0; i < diagonalStoneSprites.length; i++)
			diagonalStoneSprites[i] = blockSheet.getSubimage(1 + i * 17, 52, 16, 16);
		
		// Basic Enemy Sprites________________________________________________
		basicEnemyRunIdleSprites = new BufferedImage[36];
		for (int i = 0; i < 10; i++)
			basicEnemyRunIdleSprites[i] = enemySheet.getSubimage(1 + i * 33, 1, 32, 32);
		for (int i = 10; i < 20; i++)
			basicEnemyRunIdleSprites[i] = enemySheet.getSubimage(1 + (i - 10) * 33, 34, 32, 32);
		for (int i = 20; i < 28; i++)
			basicEnemyRunIdleSprites[i] = enemySheet.getSubimage(1 + (i - 20) * 33, 67, 32, 32);
		for (int i = 28; i < 36; i++)
			basicEnemyRunIdleSprites[i] = enemySheet.getSubimage(1 + (i - 28) * 33, 100, 32, 32);
		
		basicEnemyAttackSprites = new BufferedImage[16];
		for (int i = 0; i < 6; i++) 
			basicEnemyAttackSprites[i] = enemySheet.getSubimage(1 + i * 65, 133, 64, 64);
		for (int i = 6; i < 12; i++)
			basicEnemyAttackSprites[i] = enemySheet.getSubimage(1 + (i - 6) * 65, 198, 64, 64);
			
		basicEnemyJumpSprites = new BufferedImage[4];
		for (int i = 0; i < 2; i++)
			basicEnemyJumpSprites[i] = enemySheet.getSubimage(265 + i * 33, 67, 32, 32);
		for (int i = 2; i < 4; i++)
			basicEnemyJumpSprites[i] = enemySheet.getSubimage(265 + (i - 2) * 33, 100, 32, 32);
	}
	
}
