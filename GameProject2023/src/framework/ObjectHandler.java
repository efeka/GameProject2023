package framework;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;
import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.util.ArrayList;

import abstracts.Creature;
import abstracts.GameObject;
import abstracts.TileOrientation;
import floor_generation.Floor;
import floor_generation.PlayerExitDestination;
import floor_generation.Room;
import floor_generation.RoomDirection;
import floor_generation.RoomExit;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import game_objects.ArcherEnemy;
import game_objects.BullEnemy;
import game_objects.DiagonalStoneTileBlock;
import game_objects.GrassBackgroundTileBlock;
import game_objects.GrassTileBlock;
import game_objects.Player;
import game_objects.RockTileBlock;
import game_objects.StoneTileBlock;
import game_objects.ToadEnemy;
import game_objects.WoodJumpThroughTileBlock;
import items.BigHealthPotionItem;
import items.Coin;
import items.KatanaWeaponItem;
import items.SmallHealthPotionItem;
import items.SwordWeaponItem;
import ui.DebugConsole;
import ui.HUD;
import ui.Inventory;
import ui.Minimap;
import window.KeyInput;
import window.MouseInput;

public class ObjectHandler {

	private Floor floor;
	private Player player = null;
	private Inventory inventory = null;
	
	public static final int BOTTOM_LAYER = 0;
	public static final int MIDDLE_LAYER = 1;
	public static final int TOP_LAYER = 2;
	public static final int MENU_LAYER = 3;
	
	private ArrayList<GameObject> menuLayer;
	private ArrayList<Creature> summonsList;
	
	private KeyInput keyInput;
	private MouseInput mouseInput;
	
	/**
	 * This is the class responsible for adding, removing, updating and rendering 
	 * all GameObjects in the game.
	 * Objects can be added to different layers to set their rendering order.
	 */
	public ObjectHandler(KeyInput keyInput, MouseInput mouseInput) {
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		
		menuLayer = new ArrayList<GameObject>();
		summonsList = new ArrayList<Creature>();
	}
	
	/**
	 * Creates the necessary UI elements and generates the first floor of the run.
	 * @param roomsPerFloor the number of rooms to generate per each floor
	 */
	public void setupGame(int roomsPerFloor) {
		floor = new Floor(this);
		floor.generateRandomFloor(roomsPerFloor);
		
		addObject(new DebugConsole(180, 10, keyInput, this), MENU_LAYER);
		
		inventory = new Inventory(3, 3, this, keyInput, mouseInput);
		addObject(inventory, MENU_LAYER);
		
		HUD hud = new HUD(0, 0, TILE_SIZE * 3, TILE_SIZE / 2, player);
		addObject(hud, MENU_LAYER);
		
		Minimap minimap = new Minimap(TILE_SIZE * 4, TILE_SIZE * 4, floor);
		addObject(minimap, MENU_LAYER);
	}
	
	/**
	 * Creates the GameObjects with the given uids and returns them as a list.
	 * @param bottomLayerUIDs the uids of the objects on the bottom layer
	 * @param middleLayerUIDs the uids of the objects on the middle layer
	 * @param topLayerUIDs the uids of the objects on the top layer
	 * @return A list containing the game objects.
	 * 		   Index 0 contains the bottom layer.
	 * 		   Index 1 contains the middle layer.
	 *		   Index 2 contain the top layer. 
	 */
	public ArrayList<ArrayList<GameObject>> loadLevel(int[][] bottomLayerUIDs, int[][] middleLayerUIDs, int[][] topLayerUIDs) {
		ArrayList<ArrayList<GameObject>> layers = new ArrayList<>();
		// Bottom layer on index 0
		layers.add(new ArrayList<GameObject>());
		// Middle layer on index 1
		layers.add(new ArrayList<GameObject>());
		// Top layer on index 2
		layers.add(new ArrayList<GameObject>());
		
		for (int i = 0; i < TILE_ROWS; i++) {
			for (int j = 0; j < TILE_COLUMNS; j++) {
				Name objectNameBL = ObjectId.Name.getByUID(bottomLayerUIDs[i][j]);
				Name objectNameML = ObjectId.Name.getByUID(middleLayerUIDs[i][j]);
				Name objectNameTL = ObjectId.Name.getByUID(topLayerUIDs[i][j]);

				int x = j * TILE_SIZE;
				int y = i * TILE_SIZE;
				
				if (objectNameBL != null) {
					GameObject gameObject = createObjectByName(objectNameBL, x, y);
					layers.get(0).add(gameObject);
				}
				if (objectNameML != null) {
					GameObject gameObject = createObjectByName(objectNameML, x, y);
					layers.get(1).add(gameObject);
				}
				if (objectNameTL != null) {
					GameObject gameObject = createObjectByName(objectNameTL, x, y);
					layers.get(2).add(gameObject);
				}
			}
		}
		
		return layers;
	}
	
