package level_designer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

	private LevelGrid[] levelGrids;
	private boolean showGrid = true;
	private int selectedLayer = 1;
	private float nonselectedLayerTransparency = 0.4f;

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
		levelGrids = new LevelGrid[3];
		for (int i = 0; i < levelGrids.length; i++)
			levelGrids[i] = new LevelGrid(0, 0, getWidth(), getHeight());

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				for (int i = 0; i < levelGrids.length; i++)
					levelGrids[i].resizeGrid(getWidth(), getHeight());
				repaint();
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Grid background
		g.setColor(selectedBackgroundColor);
		int cellSize = levelGrids[0].cellSize;
		g.fillRect(levelGrids[0].xOffset, levelGrids[0].yOffset, levelGrids[0].cols * cellSize, levelGrids[0].rows * cellSize);

		// Grid cell frames
		int backgroundColorAverage = (selectedBackgroundColor.getRed() +
				selectedBackgroundColor.getGreen() +
				selectedBackgroundColor.getBlue()) / 3;
		Color cellBorderColor = backgroundColorAverage < 127 ? 
				new Color(150, 150, 150) :
					new Color(51, 51, 51);

		// Grid cells
		g.setColor(cellBorderColor);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nonselectedLayerTransparency));
		
		GridCell[][] gridBg = levelGrids[0].getGrid();
		GridCell[][] grid = levelGrids[1].getGrid();
		GridCell[][] gridFg = levelGrids[2].getGrid();
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				GridCell cellBg = gridBg[i][j];
				GridCell cell = grid[i][j];
				GridCell cellFg = gridFg[i][j];

				if (cellBg.image != null) {
					if (selectedLayer == 0 || selectedLayer == 3)
						g.drawImage(cellBg.image, cellBg.x, cellBg.y, cellBg.size, cellBg.size, null);
					else
						g2d.drawImage(cellBg.image, cellBg.x, cellBg.y, cellBg.size, cellBg.size, null);
				}
				if (cell.image != null) {
					if (selectedLayer == 1 || selectedLayer == 3)
						g.drawImage(cell.image, cell.x, cell.y, cell.size, cell.size, null);
					else
						g2d.drawImage(cell.image, cell.x, cell.y, cell.size, cell.size, null);
				}
				if (cellFg.image != null) {
					if (selectedLayer == 2 || selectedLayer == 3)
						g.drawImage(cellFg.image, cellFg.x, cellFg.y, cellFg.size, cellFg.size, null);
					else
						g2d.drawImage(cellFg.image, cellFg.x, cellFg.y, cellFg.size, cellFg.size, null);
				}

				if (showGrid)
					g.drawRect(cell.x, cell.y, cell.size, cell.size);	
			}
		}	
		g2d.dispose();

		// Display the selected objects name
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
	public void onLayerSelect(int index) {
		selectedLayer = index;
		repaint();
	}
	
	@Override
	public void onTransparencySelect(float transparency) {
		nonselectedLayerTransparency = transparency;
		repaint();
	}

	@Override
	public void clearLayer() {
		// Clear all layers
		if (selectedLayer == 3) {
			for (int i = 0; i < levelGrids.length; i++) {
				GridCell[][] grid = levelGrids[i].getGrid();
				for (int j = 0; j < grid.length; j++)
					for (int k = 0; k < grid[j].length; k++)
						grid[j][k].clear();
			}
		}
		// Only clear the selected layer
		else {
			GridCell[][] grid = levelGrids[selectedLayer].getGrid();
			for (int j = 0; j < grid.length; j++)
				for (int k = 0; k < grid[j].length; k++)
					grid[j][k].clear();
		}
		repaint();
	}

	/**
	 * Saves the contents of the drawing area into the txt files.
	 */
	@Override
	public void saveDesign() {
		int rows = levelGrids[0].rows;
		int cols = levelGrids[0].cols;

		int[][][] objectUIDs = new int[3][rows][cols];
		GridCell[][] gridBg = levelGrids[0].getGrid();
		GridCell[][] grid = levelGrids[1].getGrid();
		GridCell[][] gridFg = levelGrids[2].getGrid();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Name objectNameBg = gridBg[i][j].objectName;
				Name objectName = grid[i][j].objectName;
				Name objectNameFg = gridFg[i][j].objectName;

				objectUIDs[0][i][j] = objectNameBg != null ? objectNameBg.getUID() : 0;
				objectUIDs[1][i][j] = objectName != null ? objectName.getUID() : 0;
				objectUIDs[2][i][j] = objectNameFg != null ? objectNameFg.getUID() : 0;
			}
		}

		new FileIO().saveLevel("levels_bg.txt", objectUIDs[0]);
		new FileIO().saveLevel("levels.txt", objectUIDs[1]);
		new FileIO().saveLevel("levels_fg.txt", objectUIDs[2]);

		JOptionPane.showMessageDialog(this, "Save successful");

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (selectedLayer == 3)
			return;

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

		int cellRow = (e.getY() - levelGrids[selectedLayer].yOffset) / levelGrids[selectedLayer].cellSize;
		int cellCol = (e.getX() - levelGrids[selectedLayer].xOffset) / levelGrids[selectedLayer].cellSize;

		if (!isValidPosition(cellRow, cellCol))
			return;

		if (leftMousePressed) {
			levelGrids[selectedLayer].getGrid()[cellRow][cellCol].image = selectedImage;
			levelGrids[selectedLayer].getGrid()[cellRow][cellCol].objectName = selectedObjectName;
		}
		else if (rightMousePressed) {
			levelGrids[selectedLayer].getGrid()[cellRow][cellCol].image = null;
			levelGrids[selectedLayer].getGrid()[cellRow][cellCol].objectName = null;
		}

		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selectedLayer == 3)
			return;
		if (!leftMousePressed && !rightMousePressed)
			return;

		int cellRow = (e.getY() - levelGrids[selectedLayer].yOffset) / levelGrids[selectedLayer].cellSize;
		int cellCol = (e.getX() - levelGrids[selectedLayer].xOffset) / levelGrids[selectedLayer].cellSize;

		if (!isValidPosition(cellRow, cellCol))
			return;

		if (leftMousePressed) {
			levelGrids[selectedLayer].getGrid()[cellRow][cellCol].image = selectedImage;
			levelGrids[selectedLayer].getGrid()[cellRow][cellCol].objectName = selectedObjectName;
		}
		else if (rightMousePressed) {
			levelGrids[selectedLayer].getGrid()[cellRow][cellCol].image = null;
			levelGrids[selectedLayer].getGrid()[cellRow][cellCol].objectName = null;
		}

		repaint();
	}

	private boolean isValidPosition(int row, int col) {
		return row >= 0 &&
				row < levelGrids[selectedLayer].rows &&
				col >= 0 &&
				col < levelGrids[selectedLayer].cols;
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
