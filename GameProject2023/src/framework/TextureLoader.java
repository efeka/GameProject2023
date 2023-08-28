package framework;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class TextureLoader {

	private static TextureLoader instance = null;

	public enum TextureName {
		Missing,
		PlayerIdle,
		PlayerRun,
		PlayerAttack,
		PlayerUppercut,
		PlayerJump,
		
		PlayerSwordIdle,
		PlayerSwordRun,
		PlayerSwordAttack,
		PlayerSwordStab,
		
		PlayerHammerIdle,
		PlayerHammerRun,
		PlayerHammerAttack,
		PlayerHammerVerticalSlam,
		
		BasicEnemyRun,
		BasicEnemyAttack,
		BasicEnemyIdle,
		BasicEnemyJump,
		
		StoneTiles,
		GrassTiles,
		RockTiles,
		DiagonalStoneTiles,
		WoodJumpThroughTiles,
	}

	private BufferedImage playerSheet = null;
	private BufferedImage blockSheet = null;
	private BufferedImage enemySheet = null;

	private TextureLoader() {
		try {
			FileIO fileIO = new FileIO();
			playerSheet = fileIO.loadSheet("player_sheet_64x64.png");
			enemySheet = fileIO.loadSheet("enemy_sheet_64x64.png");
			blockSheet = fileIO.loadSheet("block_sheet_16x16.png");
		} 
		catch(IOException e) {
			System.err.println("Failed to load an image file from the /res folder.");
			e.printStackTrace();
		}
	};

	public static TextureLoader getInstance() {
		if (instance == null)
			instance = new TextureLoader();
		return instance;
	}

	public BufferedImage[] getTextures(TextureName textureName) {
		BufferedImage[] textures = null;

		switch (textureName) {
		case PlayerIdle:
			textures = new BufferedImage[20];
			for (int i = 0; i < 10; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 1, 64, 64);
				textures[i + 10] = playerSheet.getSubimage(1 + i * 65, 66, 64, 64);
			}
			break;
		case PlayerRun:
			textures = new BufferedImage[16];
			for (int i = 0; i < 8; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 131, 64, 64);
				textures[i + 8] = playerSheet.getSubimage(1 + i * 65, 196, 64, 64);
			}
			break;
			// TODO
		case PlayerHammerAttack:
		case PlayerUppercut:
		case PlayerAttack:
			textures = new BufferedImage[16];
			for (int i = 0; i < 8; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 261, 64, 64);
				textures[i + 8] = playerSheet.getSubimage(1 + i * 65, 326, 64, 64);
			}
			break;
		case PlayerJump:
			textures = new BufferedImage[4];
			for (int i = 0; i < 2; i++) {
				textures[i] = playerSheet.getSubimage(521 + i * 65, 131, 64, 64);
				textures[i + 2] = playerSheet.getSubimage(521 + i * 65, 196, 64, 64);
			}
			break;
		case PlayerSwordIdle:
			textures = new BufferedImage[20];
			for (int i = 0; i < 10; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 391, 64, 64);
				textures[i + 10] = playerSheet.getSubimage(1 + i * 65, 456, 64, 64);
			}
			break;
		case PlayerSwordRun:
			textures = new BufferedImage[16];
			for (int i = 0; i < 8; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 521, 64, 64);
				textures[i + 8] = playerSheet.getSubimage(1 + i * 65, 586, 64, 64);
			}
			break;
		case PlayerSwordAttack:
			textures = new BufferedImage[12];
			for (int i = 0; i < 6; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 651, 64, 64);
				textures[i + 6] = playerSheet.getSubimage(1 + i * 65, 716, 64, 64);
			}
			break;
		case PlayerSwordStab:
			textures = new BufferedImage[14];
			for (int i = 0; i < 7; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 781, 64, 64);
				textures[i + 7] = playerSheet.getSubimage(1 + i * 65, 846, 64, 64);
			}
			break;
		case PlayerHammerIdle:
			textures = new BufferedImage[14];
			for (int i = 0; i < 7; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 911, 64, 64);
				textures[i + 7] = playerSheet.getSubimage(1 + i * 65, 976, 64, 64);
			}
			break;
		case PlayerHammerRun:
			textures = new BufferedImage[16];
			for (int i = 0; i < 8; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 1171, 64, 64);
				textures[i + 8] = playerSheet.getSubimage(1 + i * 65, 1236, 64, 64);
			}
			break;
		case PlayerHammerVerticalSlam:
			textures = new BufferedImage[28];
			for (int i = 0; i < 14; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 1041, 64, 64);
				textures[i + 14] = playerSheet.getSubimage(1 + i * 65, 1106, 64, 64);
			}
			break;
		case BasicEnemyIdle:
			textures = new BufferedImage[20];
			for (int i = 0; i < 10; i++) {
				textures[i] = enemySheet.getSubimage(1 + i * 65, 1, 64, 64);
				textures[i + 10] = enemySheet.getSubimage(1 + i * 65, 66, 64, 64);
			}
			break;
		case BasicEnemyRun:
			textures = new BufferedImage[16];
			for (int i = 0; i < 8; i++) {
				textures[i] = enemySheet.getSubimage(1 + i * 65, 131, 64, 64);
				textures[i + 8] = enemySheet.getSubimage(1 + i * 65, 196, 64, 64);
			}
			break;
		case BasicEnemyAttack:
			textures = new BufferedImage[16];
			for (int i = 0; i < 8; i++) {
				textures[i] = enemySheet.getSubimage(1 + i * 65, 261, 64, 64);
				textures[i + 8] = enemySheet.getSubimage(1 + i * 65, 326, 64, 64);
			}
			break;
		case BasicEnemyJump:
			textures = new BufferedImage[4];
			for (int i = 0; i < 2; i++) {
				textures[i] = enemySheet.getSubimage(521 + i * 65, 131, 64, 64);
				textures[i + 2] = enemySheet.getSubimage(521 + i * 65, 196, 64, 64);
			}
			break;
		case DiagonalStoneTiles:
			textures = new BufferedImage[4];
			for (int i = 0; i < 4; i++)
				textures[i] = blockSheet.getSubimage(1 + i * 17, 52, 16, 16);
			break;
		case GrassTiles:
			textures = new BufferedImage[13];
			for (int i = 0; i < textures.length; i++)
				textures[i] = blockSheet.getSubimage(1 + i * 17, 18, 16, 16);
			break;
		case RockTiles:
			textures = new BufferedImage[13];
			for (int i = 0; i < textures.length; i++)
				textures[i] = blockSheet.getSubimage(1 + i * 17, 35, 16, 16);
			break;
		case StoneTiles:
			textures = new BufferedImage[13];
			for (int i = 0; i < textures.length; i++)
				textures[i] = blockSheet.getSubimage(1 + i * 17, 1, 16, 16);
			break;
		case WoodJumpThroughTiles:
			textures = new BufferedImage[3];
			for (int i = 0; i < textures.length; i++)
				textures[i] = blockSheet.getSubimage(1 + i * 17, 69, 16, 16);
			break;
		default:
			textures = new BufferedImage[] {blockSheet.getSubimage(222, 1, 16, 16)};
			break;
		}

		return textures;
	}
	
	public BufferedImage[] getTexturesByDirection(TextureName textureName, int direction) {
		if (direction != 1 && direction != -1)
			throw new IllegalArgumentException("Invalid direction value: " + direction);
		
		BufferedImage[] textures = getTextures(textureName);
		BufferedImage[] directionalTextures;
		// Right
		if (direction == 1)
			directionalTextures = Arrays.copyOfRange(textures, 0, textures.length / 2);
		// Left
		else
			directionalTextures = Arrays.copyOfRange(textures, textures.length / 2, textures.length);
		
		return directionalTextures;
	}

}
