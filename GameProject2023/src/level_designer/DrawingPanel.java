package level_designer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import framework.FileIO;
import framework.ObjectId.Name;

public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener, OptionsPanel.OptionSelectionListener {

	private static final long serialVersionUID = -7181166224947191192L;

	private boolean leftMousePressed = false;
	private boolean rightMousePressed = false;

	private LevelGrid levelGrid;
	private boolean showGrid = true;

	private BufferedImage selectedImage = null;
	private Name selectedObjectName = null;
	private Color selectedBackgroundColor = new Color(51, 51, 51);

	public DrawingPanel(int width, int height) {
		setSize(width, height);
		addMouseMotionListener(this);
		addMouseListener(this);

		setupGrid();

		setVisible(true);
	}

	private void setupGrid() {
		levelGrid = new LevelGrid(0, 0, getWidth(), getHeight());

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				levelGrid.resizeGrid(getWidth(), getHeight());
				repaint();
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Grid background
		g.setColor(selectedBackgroundColor);
		int cellSize = levelGrid.cellSize;
		g.fillRect(levelGrid.xOffset, levelGrid.yOffset, levelGrid.cols * cellSize, levelGrid.rows * cellSize);

		// Grid cells
		int backgroundColorAverage = (selectedBackgroundColor.getRed() +
				selectedBackgroundColor.getGreen() +
				selectedBackgroundColor.getBlue()) / 3;
		Color cellBorderColor = backgroundColorAverage < 127 ? 
				new Color(150, 150, 150) :
				new Color(51, 51, 51);
		
		g.setColor(cellBorderColor);
		GridCell[][] grid = levelGrid.getGrid();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				GridCell cell = grid[i][j];

				if (cell.image != null)
					g.drawImage(cell.image, cell.x, cell.y, cell.size, cell.size, null);

				if (showGrid)
					g.drawRect(cell.x, cell.y, cell.size, cell.size);	
			}
		}
		
		if (selectedObjectName != null) {
			g.setColor(Color.BLACK);
			g.drawString(selectedObjectName.toString(), 5, getHeight() - 5);
		}
	}

	@Override
	public void onGameObjectSelected(BufferedImage selectedImage, Name objectName) {
		this.selectedImage = selectedImage;
		this.selectedObjectName = objectName;
		repaint();
	}

	@Override
	public void onBackgroundColorSelected(Color color) {
		selectedBackgroundColor = color;
		repaint();
	}

	@Override
	public void onGridToggle(boolean toggle) {
		showGrid = toggle;
		repaint();
	}

	@Override
	public void clearDesign() {
		GridCell[][] grid = levelGrid.getGrid();
		for (int i = 0; i < levelGrid.rows; i++)
			for (int j = 0; j < levelGrid.cols; j++)
				grid[i][j].clear();
		repaint();
	}

	/**
	 * Saves the contents of the drawing area into the levels file.
	 */
	@Override
	public void saveDesign() {
		int rows = levelGrid.rows;
		int cols = levelGrid.cols;

		int[][] objectUIDs = new int[rows][cols];
		GridCell[][] grid = levelGrid.getGrid();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Name objectName = grid[i][j].objectName;
				int uid = objectName != null ? objectName.getUID() : 0;
				objectUIDs[i][j] = uid;
			}
		}

		new FileIO().saveLevel("levels.txt", objectUIDs);

		JOptionPane.showMessageDialog(this, "Save successful");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftMousePressed = true;
			rightMousePressed = false;
		}
		else if (e.getButton() == MouseEvent.BUTTON3) {
			leftMousePressed = false;
			rightMousePressed = true;
		}
		else
			return;

		int cellRow = (e.getY() - levelGrid.yOffset) / levelGrid.cellSize;
		int cellCol = (e.getX() - levelGrid.xOffset) / levelGrid.cellSize;

		if (!isValidPosition(cellRow, cellCol))
			return;

		if (leftMousePressed) {
			levelGrid.getGrid()[cellRow][cellCol].image = selectedImage;
			levelGrid.getGrid()[cellRow][cellCol].objectName = selectedObjectName;
		}
		else if (rightMousePressed) {
			levelGrid.getGrid()[cellRow][cellCol].image = null;
			levelGrid.getGrid()[cellRow][cellCol].objectName = null;
		}

		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!leftMousePressed && !rightMousePressed)
			return;

		int cellRow = (e.getY() - levelGrid.yOffset) / levelGrid.cellSize;
		int cellCol = (e.getX() - levelGrid.xOffset) / levelGrid.cellSize;

		if (!isValidPosition(cellRow, cellCol))
			return;

		if (leftMousePressed) {
			levelGrid.getGrid()[cellRow][cellCol].image = selectedImage;
			levelGrid.getGrid()[cellRow][cellCol].objectName = selectedObjectName;
		}
		else if (rightMousePressed) {
			levelGrid.getGrid()[cellRow][cellCol].image = null;
			levelGrid.getGrid()[cellRow][cellCol].objectName = null;
		}

		repaint();
	}

	private boolean isValidPosition(int row, int col) {
		return row >= 0 &&
				row < levelGrid.rows &&
				col >= 0 &&
				col < levelGrid.cols;
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

}