	/**
	 * Update the GameObjects in the current room.
	 * This method should be called in every update of the game loop.
	 * Culling is performed by only updating the room that the player is in.
	 */
	public void updateObjects() {
		if (floor == null)
			return;
		
		Room currentRoom = floor.getCurrentRoom();
		for (int i = currentRoom.getBottomLayer().size() - 1; i >= 0; i--) 
			currentRoom.getBottomLayer().get(i).tick();
		for (int i = currentRoom.getMiddleLayer().size() - 1; i >= 0; i--) 
			currentRoom.getMiddleLayer().get(i).tick();
		for (int i = currentRoom.getTopLayer().size() - 1; i >= 0; i--) 
			currentRoom.getTopLayer().get(i).tick();
		for (int i = menuLayer.size() - 1; i >= 0; i--) 
			menuLayer.get(i).tick();
	}

	/**
	 * Render the GameObjects in the current room.
	 * This method should be called in every frame of the game loop.
	 * Culling is performed by only rendering the room that the player is in.
	 * @param g the graphics object to use for rendering
	 */
	public void renderObjects(Graphics g) {
		if (floor == null)
			return;
		
		Room currentRoom = floor.getCurrentRoom();
		for (int i = currentRoom.getBottomLayer().size() - 1; i >= 0; i--) 
			currentRoom.getBottomLayer().get(i).render(g);
		for (int i = currentRoom.getMiddleLayer().size() - 1; i >= 0; i--) 
			currentRoom.getMiddleLayer().get(i).render(g);
		for (int i = currentRoom.getTopLayer().size() - 1; i >= 0; i--) 
			currentRoom.getTopLayer().get(i).render(g);
		for (int i = menuLayer.size() - 1; i >= 0; i--) 
			menuLayer.get(i).render(g);
	}

	/**
	 * Add an object into the current room
	 * @param object the object to be added
	 * @param layer the layer to add the object into
	 */
	public void addObject(GameObject object, int layer) {
		if (floor == null)
			return;
		
		Room currentRoom = floor.getCurrentRoom();
		switch(layer) {
		case BOTTOM_LAYER:
			currentRoom.getBottomLayer().add(object);
			break;
		case MIDDLE_LAYER:
			currentRoom.getMiddleLayer().add(object);
			break;
		case TOP_LAYER:
			currentRoom.getTopLayer().add(object);
			break;
		case MENU_LAYER:
			menuLayer.add(object);
			break;
		}
		
		if (object != null && object.getObjectId() != null && object.getObjectId().getCategory() == Category.FriendlySummon)
			summonsList.add((Creature) object);
	}

	/**
	 * Remove a game object from the current room
	 * @param object the object to be removed
	 */
	public void removeObject(GameObject object) {
		if (floor == null)
			return;
		
		Room currentRoom = floor.getCurrentRoom();
		if (currentRoom.getMiddleLayer().contains(object))
			currentRoom.getMiddleLayer().remove(object);
		else if (currentRoom.getBottomLayer().contains(object))
			currentRoom.getBottomLayer().remove(object);
		else if (currentRoom.getTopLayer().contains(object))
			currentRoom.getTopLayer().remove(object);
		else if (menuLayer.contains(object))
			menuLayer.remove(object);
		
		if (object != null && object.getObjectId() != null && object.getObjectId().getCategory() == Category.FriendlySummon)
			summonsList.remove((Creature) object);
	}
	
	/**
	 * Remove an object from the current floor, from the given layer.
	 * @param object the object to be removed
	 * @param layer the layer to remove the object from
	 */
	public void removeObjectFromLayer(GameObject object, int layer) {
		if (floor == null)
			return;
		
		Room currentRoom = floor.getCurrentRoom();
		switch (layer) {
		case BOTTOM_LAYER:
			if (currentRoom.getBottomLayer().contains(object))
				currentRoom.getBottomLayer().remove(object);
			break;
		case MIDDLE_LAYER:
			if (currentRoom.getMiddleLayer().contains(object))
				currentRoom.getMiddleLayer().remove(object);
			break;
		case TOP_LAYER:
			if (currentRoom.getTopLayer().contains(object))
				currentRoom.getTopLayer().remove(object);
			break;
		case MENU_LAYER:
			if (menuLayer.contains(object))
				menuLayer.remove(object);
			break;
		}
		
		if (object != null && object.getObjectId() != null && object.getObjectId().getCategory() == Category.FriendlySummon)
			summonsList.add((Creature) object);
	}
	
