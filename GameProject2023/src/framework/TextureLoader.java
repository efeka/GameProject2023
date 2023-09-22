package framework;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class TextureLoader {

	private static TextureLoader instance = null;

	public enum TextureName {
		Missing,
		
		// Player / Weapons
		PlayerIdle,
		PlayerRun,
		PlayerAttack,
		PlayerUppercut,
		PlayerJump,
		PlayerDoubleJump,
		PlayerDodge,
		PlayerPoisonGlow,
		PlayerIceGlow,
		
		PlayerSwordIdle,
		PlayerSwordRun,
		PlayerSwordAttack,
		PlayerSwordStab,
		PlayerSwordThrow,
		PlayerSwordSwing,
		PlayerSwordDash,
		
		PlayerHammerIdle,
		PlayerHammerRun,
		PlayerHammerAttack,
		PlayerHammerVerticalSlam,
		PlayerHammerSwing,
		PlayerHammerProjectile,
		
		// Enemies
		BasicEnemyRun,
		BasicEnemyAttack,
		BasicEnemyIdle,
		BasicEnemyJump,
		
		ArcherEnemyIdle,
		ArcherEnemyShootTorso,
		ArcherEnemyShootLegs,
		
		BullEnemyRun,
		BullEnemyStunned,
		
		// Tiles
		StoneTiles,
		GrassTiles,
		GrassBackgroundTiles,
		RockTiles,
		DiagonalStoneTiles,
		WoodJumpThroughTiles,
		
		// Items
		BronzeCoin,
		SilverCoin,
		GoldCoin,
		FisticuffsItem,
		SwordItem,
		HammerItem,
		
		// Projectiles
		SwordProjectile,
		ArrowProjectile,
		SwordSlashProjectile,
		
		// Effects
		SparkleEffect,
		DarkSmokeEffect,
		PoisonSmokeEffect,
		LightningEffect,
		
		SpikeAttack,
		IceSword,
		IceCircle,
		
		PoisonRatSummon,
		PoisonRatRun,
		PoisonRatAttack,
		
		// UI
		HealthBar,
		Cursor,
		Inventory,
	}

	private BufferedImage playerSheet = null;
	private BufferedImage blockSheet = null;
	private BufferedImage enemySheet = null;
	private BufferedImage itemSheet = null;
	private BufferedImage uiSheet = null;
 
	private TextureLoader() {
		try {
			FileIO fileIO = new FileIO();
			playerSheet = fileIO.loadSheet("player_sheet_64x64.png");
			enemySheet = fileIO.loadSheet("enemy_sheet_64x64.png");
			blockSheet = fileIO.loadSheet("block_sheet_16x16.png");
			itemSheet = fileIO.loadSheet("item_sheet_32x32.png");
			uiSheet = fileIO.loadSheet("ui_sheet.png");
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
		case Missing:
			textures = new BufferedImage[] {blockSheet.getSubimage(222, 1, 16, 16)};
			break;
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
		case PlayerDoubleJump:
			textures = new BufferedImage[12];
			for (int i = 0; i < 6; i++) {
				textures[i] = playerSheet.getSubimage(586 + i * 65, 261, 64, 64);
				textures[i + 6] = playerSheet.getSubimage(586 + i * 65, 326, 64, 64);
			}
			break;
		case PlayerDodge:
			textures = new BufferedImage[14];
			for (int i = 0; i < 7; i++) {
				textures[i] = playerSheet.getSubimage(521 + i * 65, 521, 64, 64);
				textures[i + 7] = playerSheet.getSubimage(521 + i * 65, 586, 64, 64);
			}
			break;
		case PlayerPoisonGlow:
			textures = new BufferedImage[9];
			for (int i = 0; i < textures.length; i++)
				textures[i] = playerSheet.getSubimage(1 + i * 65, 1301, 64, 64);
			break;
		case PlayerIceGlow:
			textures = new BufferedImage[9];
			for (int i = 0; i < textures.length; i++)
				textures[i] = playerSheet.getSubimage(1 + i * 65, 1366, 64, 64);
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
		case PlayerSwordSwing:
			textures = new BufferedImage[12];
			for (int i = 0; i < 6; i++) {
				textures[i] = playerSheet.getSubimage(456 + i * 65, 651, 64, 64);
				textures[i + 6] = playerSheet.getSubimage(456 + i * 65, 716, 64, 64);
			}
			break;
		case PlayerSwordDash:
			textures = new BufferedImage[4];
			for (int i = 0; i < textures.length; i++) 
				textures[i] = playerSheet.getSubimage(716 + i * 65, 391, 64, 64);
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
				textures[i] = playerSheet.getSubimage(1 + i * 65, 1041, 64, 64);
				textures[i + 8] = playerSheet.getSubimage(1 + i * 65, 1106, 64, 64);
			}
			break;
		case PlayerHammerVerticalSlam:
			textures = new BufferedImage[28];
			for (int i = 0; i < 14; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 1041, 64, 64);
				textures[i + 14] = playerSheet.getSubimage(1 + i * 65, 1106, 64, 64);
			}
			break;
		case PlayerHammerSwing:
			textures = new BufferedImage[20];
			for (int i = 0; i < 10; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 1301, 64, 64);
				textures[i + 10] = playerSheet.getSubimage(1 + i * 65, 1366, 64, 64);
			}
			break;
		case PlayerHammerProjectile:
			textures = new BufferedImage[1];
			textures[0] = playerSheet.getSubimage(651, 1301, 64, 64);
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
			textures = new BufferedImage[16];
			for (int i = 0; i < textures.length; i++)
				textures[i] = blockSheet.getSubimage(1 + i * 17, 18, 16, 16);
			break;
		case GrassBackgroundTiles:
			textures = new BufferedImage[13];
			for (int i = 0; i < textures.length; i++)
				textures[i] = blockSheet.getSubimage(1 + i * 17, 86, 16, 16);
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
		case BronzeCoin:
			textures = new BufferedImage[6];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 232, 32, 32);
			break;
		case SilverCoin:
			textures = new BufferedImage[6];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(199 + i * 33, 232, 32, 32);
			break;
		case GoldCoin:
			textures = new BufferedImage[6];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(199 + i * 33, 166, 32, 32);
			break;
		case FisticuffsItem:
			textures = new BufferedImage[10];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 1, 32, 32);
			break;
		case SwordItem:
			textures = new BufferedImage[8];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 34, 32, 32);
			break;
		case HammerItem:
			textures = new BufferedImage[6];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 67, 32, 32);
			break;
		case SwordProjectile:
			textures = new BufferedImage[8];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 100, 32, 32);
			break;
		case SwordSlashProjectile:
			textures = new BufferedImage[4];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(298 + i * 33, 34, 32, 32);
			break;
		case PlayerSwordThrow:
			textures = new BufferedImage[6];
			for (int i = 0; i < 3; i++) {
				textures[i] = playerSheet.getSubimage(1 + i * 65, 651, 64, 64);
				textures[i + 3] = playerSheet.getSubimage(1 + i * 65, 716, 64, 64);
			}
			break;
		case ArrowProjectile:
			textures = new BufferedImage[3];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 133, 32, 32);
			break;
		case ArcherEnemyIdle:
			textures = new BufferedImage[10];
			for (int i = 0; i < textures.length; i++)
				textures[i] = enemySheet.getSubimage(1 + i * 65, 1041, 64, 64);
			break;
		case ArcherEnemyShootTorso:
			textures = new BufferedImage[18];
			for (int i = 0; i < 9; i++) {
				textures[i] = enemySheet.getSubimage(1 + i * 65, 1106, 64, 64);
				textures[i + 9] = enemySheet.getSubimage(1 + i * 65, 1171, 64, 64);
			}
			break;
		case ArcherEnemyShootLegs:
			textures = new BufferedImage[18];
			for (int i = 0; i < 9; i++) {
				textures[i] = enemySheet.getSubimage(1 + i * 65, 1236, 64, 64);
				textures[i + 9] = enemySheet.getSubimage(1 + i * 65, 1301, 64, 64);
			}
			break;
		case BullEnemyRun:
			textures = new BufferedImage[16];
			for (int i = 0; i < 8; i++) {
				textures[i] = enemySheet.getSubimage(1 + i * 65, 1366, 64, 64);
				textures[i + 8] = enemySheet.getSubimage(1 + i * 65, 1431, 64, 64);
			}
			break;
		case BullEnemyStunned:
			textures = new BufferedImage[36];
			for (int i = 0; i < 10; i++)
				textures[i] = enemySheet.getSubimage(1 + i * 65, 1496, 64, 64);
			for (int i = 0; i < 8; i++)
				textures[i + 10] = enemySheet.getSubimage(1 + i * 65, 1561, 64, 64);
			for (int i = 0; i < 10; i++)
				textures[i + 18] = enemySheet.getSubimage(1 + i * 65, 1626, 64, 64);
			for (int i = 0; i < 8; i++)
				textures[i + 28] = enemySheet.getSubimage(1 + i * 65, 1691, 64, 64);
			break;
		case SparkleEffect:
			textures = new BufferedImage[5];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 199, 32, 32);
			break;
		case DarkSmokeEffect:
			textures = new BufferedImage[8];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(265 + i * 33, 67, 32, 32);
			break;
		case PoisonSmokeEffect:
			textures = new BufferedImage[8];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(265 + i * 33, 100, 32, 32);
			break;
		case LightningEffect:
			textures = new BufferedImage[13];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 397, 32, 32);
			break;
		case SpikeAttack:
			textures = new BufferedImage[14];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 265, 32, 32);
			break;
		case IceSword:
			textures = new BufferedImage[1];
			textures[0] = itemSheet.getSubimage(298, 34, 32, 32);
			break;
		case IceCircle:
			textures = new BufferedImage[8];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 65, 298, 64, 64);
			break;
		case PoisonRatSummon:
			textures = new BufferedImage[16];
			for (int i = 0; i < textures.length / 2; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 298, 32, 32);
			for (int i = textures.length / 2; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + (i - textures.length / 2) * 33, 364, 32, 32);
			break;
		case PoisonRatRun:
			textures = new BufferedImage[8];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(265 + i * 33, 298, 32, 32);
			break;
		case PoisonRatAttack:
			textures = new BufferedImage[12];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(1 + i * 33, 331, 32, 32);
			break;
		case HealthBar:
			textures = new BufferedImage[3];
			for (int i = 0; i < textures.length; i++)
				textures[i] = itemSheet.getSubimage(331 + i * 33, 1, 32, 32);
			break;
		case Cursor:
			textures = new BufferedImage[1];
			textures[0] = uiSheet.getSubimage(1, 85, 16, 16);
			break;
		case Inventory:
			textures = new BufferedImage[14];
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					textures[3 * i + j] = uiSheet.getSubimage(1 + 17 * j, 1 + 17 * i, 16, 16);
			textures[9] = uiSheet.getSubimage(1, 52, 64, 32);
			textures[10] = uiSheet.getSubimage(52, 1, 16, 16);
			textures[11] = uiSheet.getSubimage(69, 1, 16, 16);
			textures[12] = uiSheet.getSubimage(52, 18, 16, 16);
			textures[13] = uiSheet.getSubimage(69, 18, 16, 16);
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
