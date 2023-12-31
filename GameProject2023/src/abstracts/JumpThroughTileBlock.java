package abstracts;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

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
			texture = TextureLoader.getInstance().getTextures(TextureName.Missing)[0];
			break;
		}
		return texture;
	}
	
	@Override
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, width, height / 3);
	}

}
