package framework;

public class ObjectId {
	
	/**
	 * Defines a category for each object.
	 * Objects that are similar in behaviour should be under the same category.
	 * This approach simplifies processes such as collision detection.
	 */
	public enum Category {
		Player,
		Block,
		DiagonalBlock,
		Enemy,
		Menu,
	}
	
	/**
	 * Name is used as a spesific descriptor for the object.
	 * This allows differentiating objects which are under the same category.
	 * The {@code uid} field is used as a unique id, used for saving and loading
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
		Player(1),
		Menu(2),

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
		
		DiagonalStoneTileBlock_OuterTopLeft(2000),
		DiagonalStoneTileBlock_OuterTopRight(2001),
		DiagonalStoneTileBlock_OuterBottomLeft(2002),
		DiagonalStoneTileBlock_OuterBottomRight(2003),
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
		String objectName = "StoneTileBlock";
		TileOrientation[] orientations = TileOrientation.values();
		int baseUID = 1000;
		
		for (TileOrientation o : orientations)	
			System.out.println(objectName + "_" + o.toString() + "(" + baseUID++ + "),");
	}
	*/
	
}
