package ui;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;
import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import abstract_templates.GameObject;
import framework.GameConstants;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import items.Item;
import window.KeyInput;
import window.MouseInput;

public class Inventory extends GameObject {

	private class Slot {
		Item item;
		int currentStackSize;
	}
	
	private KeyInput keyInput;
	private MouseInput mouseInput;

	private BufferedImage[] textures;

	private int cellSize = (int) (TILE_SIZE * 1.5f);
	private int rows, cols;
	private Slot[][] inventorySlots;

	public Inventory(int rows, int cols, KeyInput keyInput, MouseInput mouseInput) {
		super(0, 0, 0, 0, new ObjectId(Category.Menu, Name.Missing));
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		this.rows = rows;
		this.cols = cols;

		inventorySlots = new Slot[rows][cols];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				inventorySlots[i][j] = new Slot();

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
		
		// Inventory slots and items
		for (int i = 1; i < inventorySlots.length + 1; i++) {
			for (int j = 1; j < inventorySlots[0].length + 1; j++) {
				int slotTextureIndex = -1;
				Rectangle slotRect = new Rectangle((int) x + j * cellSize, (int) y + i * cellSize, cellSize, cellSize);
				BufferedImage itemTexture = null;
				
				// TODO check equipped items and change slot texture
				// slotTextureIndex = checkMouseHover(slotRect) ? 13 : 12;				
				slotTextureIndex = checkMouseHover(slotRect) ? 11 : 10;
				if (inventorySlots[i - 1][j - 1].item != null)
					itemTexture = inventorySlots[i - 1][j - 1].item.getItemIcon();

				// Draw the slot texture
				BufferedImage slotTexture = textures[slotTextureIndex];
				g.drawImage(slotTexture, (int) x + j * cellSize, (int) y + i * cellSize, cellSize, cellSize, null);
				
				// Draw the item in the slot
				if (itemTexture != null) {
					int iconSize = (int) (4f * cellSize / 5);
					int iconOffset = (cellSize - iconSize) / 2;
					g.drawImage(itemTexture, (int) x + iconOffset + j * cellSize, (int) y + iconOffset + i * cellSize,
							iconSize, iconSize, null);
					
					// Write the current stack size of the item in the slot
					if (inventorySlots[i - 1][j - 1].currentStackSize > 1) {
						g.setColor(Color.BLACK);
						g.setFont(GameConstants.FontConstants.INVENTORY_FONT);
						String stackText = inventorySlots[i - 1][j - 1].currentStackSize + "";
						int stackTextX = (int) x + iconOffset + cellSize / 20 + j * cellSize;
						int stackTextY = (int) y + iconOffset + cellSize / 4 + i * cellSize;
						g.drawString(stackText, stackTextX, stackTextY);
					}
				}
			}
		}
	}

	private boolean checkMouseHover(Rectangle rectangle) {
		return rectangle.contains(mouseInput.getX(), mouseInput.getY());
	}
	
	/**
	 * Places the item into an existing stack or in an empty slot.
	 * @param item the item to be added into the inventory
	 * @return true if the item was successfully added, false otherwise
	 */
	public boolean addItem(Item item) {
		boolean foundValidSlot = false;
		boolean addedToStack = false;
		int validRow = -1, validCol = -1;
		
		for (int i = 0; i < inventorySlots.length && !foundValidSlot; i++) {
			for (int j = 0; j < inventorySlots[0].length; j++) {
				// Check for an empty slot in case there is no existing stack to add the item into.
				if (!foundValidSlot && inventorySlots[i][j].item == null) {
					validRow = i;
					validCol = j;
					foundValidSlot = true;
				}
				
				// Check to see if there is already an item with the same Name in the inventory.
				// If there is, and if the stack is not full, place the item into that stack.
				Slot slot = inventorySlots[i][j];
				Item itemInSlot = slot.item;
				if (item.getStackSize() > 1 && itemInSlot != null) {
					Name itemName = item.getObjectId().getName();
					Name slottedName = itemInSlot.getObjectId().getName();
					if (itemName == slottedName && slot.currentStackSize < item.getStackSize()) {
						slot.currentStackSize++;
						foundValidSlot = addedToStack = true;
						break;
					}
				}
			}
		}
		
		// If the item was not added to an existing stack, place it in an empty slot.
		if (!addedToStack && foundValidSlot) {
			inventorySlots[validRow][validCol].item = item;
			inventorySlots[validRow][validCol].currentStackSize = 1;
		}
		
		return foundValidSlot;
	}

}
