package general_object_types;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import framework.GameObject;
import framework.ObjectId;

public abstract class Block extends GameObject {

	public Block(int x, int y, ObjectId.Name objectName) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(ObjectId.Category.Block, objectName));
	}
	
}
