package ui;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;
import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import abstract_templates.GameObject;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;
import items.Item;
import items.WeaponItem;
import player_weapons.FistWeapon;
import window.KeyInput;
import window.MouseInput;
import window.MouseInputObserver;

public class Inventory extends GameObject implements MouseInputObserver {

	private class Slot {
		Rectangle slotBounds = new Rectangle();
		Item item = null;
		int currentStackSize = 0;
		boolean equipped = false;
	}

	private ObjectHandler objectHandler;
	private KeyInput keyInput;
	private MouseInput mouseInput;

	private BufferedImage[] textures;

	private int cellSize = (int) (TILE_SIZE * 1.5f);
	private int rows, cols;
	private Slot[][] inventorySlots;
	private Slot[] hotbarSlots;
	private boolean calculatedSlotBounds = false;

	private Slot carriedSlot = null;

	public Inventory(int rows, int cols, ObjectHandler objectHandler, KeyInput keyInput, MouseInput mouseInput) {
		super(-500, 0, 0, 0, new ObjectId(Category.Menu, Name.Missing));
		this.objectHandler = objectHandler;
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		this.rows = rows;
		this.cols = cols;
		mouseInput.registerObserver(this);

		hotbarSlots = new Slot[3];
		for (int i = 0; i < hotbarSlots.length; i++)
			hotbarSlots[i] = new Slot();
		
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
		y = (screenHeight - invHeight) / 2 - cellSize;

		// Calculate the boundaries for each slot
		if (!calculatedSlotBounds) {
			calculatedSlotBounds = true;
			// Inventory slot bounds
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++)
					inventorySlots[i][j].slotBounds = new Rectangle((int) x + (j + 1) * cellSize,
							(int) y + (i + 1) * cellSize, cellSize, cellSize);
			}
		}
	}

	@Override
	public void render(Graphics g) {
		if (keyInput.isInventoryKeyToggled())
			return;

		// Inventory text
		g.drawImage(textures[9], (int) x, (int) y - cellSize, cellSize * 4, cellSize * 2, null);
		// Inventory background
		g.drawImage(textures[4], (int) x + cellSize, (int) (y + cellSize), cols * cellSize, rows * cellSize, null);
		// Inventory corners
		g.drawImage(textures[0], (int) x, (int) y, cellSize, cellSize, null);
		g.drawImage(textures[2], (int) x + (cols + 1) * cellSize, (int) y, cellSize, cellSize, null);
		g.drawImage(textures[6], (int) x, (int) y + (rows + 1) * cellSize, cellSize, cellSize, null);
		g.drawImage(textures[8], (int) x + (cols + 1) * cellSize, (int) y + (rows + 1) * cellSize, cellSize, cellSize, null);
		// Inventory edges
		g.drawImage(textures[1], (int) x + cellSize, (int) y, cols * cellSize, cellSize, null);
		g.drawImage(textures[3], (int) x, (int) y + cellSize, cellSize, rows * cellSize, null);
		g.drawImage(textures[5], (int) x + (cols + 1) * cellSize, (int) y + cellSize, cellSize, rows * cellSize, null);
		g.drawImage(textures[7], (int) x + cellSize, (int) y + (rows + 1) * cellSize, cols * cellSize, cellSize, null);

		// Inventory slots and items
		int iconSize = (int) (7f * cellSize / 10);
		int iconOffset = (cellSize - iconSize) / 2;
		for (int i = 1; i < rows + 1; i++) {
			for (int j = 1; j < cols + 1; j++) {
				int slotTextureIndex = -1;
				Slot slot = inventorySlots[i - 1][j - 1];
				BufferedImage itemTexture = null;

				if (slot.equipped)
					slotTextureIndex = checkMouseHover(slot.slotBounds) ? 13 : 12;
				else
					slotTextureIndex = checkMouseHover(slot.slotBounds) ? 11 : 10;
				if (slot.item != null)
					itemTexture = slot.item.getItemIcon();

				// Draw the slot texture
				BufferedImage slotTexture = textures[slotTextureIndex];
				g.drawImage(slotTexture, (int) x + j * cellSize, (int) y + i * cellSize, cellSize, cellSize, null);

				// Draw the item in the slot
				if (itemTexture != null) {
					g.drawImage(itemTexture, (int) x + iconOffset + j * cellSize, (int) y + iconOffset + i * cellSize,
							iconSize, iconSize, null);

					// Write the current stack size of the item in the slot
					if (slot.currentStackSize > 1) {
						g.setColor(Color.BLACK);
						g.setFont(GameConstants.FontConstants.INVENTORY_FONT);
						String stackText = slot.currentStackSize + "";
						int stackTextX = (int) x + iconOffset + cellSize / 20 + j * cellSize;
						int stackTextY = (int) y + iconOffset + cellSize / 4 + i * cellSize;
						g.drawString(stackText, stackTextX, stackTextY);
					}
				}
			}
		}

		// Hotbar text
		int hotbarOffsetX = ((cols - 3) / 2) * cellSize;
		int hotbarOffsetY = (rows + 1) * cellSize;
		g.drawImage(textures[17], (int) x + hotbarOffsetX + cellSize, (int) y + hotbarOffsetY + 2 * cellSize, 3 * cellSize, 2 * cellSize, null);
		// Hotbar background
		g.drawImage(textures[4], (int) x + hotbarOffsetX + cellSize, (int) y + hotbarOffsetY + cellSize, 3 * cellSize, cellSize, null);
		// Hotbar corners
		g.drawImage(textures[0], (int) x + hotbarOffsetX, (int) y + hotbarOffsetY, cellSize, cellSize, null);
		g.drawImage(textures[2], (int) x + hotbarOffsetX + 4 * cellSize, (int) y + hotbarOffsetY, cellSize, cellSize, null);
		g.drawImage(textures[6], (int) x + hotbarOffsetX, (int) y + hotbarOffsetY + 2 * cellSize, cellSize, cellSize, null);
		g.drawImage(textures[8], (int) x + hotbarOffsetX + 4 * cellSize, (int) y + hotbarOffsetY + 2 * cellSize, cellSize, cellSize, null);
		// Hotbar edges
		g.drawImage(textures[1], (int) x + hotbarOffsetX + cellSize, (int) y + hotbarOffsetY, 3 * cellSize, cellSize, null);
		g.drawImage(textures[7], (int) x + hotbarOffsetX + cellSize, (int) y + hotbarOffsetY + 2 * cellSize, 3 * cellSize, cellSize, null);
		g.drawImage(textures[3], (int) x + hotbarOffsetX, (int) y + hotbarOffsetY + cellSize, cellSize, cellSize, null);
		g.drawImage(textures[5], (int) x + hotbarOffsetX + 4 * cellSize, (int) y + hotbarOffsetY + cellSize, cellSize, cellSize, null);
		// Hotbar slots
		g.drawImage(textures[14], (int) x + hotbarOffsetX + cellSize, (int) y + hotbarOffsetY + cellSize, cellSize, cellSize, null);
		g.drawImage(textures[15], (int) x + hotbarOffsetX + 2 * cellSize, (int) y + hotbarOffsetY + cellSize, cellSize, cellSize, null);
		g.drawImage(textures[16], (int) x + hotbarOffsetX + 3 * cellSize, (int) y + hotbarOffsetY + cellSize, cellSize, cellSize, null);
		for (int i = 0; i < hotbarSlots.length; i++) {
			Item hotbarItem = hotbarSlots[i].item;
			if (hotbarItem != null) {
				int iconX = (int) x + hotbarOffsetX + iconOffset + cellSize + i * cellSize;
				int iconY = (int) y + hotbarOffsetY + iconOffset+ cellSize;
				g.drawImage(hotbarItem.getItemIcon(), iconX, iconY, iconSize, iconSize, null);
			}
		}
		
		// Draw the item that is being carried by the mouse
		if (carriedSlot != null && carriedSlot.item != null) {
			BufferedImage itemImage = carriedSlot.item.getItemIcon();
			int itemImageX = mouseInput.getX() - iconSize / 2;
			int itemImageY = mouseInput.getY() - iconSize / 2;
			g.drawImage(itemImage, itemImageX, itemImageY, iconSize, iconSize, null);

			if (carriedSlot.currentStackSize > 1) {
				g.setColor(Color.BLACK);
				g.setFont(GameConstants.FontConstants.INVENTORY_FONT);
				String stackText = carriedSlot.currentStackSize + "";
				int stackTextX = itemImageX + cellSize / 20;
				int stackTextY = itemImageY + cellSize / 4;
				g.drawString(stackText, stackTextX, stackTextY);
			}
		}
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

		for (int i = 0; i < rows && !addedToStack; i++) {
			for (int j = 0; j < cols; j++) {
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
				if (item.getMaxStackSize() > 1 && itemInSlot != null) {
					boolean namesMatch = item.compareObjectName(itemInSlot);
					if (namesMatch && slot.currentStackSize < item.getMaxStackSize()) {
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

	private boolean checkMouseHover(Rectangle rectangle) {
		return rectangle.contains(mouseInput.getX(), mouseInput.getY());
	}

	@Override
	public void onMousePress(MouseEvent e) {
		// Check if the mouse click was performed on one of the slots.
		Slot clickedSlot = null;
		for (int i = 0; i < rows && clickedSlot == null; i++) {
			for (int j = 0; j < cols; j++) {
				Slot slot = inventorySlots[i][j];
				if (checkMouseHover(slot.slotBounds)) {
					clickedSlot = slot;
					break;
				}
			}
		}

		if (clickedSlot == null)
			return;
		else if (e.getButton() == MouseEvent.BUTTON1)
			handleItemCarrying(clickedSlot);
		else if (e.getButton() == MouseEvent.BUTTON3)
			handleItemEquipping(clickedSlot);
	}

	private void handleItemCarrying(Slot clickedSlot) {
		// If no item is being carried, start carrying the item in the clicked spot.
		if (carriedSlot == null) {
			carriedSlot = new Slot();
			carriedSlot.item = clickedSlot.item;
			carriedSlot.currentStackSize = clickedSlot.currentStackSize;
			carriedSlot.equipped = clickedSlot.equipped;
			clickedSlot.item = null;
			clickedSlot.currentStackSize = 0;
			clickedSlot.equipped = false;
		}
		// If an item is being carried and the clicked slot is empty,
		// place the carried item into that slot.
		else if (clickedSlot.item == null) {
			swapSlotContents(clickedSlot, carriedSlot);
			carriedSlot = null;
		}
		// If an item is being carried and if the clicked slot contains the same item,
		// add it to that slot's stack.
		else if (carriedSlot.item != null && carriedSlot.item.compareObjectName(clickedSlot.item)) {
			int maxStackSize = carriedSlot.item.getMaxStackSize();
			int emptySpaceInStack = maxStackSize - clickedSlot.currentStackSize;
			int neededSpaceInStack = carriedSlot.currentStackSize;
			// If there is enough space left in the clicked slot
			if (neededSpaceInStack <= emptySpaceInStack) {
				carriedSlot = null;
				clickedSlot.currentStackSize += neededSpaceInStack;
			}
			else {
				carriedSlot.currentStackSize -= emptySpaceInStack;
				clickedSlot.currentStackSize = maxStackSize;
			}
		}
		// If an item is being carried and if the clicked slot contains a different item,
		// start carrying the slotted item and place the current one in.
		else {
			swapSlotContents(clickedSlot, carriedSlot);
		}
	}

	private void swapSlotContents(Slot slot1, Slot slot2) {
		Slot tempSlot1 = new Slot();
		tempSlot1.item = slot1.item;
		tempSlot1.currentStackSize = slot1.currentStackSize;
		tempSlot1.equipped = slot1.equipped;
		
		slot1.item = slot2.item;
		slot1.currentStackSize = slot2.currentStackSize;
		slot1.equipped = slot2.equipped;
		slot2.item = tempSlot1.item;
		slot2.currentStackSize = tempSlot1.currentStackSize;
		slot2.equipped = tempSlot1.equipped;
	}
	
	private void handleItemEquipping(Slot clickedSlot) {
		Player player = objectHandler.getPlayer();
		Item itemInSlot = clickedSlot.item;
		if (itemInSlot != null && itemInSlot.isEquippable()) {
			// Equipping a weapon
			if (itemInSlot.getObjectId().getCategory() == Category.WeaponItem) {
				// If the player does not currently have an equipped weapon, equip the item in the clicked slot
				if (hotbarSlots[0].item == null) {
					clickedSlot.equipped = true;
					hotbarSlots[0].item = clickedSlot.item;
					player.setWeapon(((WeaponItem) clickedSlot.item).getWeapon());
				}
				// If the player does have a weapon equipped
				else {
					// If the clicked slot contains the current weapon, unequip it and 
					// give the player the default FistWeapon
					if (clickedSlot.item.equals(hotbarSlots[0].item)) {
						clickedSlot.equipped = false;
						hotbarSlots[0].item = null;
						player.setWeapon(new FistWeapon(objectHandler, keyInput, mouseInput));
					}
					// If the clicked slot contains a different weapon, swap to the new weapon
					else {
						clickedSlot.equipped = true;
						unequipFromSlot(hotbarSlots[0].item);
						hotbarSlots[0].item = clickedSlot.item;
						objectHandler.getPlayer().setWeapon(((WeaponItem) clickedSlot.item).getWeapon());
					}
				}
			}
		}
	}
	
	/**
	 * Set the {@code equipped} parameter of the slot that contains the given item to false.
	 * @param item the item to unequip from the inventory slots.
	 */
	private void unequipFromSlot(Item item) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Item slotItem = inventorySlots[i][j].item;
				if (slotItem != null && slotItem.equals(item)) {
					inventorySlots[i][j].equipped = false;
					return;
				}
			}
		}
	}

	@Override
	public void onMouseClick(MouseEvent e) {}

}
