package ui;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;
import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import abstracts.GameObject;
import abstracts.Item;
import abstracts.WeaponItem;
import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import game_objects.Player;
import player_weapons.FistWeapon;
import window.KeyInput;
import window.MouseInput;
import window.MouseInputObserver;

public class Inventory extends GameObject implements MouseInputObserver {

	private class Slot {
		Rectangle slotBounds = new Rectangle();
		Item item = null;
		int quantity = 0;
		boolean equipped = false;
		
		public Slot() {}
		
		public Slot(Item item, int quantity, boolean equipped) {
			this.item = item;
			this.quantity = quantity;
			this.equipped = equipped;
		}
	}

	private class Hotbar {
		Slot weaponSlot;
		Slot[] itemSlots;
		int slotCooldownMillis = 1000;
		long[] slotCooldownTimers;
		
		public Hotbar() {
			weaponSlot = new Slot();
			
			int hotbarSlots = 2;
			itemSlots = new Slot[hotbarSlots];
			slotCooldownTimers = new long[hotbarSlots];
			
			for (int i = 0; i < hotbarSlots; i++)
				itemSlots[i] = new Slot();
		}

		public boolean equipItem(Item item, int quantity) {
			boolean equipSuccessful = false;
			if (item.compareCategory(Category.WeaponItem)) {
				weaponSlot.item = item;
				equipSuccessful = true;
			}
			else {
				for (int i = 0; i < itemSlots.length; i++) {
					if (itemSlots[i].item == null) {
						itemSlots[i].item = item;
						itemSlots[i].quantity = quantity;
						equipSuccessful = true;
						break;
					}
				}
			}
			return equipSuccessful;
		}
		
		public void unequipItem(Item item) {
			if (item.compareCategory(Category.WeaponItem)) {
				weaponSlot.item = null;
				weaponSlot.equipped = false;
			}
			else {
				for (int i = 0; i < itemSlots.length; i++) {
					if (itemSlots[i].item != null && itemSlots[i].item.equals(item)) {
						itemSlots[i].item = null;
						break;
					}
				}
			}
		}
		
		public void setItemQuantity(Item item, int quantity) {
			for (int i = 0; i < itemSlots.length; i++) {
				Item itemInSlot = itemSlots[i].item;
				if (itemInSlot != null && itemInSlot.equals(item)) {
					itemSlots[i].quantity = quantity;
					break;
				}
			}
		}
		
		public void startSlotCooldownTimer(int index) {
			slotCooldownTimers[index] = System.currentTimeMillis();
		}
		
		public boolean isSlotCooldownReady(int index) {
			return System.currentTimeMillis() - slotCooldownTimers[index] > slotCooldownMillis;
		}
	}
	private Hotbar hotbar;

	private ObjectHandler objectHandler;
	private KeyInput keyInput;
	private MouseInput mouseInput;

	private BufferedImage[] textures;

