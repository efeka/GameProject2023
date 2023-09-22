package ui;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;
import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import abstract_templates.GameObject;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import window.KeyInput;
import window.MouseInput;

public class Inventory extends GameObject {

	private KeyInput keyInput;
	private MouseInput mouseInput;

	private BufferedImage[] textures;

	private int cellSize = (int) (TILE_SIZE * 1.5f);
	private int rows, cols;
	private boolean[][] inventorySlots;

	public Inventory(int rows, int cols, KeyInput keyInput, MouseInput mouseInput) {
		super(0, 0, 0, 0, new ObjectId(Category.Menu, Name.Missing));
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		this.rows = rows;
		this.cols = cols;

		inventorySlots = new boolean[rows][cols];

		textures = TextureLoader.getInstance().getTextures(TextureName.Inventory);
		texture = textures[9];
	}

	@Override
	public void tick() {
		if (keyInput.isInventoryKeyToggled())
			return;

		// Calculate the centered inventory window position
		int screenWidth = TILE_COLUMNS * TILE_SIZE;
		int screenHeight = TILE_ROWS * TILE_SIZE;
		int invWidth = (cols + 2) * cellSize;
		int invHeight = (rows + 2) * cellSize;
		x = (screenWidth - invWidth) / 2;
		y = (screenHeight - invHeight) / 2;
	}

	@Override
	public void render(Graphics g) {
		if (keyInput.isInventoryKeyToggled())
			return;

		// Inventory text
		g.drawImage(textures[9], (int) x, (int) y - cellSize, cellSize * 4, cellSize * 2, null);
		
		// Background
		g.drawImage(textures[4], (int) x + cellSize, (int) (y + cellSize), cols * cellSize, rows * cellSize, null);
		
		// Corners
		g.drawImage(textures[0], (int) x, (int) y, cellSize, cellSize, null);
		g.drawImage(textures[2], (int) x + (cols + 1) * cellSize, (int) y, cellSize, cellSize, null);
		g.drawImage(textures[6], (int) x, (int) y + (rows + 1) * cellSize, cellSize, cellSize, null);
		g.drawImage(textures[8], (int) x + (cols + 1) * cellSize, (int) y + (rows + 1) * cellSize, cellSize, cellSize, null);
		
		// Edges
		g.drawImage(textures[1], (int) x + cellSize, (int) y, cols * cellSize, cellSize, null);
		g.drawImage(textures[3], (int) x, (int) y + cellSize, cellSize, rows * cellSize, null);
		g.drawImage(textures[5], (int) x + (cols + 1) * cellSize, (int) y + cellSize, cellSize, rows * cellSize, null);
		g.drawImage(textures[7], (int) x + cellSize, (int) y + (rows + 1) * cellSize, cols * cellSize, cellSize, null);
		
		// Inventory Slots
		for (int i = 1; i < inventorySlots.length + 1; i++) {
			for (int j = 1; j < inventorySlots[0].length + 1; j++) {
				int slotTextureIndex = -1;
				Rectangle slotRect = new Rectangle((int) x + j * cellSize, (int) y + i * cellSize, cellSize, cellSize);
				
				if (inventorySlots[i - 1][j - 1])
					slotTextureIndex = checkMouseHover(slotRect) ? 13 : 12;
				else
					slotTextureIndex = checkMouseHover(slotRect) ? 11 : 10;
				
				BufferedImage slotTexture = textures[slotTextureIndex];
				g.drawImage(slotTexture, (int) x + j * cellSize, (int) y + i * cellSize, cellSize, cellSize, null);
			}
		}
	}

	private boolean checkMouseHover(Rectangle rectangle) {
		return rectangle.contains(mouseInput.getX(), mouseInput.getY());
	}

}
