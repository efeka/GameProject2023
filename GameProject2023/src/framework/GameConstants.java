package framework;

import java.awt.Font;

public interface GameConstants {

	/*
	 * TODO:
	 * Calculate a variable which declares a "unit speed" for the game.
	 * This unit speed should be automatically adjusted using the game's scale.
	 * 
	 * Calculation:
	 * The base tile size is 48. And for speed, let's take the player's speed as a basis.
	 * unitSpeed = playerSpeed * (baseTileSize * scale) / baseTileSize
	 * We can then use this unitSpeed to replace all velocities in the game.
	 * For example, the players horizontal speed would now be equal to unitSpeed.
	 *		This might be unnecessary, it may be possible to multiply all speeds with scale and
	 *		still get accurate results.
	 *
	 * TODO:
	 * Fix the resolution issue.
	 * Start the game as full screen and store the window width and height.
	 * 
	 * Calculate tile size:
	 * tileSize = min(WIDTH / COLUMNS, HEIGHT / ROWS)
	 * Add letter boxing and center the game.
	 */
	
	public static class ScaleConstants {
		private static final int DEFAULT_TILE_SIZE = 48;
		private static final int DEFAULT_PLAYER_WIDTH = 64;
		private static final int DEFAULT_PLAYER_HEIGHT = 64;
		
		public static final float SCALE = 1f;
		public static final int TILE_COLUMNS = 32;
		public static final int TILE_ROWS = 18;
		
		public static final int TILE_SIZE = (int) (DEFAULT_TILE_SIZE * SCALE);
		public static final int PLAYER_WIDTH = (int) (DEFAULT_PLAYER_WIDTH * SCALE);
		public static final int PLAYER_HEIGHT = (int) (DEFAULT_PLAYER_HEIGHT * SCALE);
		
		public static final int GAME_WIDTH = TILE_SIZE * TILE_COLUMNS;
		public static final int GAME_HEIGHT = TILE_SIZE * TILE_ROWS;
	}
	
	public static class PhysicsConstants {
		public static final float GRAVITY = 0.2f;
		public static final int TERMINAL_VELOCITY = 15;		
	}
	
	public static class FontConstants {
		public static final Font DAMAGE_FONT = new Font("Calibri", Font.PLAIN, 20);
	}
	
}