	private int cellSize = (int) (TILE_SIZE * 1.5f);
	private int rows, cols;
	private Slot[][] inventorySlots;
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
		hotbar = new Hotbar();

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
					if (slot.quantity > 1) {
						g.setColor(Color.BLACK);
						g.setFont(GameConstants.FontConstants.INVENTORY_FONT);
						int stackTextX = (int) x + iconOffset + cellSize / 20 + j * cellSize;
						int stackTextY = (int) y + iconOffset + cellSize / 4 + i * cellSize;
						g.drawString(slot.quantity + "", stackTextX, stackTextY);
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
		BufferedImage slotTexture = hotbar.weaponSlot.item == null ? textures[14] : textures[11];
		g.drawImage(slotTexture, (int) x + hotbarOffsetX + cellSize, (int) y + hotbarOffsetY + cellSize, cellSize, cellSize, null);
		slotTexture = hotbar.itemSlots[0].item == null ? textures[15] : textures[11];
		g.drawImage(slotTexture, (int) x + hotbarOffsetX + 2 * cellSize, (int) y + hotbarOffsetY + cellSize, cellSize, cellSize, null);
		slotTexture = hotbar.itemSlots[1].item == null ? textures[16] : textures[11];
		g.drawImage(slotTexture, (int) x + hotbarOffsetX + 3 * cellSize, (int) y + hotbarOffsetY + cellSize, cellSize, cellSize, null);
		
		// Item icons in hotbar slots
		int iconX = (int) x + hotbarOffsetX + iconOffset + cellSize;
		int iconY = (int) y + hotbarOffsetY + iconOffset + cellSize;
		BufferedImage itemIcon = hotbar.weaponSlot.item == null ? null : hotbar.weaponSlot.item.getItemIcon();
		g.drawImage(itemIcon, iconX, iconY, iconSize, iconSize, null);
		
		for (int i = 0; i < hotbar.itemSlots.length; i++) {
			Item hotbarItem = hotbar.itemSlots[i].item;
			iconX += cellSize;
			if (hotbarItem != null) {
				g.drawImage(hotbarItem.getItemIcon(), iconX, iconY, iconSize, iconSize, null);
				
				g.setColor(Color.BLACK);
				g.setFont(GameConstants.FontConstants.INVENTORY_FONT);
				int stackTextX = iconX + cellSize / 20;
				int stackTextY = iconY + cellSize / 4;
				g.drawString(hotbar.itemSlots[i].quantity + "", stackTextX, stackTextY);
			}
		}

		// Draw the item that is being carried by the mouse
		if (carriedSlot != null && carriedSlot.item != null) {
			BufferedImage itemImage = carriedSlot.item.getItemIcon();
			int itemImageX = mouseInput.getX() - iconSize / 2;
			int itemImageY = mouseInput.getY() - iconSize / 2;
			g.drawImage(itemImage, itemImageX, itemImageY, iconSize, iconSize, null);

			if (carriedSlot.quantity > 1) {
				g.setColor(Color.BLACK);
				g.setFont(GameConstants.FontConstants.INVENTORY_FONT);
				String stackText = carriedSlot.quantity + "";
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
					if (namesMatch && slot.quantity < item.getMaxStackSize()) {
						slot.quantity++;
						foundValidSlot = addedToStack = true;
						
						// If this item is currently equipped, increase the quantity of the item
						// in the hotbar's slot as well
						if (slot.equipped)
							hotbar.setItemQuantity(itemInSlot, slot.quantity);
						break;
					}
				}
			}
		}

		// If the item was not added to an existing stack, place it in an empty slot.
		if (!addedToStack && foundValidSlot) {
			inventorySlots[validRow][validCol].item = item;
			inventorySlots[validRow][validCol].quantity = 1;
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
			handleEquippingAction(clickedSlot);
	}

	private void handleItemCarrying(Slot clickedSlot) {
		// If no item is being carried, start carrying the item in the clicked spot.
		if (carriedSlot == null) {
			carriedSlot = new Slot(clickedSlot.item, clickedSlot.quantity, clickedSlot.equipped);
			clickedSlot.item = null;
			clickedSlot.quantity = 0;
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
			int emptySpaceInStack = maxStackSize - clickedSlot.quantity;
			int neededSpaceInStack = carriedSlot.quantity;
			// If there is enough space left in the clicked slot
			if (neededSpaceInStack <= emptySpaceInStack) {
				carriedSlot = null;
				clickedSlot.quantity += neededSpaceInStack;
			}
			else {
				carriedSlot.quantity -= emptySpaceInStack;
				clickedSlot.quantity = maxStackSize;
			}
			
			// Update item quantity in the hotbar
			if (carriedSlot.equipped)
				hotbar.setItemQuantity(carriedSlot.item, carriedSlot.quantity);
			if (clickedSlot.equipped)
				hotbar.setItemQuantity(clickedSlot.item, clickedSlot.quantity);
		}
		// If an item is being carried and if the clicked slot contains a different item,
		// start carrying the slotted item and place the current one in.
		else {
			swapSlotContents(clickedSlot, carriedSlot);
		}
	}

