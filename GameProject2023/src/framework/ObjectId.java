package framework;

public class ObjectId {
	
	public enum Category {
		Player,
		Block,
		Enemy,
	}
	
	public enum Name {
		Player(1),
		GrassBlock(2),
		BasicEnemy(3);
		
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
	
	public Category category;
	public Name name;
	
	public ObjectId(Category category, Name name) {
		this.category = category;
		this.name = name;
	}
	
}
