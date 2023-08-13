package framework;

public class ObjectId {
	
	public enum Category {
		Player,
		Block,
		DiagonalBlock,
		Enemy,
		Menu,
	}
	
	public enum Name {
		Player(1),
		GrassBlock(2),
		StoneTileBlock(3),
		DiagonalStoneBlock(4),
		BasicEnemy(5),
		HUD(6);
		
		private int value;

		Name(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
	    public static Name getByValue(int value) {
	        for (Name objectId : values())
	            if (objectId.getValue() == value)
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
	
}
