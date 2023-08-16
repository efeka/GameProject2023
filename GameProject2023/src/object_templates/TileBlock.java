package object_templates;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.image.BufferedImage;

import framework.GameObject;
import framework.ObjectId;

public abstract class TileBlock extends GameObject {

	public TileBlock(int x, int y, ObjectId.Name objectName, TileOrientation orientation) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(ObjectId.Category.Block, objectName));
	}
	
	public TileBlock(TileOrientation orientation) {
		super();
	}
	
	protected BufferedImage getImageByOrientation(BufferedImage[] tileSet, TileOrientation orientation) {
		return tileSet[orientation.getValue()];
	}
	
}
