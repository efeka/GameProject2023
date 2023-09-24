package framework;

public class ObjectId {
	
	/**
	 * Defines a category for each object.
	 * Objects that are similar in behaviour should be under the same category.
	 * This approach simplifies processes such as collision detection.
	 */
	public enum Category {
		Missing,
		Player,
		Block,
		JumpThroughBlock,
		DiagonalBlock,
		Enemy,
		Menu,
		Item,
		WeaponItem,
		Projectile,
		Summon,
	}
	
	/**
	 * Name is used as a spesific descriptor for the object.
	 * This allows differentiating objects which are under the same category.
	 * The {@code uid} is used as a unique id for saving and loading
	 * levels from the save file into the game.
	 * 
	 * TODO: Many approaches was taken into consideration while deciding on
	 * how to handle objects with different types, such as TileBlocks with their
	 * various orientations.
	 * Finding a way to avoid having to add a new entry to this enum for each
	 * orientation of an object could simplify the code for LevelDesigner and
	 * get rid of reduntant code in this class.
	 */
	public enum Name {
		Missing(-1),
		
		Player(1),
		BasicEnemy(2),
		ArcherEnemy(3),
		BullEnemy(4),

		HUD(500),

		// Tile blocks
		StoneTileBlock_OuterTopLeft(1000),
		StoneTileBlock_OuterTop(1001),
		StoneTileBlock_OuterTopRight(1002),
		StoneTileBlock_OuterLeft(1003),
		StoneTileBlock_Center(1004),
		StoneTileBlock_OuterRight(1005),
		StoneTileBlock_OuterBottomLeft(1006),
		StoneTileBlock_OuterBottom(1007),
		StoneTileBlock_OuterBottomRight(1008),
		StoneTileBlock_InnerTopLeft(1009),
		StoneTileBlock_InnerTopRight(1010),
		StoneTileBlock_InnerBottomLeft(1011),
		StoneTileBlock_InnerBottomRight(1012),
		
		GrassTileBlock_OuterTopLeft(1013),
		GrassTileBlock_OuterTop(1014),
		GrassTileBlock_OuterTopRight(1015),
		GrassTileBlock_OuterLeft(1016),
		GrassTileBlock_Center(1017),
		GrassTileBlock_OuterRight(1018),
		GrassTileBlock_OuterBottomLeft(1019),
		GrassTileBlock_OuterBottom(1020),
		GrassTileBlock_OuterBottomRight(1021),
		GrassTileBlock_InnerTopLeft(1022),
		GrassTileBlock_InnerTopRight(1023),
		GrassTileBlock_InnerBottomLeft(1024),
		GrassTileBlock_InnerBottomRight(1025),
		GrassTileBlock_StandaloneLeft(1026),
		GrassTileBlock_StandaloneCenter(1027),
		GrassTileBlock_StandaloneRight(1028),
		
		RockTileBlock_OuterTopLeft(1029),
		RockTileBlock_OuterTop(1030),
		RockTileBlock_OuterTopRight(1031),
		RockTileBlock_OuterLeft(1032),
		RockTileBlock_Center(1033),
		RockTileBlock_OuterRight(1034),
		RockTileBlock_OuterBottomLeft(1035),
		RockTileBlock_OuterBottom(1036),
		RockTileBlock_OuterBottomRight(1037),
		RockTileBlock_InnerTopLeft(1038),
		RockTileBlock_InnerTopRight(1039),
		RockTileBlock_InnerBottomLeft(1040),
		RockTileBlock_InnerBottomRight(1041),
		
		GrassBackgroundTileBlock_OuterTopLeft(1042),
		GrassBackgroundTileBlock_OuterTop(1043),
		GrassBackgroundTileBlock_OuterTopRight(1044),
		GrassBackgroundTileBlock_OuterLeft(1045),
		GrassBackgroundTileBlock_Center(1046),
		GrassBackgroundTileBlock_OuterRight(1047),
		GrassBackgroundTileBlock_OuterBottomLeft(1048),
		GrassBackgroundTileBlock_OuterBottom(1049),
		GrassBackgroundTileBlock_OuterBottomRight(1050),
		GrassBackgroundTileBlock_InnerTopLeft(1051),
		GrassBackgroundTileBlock_InnerTopRight(1052),
		GrassBackgroundTileBlock_InnerBottomLeft(1053),
		GrassBackgroundTileBlock_InnerBottomRight(1054),
		
		DiagonalStoneTileBlock_OuterTopLeft(2000),
		DiagonalStoneTileBlock_OuterTopRight(2001),
		DiagonalStoneTileBlock_OuterBottomLeft(2002),
		DiagonalStoneTileBlock_OuterBottomRight(2003),
		
		WoodJumpThroughTileBlock_OuterLeft(2004),
		WoodJumpThroughTileBlock_Center(2005),
		WoodJumpThroughTileBlock_OuterRight(2006),
		
		// Items
		Coin(2999),
		FisticuffsItem(3000),
		SwordItem(3001),
		HammerItem(3002),
		HealthPotionItem(3003),
		;
		
		private int uid;

		Name(int uid) {
			this.uid = uid;
		}
		
		public int getUID() {
			return uid;
		}
		
	    public static Name getByUID(int uid) {
	        for (Name objectId : values())
	            if (objectId.getUID() == uid)
	                return objectId;
	        return null;
	    }
	}
	
	private Category category;
	private Name name;
	
	public ObjectId(Category category, Name name) {
		this.category = category;
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public Name getName() {
		return name;
	}
	
	// Used for easily copying and pasting new entries into the Name enum.
	/*
	public static void main(String[] args) {
		String objectName = "GrassBackgroundTileBlock";
		TileOrientation[] orientations = TileOrientation.values();
		int baseUID = 1042;
		
		for (TileOrientation o : orientations)	
			System.out.println(objectName + "_" + o.toString() + "(" + baseUID++ + "),");
	}
	*/
	
}
