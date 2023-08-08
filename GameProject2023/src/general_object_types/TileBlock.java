package general_object_types;

import framework.GameObject;
import framework.ObjectId;

public abstract class TileBlock extends GameObject {

	public TileBlock(int x, int y, int width, int height, ObjectId.Name objectName, TileOrientation orientation) {
		super(x, y, width, height, new ObjectId(ObjectId.Category.Block, objectName));
	}
	
}
