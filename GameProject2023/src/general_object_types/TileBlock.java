package general_object_types;

import framework.GameObject;
import framework.ObjectId;

public abstract class TileBlock extends GameObject {
	
	/**
	 * TextureLoader loads sprites in the same order as the values of this enum.
	 * Therefore, tile sets drawn in sprite sheets should follow this value order.
	 * This simplifies indexing and GameObject-Texture coupling.
	 */
	public enum Orientation {
		OuterTopLeft(0),
		OuterTop(1),
		OuterTopRight(2),
		OuterLeft(3),
		Center(4),
		OuterRight(5),
		OuterBottomLeft(6),
		OuterBottom(7),
		OuterBottomRight(8),
		InnerTopLeft(9),
		InnerTopRight(10),
		InnerBottomLeft(11),
		InnerBottomRight(12);
		
		private int value;
		
		Orientation(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
	}

	public TileBlock(int x, int y, int width, int height, ObjectId.Name objectName) {
		super(x, y, width, height, new ObjectId(ObjectId.Category.Block, objectName));
	}
	
}
