package framework;

import java.awt.Font;

public interface GameConstants {

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
	
	public static class CreatureConstants {
		public static final float GRAVITY = 0.2f;
		public static final int TERMINAL_VELOCITY = 15;		
	}
	
	public static class FontConstants {
		public static final Font DAMAGE_FONT = new Font("Calibri", Font.PLAIN, 20);
	}
	
}
