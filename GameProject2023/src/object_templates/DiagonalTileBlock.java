package object_templates;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import framework.GameObject;
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
		
		switch (orientation) {
		case OuterTopLeft:
			diagonalDirection = DiagonalDirection.AscendLeft;
			break;
		case OuterBottomLeft:
			diagonalDirection = DiagonalDirection.DescendLeft;
			break;
		case OuterBottomRight:
			diagonalDirection = DiagonalDirection.DescendRight;
			break;
		case OuterTopRight:
			diagonalDirection = DiagonalDirection.AscendRight;
			break;
		default:
			throw new IllegalArgumentException("Orientation does not represent a diagonal.");
		}
	}
	
	public DiagonalDirection getDiagonalDirection() {
		return diagonalDirection;
	}
}