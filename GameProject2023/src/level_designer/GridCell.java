package level_designer;

import java.awt.image.BufferedImage;

import framework.ObjectId.Name;

public class GridCell {

	protected BufferedImage image;
	protected int x, y, size;
	protected Name objectName;
	
	public GridCell(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
	}
	
	public void clear() {
		image = null;
		objectName = null;
	}
	
}
