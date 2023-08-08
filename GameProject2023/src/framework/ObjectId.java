package framework;

public class ObjectId {
	
	public enum Category {
		Player,
		Block,
		Enemy,
	}
	
	public enum Name {
		Player,
		GrassBlock,
		BasicEnemy,
	}
	
	public Category category;
	public Name name;
	
	public ObjectId(Category category, Name name) {
		this.category = category;
		this.name = name;
	}
	
}
