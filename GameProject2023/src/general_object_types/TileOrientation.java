package general_object_types;

/**
 * TextureLoader loads sprites in the same order as the values of this enum.
 * Therefore, tile sets drawn in sprite sheets should follow this value order.
 * This simplifies indexing and GameObject-Texture coupling.
 */
public enum TileOrientation {

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

	TileOrientation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
