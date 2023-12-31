package abstracts;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.image.BufferedImage;

import framework.ObjectId;

public abstract class TileBlock extends GameObject {

	public TileBlock(int x, int y, ObjectId.Name objectName, TileOrientation orientation) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(ObjectId.Category.Block, objectName));
	}
	
	protected BufferedImage getImageByOrientation(BufferedImage[] tileSet, TileOrientation orientation) {
		return tileSet[orientation.getValue()];
	}
	
}
