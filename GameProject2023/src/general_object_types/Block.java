package general_object_types;

import framework.GameObject;
import framework.ObjectId;

public abstract class Block extends GameObject {

	public Block(int x, int y, int width, int height, ObjectId.Name objectName) {
		super(x, y, width, height, new ObjectId(ObjectId.Category.Block, objectName));
	}
	
}
