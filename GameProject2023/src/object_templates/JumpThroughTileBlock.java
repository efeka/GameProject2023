package object_templates;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.image.BufferedImage;

import framework.GameObject;
import framework.ObjectId;
import framework.TextureLoader;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public abstract class JumpThroughTileBlock extends GameObject {

	public JumpThroughTileBlock(float x, float y, Name objectName, TileOrientation orientation) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(Category.JumpThroughBlock, objectName));
	}

	protected BufferedImage getImageByOrientation(BufferedImage[] tileSet, TileOrientation orientation) {
		BufferedImage texture = null; 
				
		switch (orientation) {
		case OuterLeft:
			texture = tileSet[0];
			break;
		case Center:
			texture = tileSet[1];
			break;
		case OuterRight:
			texture = tileSet[2];
			break;
		default:
			texture = TextureLoader.getInstance().missingSprite;
			break;
		}
		return texture;
	}

}