	/**
	 * Retrieve the selected layer of objects from the current room
	 * @param layer the layer to retrieve
	 * @return list objects in the selected layer
	 */
	public ArrayList<GameObject> getLayer(int layer) {
		if (floor == null)
			return null;
		
		Room currentRoom = floor.getCurrentRoom();
		switch (layer) {
		case BOTTOM_LAYER:
			return currentRoom.getBottomLayer();
		case MIDDLE_LAYER:
			return currentRoom.getMiddleLayer();
		case TOP_LAYER:
			return currentRoom.getTopLayer();
		case MENU_LAYER:
			return menuLayer;
		default:
			return null;
		}
	}
	
	/**
	 * Loads the the room that is neighboring the currentRoom, at the given location.
	 * Slightly moves the player to avoid getting stuck inside the two exits.
	 * @param roomExit the roomExit that the player just went through
	 * @param exitLocationToNeighbor the location of the exit leading to the neighbor
	 */
	public void loadNeighboringRoom(RoomExit roomExit, RoomDirection exitLocationToNeighbor) {
		floor.getCurrentRoom().getMiddleLayer().remove(player);
		floor.loadNextRoom(exitLocationToNeighbor);
		floor.getCurrentRoom().getMiddleLayer().add(player);
		// Move the player to the corresponding spawn location of the next rooms exit
		RoomDirection oppositeExitLocation = RoomDirection.getOppositeDirection(exitLocationToNeighbor);
		PlayerExitDestination playerExitDestination = floor.getCurrentRoom().getPlayerExitDestination(oppositeExitLocation);
		player.setX(playerExitDestination.getX());
		player.setY(playerExitDestination.getY());
	}
	
	public ArrayList<Creature> getSummonsList() {
		return summonsList;
	}
	
