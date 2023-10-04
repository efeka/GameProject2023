package abstracts;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.image.BufferedImage;

import framework.ObjectId;
import framework.ObjectId.Name;

public abstract class DiagonalTileBlock extends GameObject {

	/*
	 *    AscendLeft   /\   AscendRight
	 *                /  \
	 *                \  /
	 *    DescendLeft  \/   DescendRight
	 */
	protected enum DiagonalDirection {
	    AscendLeft,      // Ascending from bottom left to top right
	    AscendRight,     // Ascending from bottom right to top left
	    DescendLeft,     // Descending from top left to bottom right
	    DescendRight;    // Descending from top right to bottom left
	}
	protected DiagonalDirection diagonalDirection;
	
	public DiagonalTileBlock(float x, float y, Name objectName, TileOrientation orientation) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(ObjectId.Category.DiagonalBlock, objectName));
		
		diagonalDirection = getDirectionByOrientation(orientation);
	}

	protected BufferedImage getImageByOrientation(BufferedImage[] tileSet, TileOrientation orientation) {
		DiagonalDirection direction = getDirectionByOrientation(orientation);
		BufferedImage texture = null;
		
		switch (direction) {
		case AscendLeft:
			texture = tileSet[0]; 
			break;
		case AscendRight:
			texture = tileSet[1];
			break;
		case DescendLeft:
			texture = tileSet[2];
			break;
		case DescendRight:
			texture = tileSet[3];
			break;
		}
		
		return texture;
	}
	
	private DiagonalDirection getDirectionByOrientation(TileOrientation orientation) {
		DiagonalDirection direction = null;
		
		switch (orientation) {
		case OuterTopLeft:
			direction = DiagonalDirection.AscendLeft;
			break;
		case OuterBottomLeft:
			direction = DiagonalDirection.DescendLeft;
			break;
		case OuterBottomRight:
			direction = DiagonalDirection.DescendRight;
			break;
		case OuterTopRight:
			direction = DiagonalDirection.AscendRight;
			break;
		default:
			throw new IllegalArgumentException("Orientation does not represent a diagonal.");
		}
		
		return direction;
	}
	
	public DiagonalDirection getDiagonalDirection() {
		return diagonalDirection;
	}
	
}
