package object_templates;

import framework.GameObject;
import framework.ObjectId;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

public abstract class TileBlock extends GameObject {

	public TileBlock(int x, int y, ObjectId.Name objectName, TileOrientation orientation) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(ObjectId.Category.Block, objectName));
	}
	
}