	public GameObject createObjectByName(Name objectName, int x, int y) {
		GameObject gameObject = null;
		
		switch (objectName) {
		case Player:
			player = new Player(x, y, inventory, this, keyInput, mouseInput);
			gameObject = player;
			summonsList.add(player);
			break;
		case BasicEnemy:
			gameObject = new ToadEnemy(x, y, this);
			break;
		case ArcherEnemy:
			gameObject = new ArcherEnemy(x, y, this);
			break;
		case BullEnemy:
			gameObject = new BullEnemy(x, y, this);
			break;
		case DiagonalStoneTileBlock_OuterBottomLeft:
			gameObject = new DiagonalStoneTileBlock(x, y, objectName, TileOrientation.OuterBottomLeft);
			break;
		case DiagonalStoneTileBlock_OuterBottomRight:
			gameObject = new DiagonalStoneTileBlock(x, y, objectName, TileOrientation.OuterBottomRight);
			break;
		case DiagonalStoneTileBlock_OuterTopLeft:
			gameObject = new DiagonalStoneTileBlock(x, y, objectName, TileOrientation.OuterTopLeft);
			break;
		case DiagonalStoneTileBlock_OuterTopRight:
			gameObject = new DiagonalStoneTileBlock(x, y, objectName, TileOrientation.OuterTopRight);
			break;
		case StoneTileBlock_Center:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.Center);
			break;
		case StoneTileBlock_InnerBottomLeft:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.InnerBottomLeft);
			break;
		case StoneTileBlock_InnerBottomRight:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.InnerBottomRight);
			break;
		case StoneTileBlock_InnerTopLeft:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.InnerTopLeft);
			break;
		case StoneTileBlock_InnerTopRight:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.InnerTopRight);
			break;
		case StoneTileBlock_OuterBottom:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.OuterBottom);
			break;
		case StoneTileBlock_OuterBottomLeft:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.OuterBottomLeft);
			break;
		case StoneTileBlock_OuterBottomRight:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.OuterBottomRight);
			break;
		case StoneTileBlock_OuterLeft:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.OuterLeft);
			break;
		case StoneTileBlock_OuterRight:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.OuterRight);
			break;
		case StoneTileBlock_OuterTop:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.OuterTop);
			break;
		case StoneTileBlock_OuterTopLeft:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.OuterTopLeft);
			break;
		case StoneTileBlock_OuterTopRight:
			gameObject = new StoneTileBlock(x, y, objectName, TileOrientation.OuterTopRight);
			break;
		case GrassTileBlock_Center:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.Center);
			break;
		case GrassTileBlock_InnerBottomLeft:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.InnerBottomLeft);
			break;
		case GrassTileBlock_InnerBottomRight:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.InnerBottomRight);
			break;
		case GrassTileBlock_InnerTopLeft:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.InnerTopLeft);
			break;
		case GrassTileBlock_InnerTopRight:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.InnerTopRight);
			break;
		case GrassTileBlock_OuterBottom:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.OuterBottom);
			break;
		case GrassTileBlock_OuterBottomLeft:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.OuterBottomLeft);
			break;
		case GrassTileBlock_OuterBottomRight:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.OuterBottomRight);
			break;
		case GrassTileBlock_OuterLeft:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.OuterLeft);
			break;
		case GrassTileBlock_OuterRight:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.OuterRight);
			break;
		case GrassTileBlock_OuterTop:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.OuterTop);
			break;
		case GrassTileBlock_OuterTopLeft:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.OuterTopLeft);
			break;
		case GrassTileBlock_OuterTopRight:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.OuterTopRight);
			break;
		case GrassTileBlock_StandaloneLeft:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.StandaloneLeft);
			break;
		case GrassTileBlock_StandaloneCenter:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.StandaloneCenter);
			break;
		case GrassTileBlock_StandaloneRight:
			gameObject = new GrassTileBlock(x, y, objectName, TileOrientation.StandaloneRight);
			break;
		case RockTileBlock_Center:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.Center);
			break;
		case RockTileBlock_InnerBottomLeft:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.InnerBottomLeft);
			break;
		case RockTileBlock_InnerBottomRight:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.InnerBottomRight);
			break;
		case RockTileBlock_InnerTopLeft:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.InnerTopLeft);
			break;
		case RockTileBlock_InnerTopRight:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.InnerTopRight);
			break;
		case RockTileBlock_OuterBottom:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.OuterBottom);
			break;
		case RockTileBlock_OuterBottomLeft:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.OuterBottomLeft);
			break;
		case RockTileBlock_OuterBottomRight:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.OuterBottomRight);
			break;
		case RockTileBlock_OuterLeft:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.OuterLeft);
			break;
		case RockTileBlock_OuterRight:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.OuterRight);
			break;
		case RockTileBlock_OuterTop:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.OuterTop);
			break;
		case RockTileBlock_OuterTopLeft:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.OuterTopLeft);
			break;
		case RockTileBlock_OuterTopRight:
			gameObject = new RockTileBlock(x, y, objectName, TileOrientation.OuterTopRight);
			break;
		case WoodJumpThroughTileBlock_Center:
			gameObject = new WoodJumpThroughTileBlock(x, y, objectName, TileOrientation.Center);
			break;
		case WoodJumpThroughTileBlock_OuterLeft:
			gameObject = new WoodJumpThroughTileBlock(x, y, objectName, TileOrientation.OuterLeft);
			break;
		case WoodJumpThroughTileBlock_OuterRight:
			gameObject = new WoodJumpThroughTileBlock(x, y, objectName, TileOrientation.OuterRight);
			break;
		case Coin:
			gameObject = new Coin(x, y, this);
			break;
		case Missing:
			break;
		case GrassBackgroundTileBlock_Center:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.Center);
			break;
		case GrassBackgroundTileBlock_InnerBottomLeft:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.InnerBottomLeft);
			break;
		case GrassBackgroundTileBlock_InnerBottomRight:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.InnerBottomRight);
			break;
		case GrassBackgroundTileBlock_InnerTopLeft:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.InnerTopLeft);
			break;
		case GrassBackgroundTileBlock_InnerTopRight:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.InnerTopRight);
			break;
		case GrassBackgroundTileBlock_OuterBottom:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.OuterBottom);
			break;
		case GrassBackgroundTileBlock_OuterBottomLeft:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.OuterBottomLeft);
			break;
		case GrassBackgroundTileBlock_OuterBottomRight:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.OuterBottomRight);
			break;
		case GrassBackgroundTileBlock_OuterLeft:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.OuterLeft);
			break;
		case GrassBackgroundTileBlock_OuterRight:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.OuterRight);
			break;
		case GrassBackgroundTileBlock_OuterTop:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.OuterTop);
			break;
		case GrassBackgroundTileBlock_OuterTopLeft:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.OuterTopLeft);
			break;
		case GrassBackgroundTileBlock_OuterTopRight:
			gameObject = new GrassBackgroundTileBlock(x, y, objectName, TileOrientation.OuterTopRight);
			break;
		case SwordWeaponItem:
			gameObject = new SwordWeaponItem(x, y, this, keyInput, mouseInput);
			break;
		case KatanaWeaponItem:
			gameObject = new KatanaWeaponItem(x, y, this, keyInput, mouseInput);
			break;
		case SmallHealthPotionItem:
			gameObject = new SmallHealthPotionItem(x, y, this);
			break;
		case BigHealthPotionItem:
			gameObject = new BigHealthPotionItem(x, y, this);
			break;
		case RoomExitUp:
		case RoomExitDown:
		case RoomExitLeft:
		case RoomExitRight:
			gameObject = new RoomExit(x, y, this, objectName);
			break;
		case PlayerExitDestinationUp:
		case PlayerExitDestinationDown:
		case PlayerExitDestinationLeft:
		case PlayerExitDestinationRight:
			gameObject = new PlayerExitDestination(x, y, objectName);
			break;
		}
		
		if (gameObject == null && objectName != Name.Missing)
			System.err.println("ObjectHandler: Could not create game object with the name: " + objectName);
		
		return gameObject;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
}
