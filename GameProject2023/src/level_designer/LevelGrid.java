package level_designer;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;

import java.awt.image.BufferedImage;

import framework.ObjectId.Name;

public class LevelGrid {

	protected int rows = TILE_ROWS;
	protected int cols = TILE_COLUMNS;
	protected int xOffset;
	protected int yOffset;
	protected int cellSize;
	
	private GridCell[][] grid;
	
	public LevelGrid(int x, int y, int width, int height) {
		cellSize = Math.min(width / cols, height / rows);
		xOffset = (width - cols * cellSize) / 2;
		yOffset = (height - rows * cellSize) / 2;
		
		grid = new GridCell[rows][cols];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				grid[i][j] = new GridCell(xOffset + j * cellSize, yOffset + i * cellSize, cellSize);
	}
	
	public GridCell[][] getGrid() {
		return grid;
	}
	
	public void setCellData(int row, int col, BufferedImage image, Name objectName) {
		grid[row][col].image = image;
		grid[row][col].objectName = objectName;
	}
	
	public void resizeGrid(int width, int height) {
		cellSize = Math.min(width / cols, height / rows);
		xOffset = (width - cols * cellSize) / 2;
		yOffset = (height - rows * cellSize) / 2;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				GridCell cell = grid[i][j];
				
				cell.x = xOffset + j * cellSize;
				cell.y = yOffset + i * cellSize;
				cell.size = cellSize;
			}
		}
	}
	
}
