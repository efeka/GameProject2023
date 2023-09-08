package framework;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;
import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.util.ArrayList;

import abstract_templates.GameObject;
import abstract_templates.TileOrientation;
import framework.ObjectId.Name;
import game_objects.ArcherEnemy;
import game_objects.BasicEnemy;
import game_objects.BullEnemy;
import game_objects.DiagonalStoneTileBlock;
import game_objects.GrassBackgroundTileBlock;
import game_objects.GrassTileBlock;
import game_objects.Player;
import game_objects.RockTileBlock;
import game_objects.StoneTileBlock;
import game_objects.WoodJumpThroughTileBlock;
import items.Coin;
import items.FisticuffsItem;
import items.HammerItem;
import items.SwordItem;
import window.HUD;
import window.KeyInput;
import window.MouseInput;

public class ObjectHandler {

	private Player player = null;
	
	public static final int BOTTOM_LAYER = 0;
	public static final int MIDDLE_LAYER = 1;
	public static final int TOP_LAYER = 2;
	public static final int MENU_LAYER = 3;
	
	private ArrayList<GameObject> bottomLayer, middleLayer, topLayer, menuLayer;
	
	private KeyInput keyInput;
	private MouseInput mouseInput;
	
	/**
	 * This is the class responsible for adding and removing GameObjects from the game.
	 * Objects can be added to different layers to determine their rendering order.
	 */
	public ObjectHandler() {
		bottomLayer = new ArrayList<GameObject>();
		middleLayer = new ArrayList<GameObject>();
		topLayer = new ArrayList<GameObject>();
		menuLayer = new ArrayList<GameObject>();
	}
	
	/**
	 * Load the first level from the levels file and initialize objects.
	 * @param keyInput the key listener object attached to the game window
	 */
	public void setupGame(KeyInput keyInput, MouseInput mouseInput) {
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;

		FileIO fileIO = new FileIO();
		int[][] objectUIDsBottomLayer = fileIO.loadLevel("levels_bg.txt", 0);
		int[][] objectUIDsMiddleLayer = fileIO.loadLevel("levels.txt", 0);
		int[][] objectUIDsTopLayer = fileIO.loadLevel("levels_fg.txt", 0);
		
		for (int i = 0; i < TILE_ROWS; i++) {
			for (int j = 0; j < TILE_COLUMNS; j++) {
				Name objectNameBL = ObjectId.Name.getByUID(objectUIDsBottomLayer[i][j]);
				Name objectNameML = ObjectId.Name.getByUID(objectUIDsMiddleLayer[i][j]);
				Name objectNameTL = ObjectId.Name.getByUID(objectUIDsTopLayer[i][j]);

				int x = j * TILE_SIZE;
				int y = i * TILE_SIZE;
				
				if (objectNameBL != null) {
					GameObject gameObject = createObjectByName(objectNameBL, x, y);
					addObject(gameObject, BOTTOM_LAYER);
				}
				if (objectNameML != null) {
					GameObject gameObject = createObjectByName(objectNameML, x, y);
					addObject(gameObject, MIDDLE_LAYER);
				}
				if (objectNameTL != null) {
					GameObject gameObject = createObjectByName(objectNameTL, x, y);
					addObject(gameObject, TOP_LAYER);
				}
			}
		}
		
		HUD hud = (HUD) createObjectByName(Name.HUD, 10, 10);
		addObject(hud, ObjectHandler.MENU_LAYER);
	}
	
	/**
	 * Updates all objects in the game.
	 * This should be called in every frame of the game loop.
	 */
	public void updateObjects() {
		for (int i = middleLayer.size() - 1; i >= 0; i--) 
			middleLayer.get(i).tick();
		for (int i = bottomLayer.size() - 1; i >= 0; i--) 
			bottomLayer.get(i).tick();
		for (int i = topLayer.size() - 1; i >= 0; i--) 
			topLayer.get(i).tick();
		for (int i = menuLayer.size() - 1; i >= 0; i--) 
			menuLayer.get(i).tick();
	}

	/**
	 * Renders all objects in the game.
	 * This should be called in every update of the game loop.
	 * @param g graphics object to use for rendering
	 */
	public void renderObjects(Graphics g) {
		for (int i = bottomLayer.size() - 1; i >= 0; i--) 
			bottomLayer.get(i).render(g);
		for (int i = middleLayer.size() - 1; i >= 0; i--) 
			middleLayer.get(i).render(g);
		for (int i = topLayer.size() - 1; i >= 0; i--) 
			topLayer.get(i).render(g);
		for (int i = menuLayer.size() - 1; i >= 0; i--) 
			menuLayer.get(i).render(g);
	}

	public void addObject(GameObject object, int layer) {
		switch(layer) {
		case BOTTOM_LAYER:
			bottomLayer.add(object);
			break;
		case MIDDLE_LAYER:
			middleLayer.add(object);
			break;
		case TOP_LAYER:
			topLayer.add(object);
			break;
		case MENU_LAYER:
			menuLayer.add(object);
			break;
		}
	}

	public void removeObject(GameObject object) {
		if (middleLayer.contains(object))
			middleLayer.remove(object);
		else if (bottomLayer.contains(object))
			bottomLayer.remove(object);
		else if (topLayer.contains(object))
			topLayer.remove(object);
		else if (menuLayer.contains(object))
			menuLayer.remove(object);
	}
	
	public void removeObjectFromLayer(GameObject object, int layer) {
		switch (layer) {
		case BOTTOM_LAYER:
			if (bottomLayer.contains(object))
				bottomLayer.remove(object);
			break;
		case MIDDLE_LAYER:
			if (middleLayer.contains(object))
				middleLayer.remove(object);
			break;
		case TOP_LAYER:
			if (topLayer.contains(object))
				topLayer.remove(object);
			break;
		case MENU_LAYER:
			if (menuLayer.contains(object))
				menuLayer.remove(object);
			break;
		}
	}
	
	public ArrayList<GameObject> getLayer(int layer) {
		switch (layer) {
		case BOTTOM_LAYER:
			return bottomLayer;
		case MIDDLE_LAYER:
			return middleLayer;
		case TOP_LAYER:
			return topLayer;
		case MENU_LAYER:
			return menuLayer;
		default:
			return null;
		}
	}
	
	public GameObject createObjectByName(Name objectName, int x, int y) {
		GameObject gameObject = null;
		
		switch (objectName) {
		case Player:
			player = new Player(x, y, this, keyInput, mouseInput);
			gameObject = player;
			break;
		case BasicEnemy:
			gameObject = new BasicEnemy(x, y, this);
			break;
		case ArcherEnemy:
			gameObject = new ArcherEnemy(x, y, this);
			break;
		case BullEnemy:
			gameObject = new BullEnemy(x, y, this);
			break;
		case HUD:
			gameObject = new HUD(x, y, TILE_SIZE * 3, TILE_SIZE / 2, player);
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
		case FisticuffsItem:
			gameObject = new FisticuffsItem(x, y, keyInput, mouseInput, this);
			break;
		case SwordItem:
			gameObject = new SwordItem(x, y, keyInput, mouseInput, this); 
			break;
		case HammerItem:
			gameObject = new HammerItem(x, y, keyInput, mouseInput, this);
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
		}
		
		if (gameObject == null)
			System.err.println("ObjectHandler: Could not create game object with the name: " + objectName);
		
		return gameObject;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