	private void swapSlotContents(Slot slot1, Slot slot2) {
		Slot tempSlot1 = new Slot(slot1.item, slot1.quantity, slot1.equipped);
		slot1.item = slot2.item;
		slot1.quantity = slot2.quantity;
		slot1.equipped = slot2.equipped;
		slot2.item = tempSlot1.item;
		slot2.quantity = tempSlot1.quantity;
		slot2.equipped = tempSlot1.equipped;
	}

	private void handleEquippingAction(Slot clickedSlot) {
		Item itemInSlot = clickedSlot.item;
		if (itemInSlot == null || !itemInSlot.isEquippable())
			return;

		if (itemInSlot.compareCategory(Category.WeaponItem))
			handleWeaponEquipping(clickedSlot);
		else if (itemInSlot.compareCategory(Category.Item))
			handleItemEquipping(clickedSlot);
	}

	private void handleWeaponEquipping(Slot inventorySlot) {
		Player player = objectHandler.getPlayer();
		WeaponItem itemInSlot = (WeaponItem) inventorySlot.item;
		
		// If the clicked slot contains the currently equipped weapon
		if (itemInSlot.equals(hotbar.weaponSlot.item)) {
			inventorySlot.equipped = false;
			hotbar.unequipItem(itemInSlot);
			player.setWeapon(new FistWeapon(objectHandler, keyInput, mouseInput));
		}
		// If the clicked slot contains a new weapon
		else {
			if (hotbar.weaponSlot.item != null)
				getInventorySlotFromItem(hotbar.weaponSlot.item).equipped = false;
			inventorySlot.equipped = true;
			hotbar.equipItem((WeaponItem) itemInSlot, 1);
			player.setWeapon(((WeaponItem) itemInSlot).getWeapon());
		}
	}
	
	private void handleItemEquipping(Slot inventorySlot) {
		Item itemInSlot = inventorySlot.item;
		// If the clicked slot contains an already equipped item
		if (inventorySlot.equipped) {
			inventorySlot.equipped = false;
			hotbar.unequipItem(itemInSlot);
		}
		// If the clicked slot contains a new item
		else
			inventorySlot.equipped = hotbar.equipItem(itemInSlot, inventorySlot.quantity);
	}

	/**
	 * Use the item in the given hotbar item index.
	 * @param hotbarItemIndex the index of the item in the hotbar
	 */
	public void useItem(int hotbarItemIndex) {
		if (hotbarItemIndex < 0 || hotbarItemIndex >= hotbar.itemSlots.length)
			throw new IndexOutOfBoundsException("Index " + hotbarItemIndex + " is out of bounds for the hotbar items array");
		
		Slot hotbarSlot = hotbar.itemSlots[hotbarItemIndex];
		if (hotbarSlot == null)
			return;
		if (!hotbar.isSlotCooldownReady(hotbarItemIndex))
			return;
		Item hotbarItem = hotbarSlot.item;
		if (hotbarItem == null)
			return;
		
		// Use the item
		Slot inventorySlot = getInventorySlotFromItem(hotbarItem);
		inventorySlot.quantity--;
		hotbarItem.useItem();
		hotbarSlot.quantity--;
		hotbar.startSlotCooldownTimer(hotbarItemIndex);
		
		// If there is no more of this item left in the slot
		if (hotbarSlot.quantity == 0) {
			// Reset the slots
			hotbarSlot.equipped = false;
			hotbarSlot.item = null;
			inventorySlot.equipped = false;
			inventorySlot.item = null;
		}
	}
	
	/**
	 * Search the inventory for the given item, if it exists, return the inventory slot that contains it.
	 * @param item the item to search the inventory slots with
	 * @return the slot that contains the item if it exists, null otherwise
	 */
	private Slot getInventorySlotFromItem(Item item) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Item slotItem = inventorySlots[i][j].item;
				if (slotItem != null && slotItem.equals(item)) {
					return inventorySlots[i][j];
				}
			}
		}
		return null;
	}
	
	@Override
	public void onMouseClick(MouseEvent e) {}

}
