package framework;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;
import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.util.ArrayList;

import framework.ObjectId.Name;
import game_objects.DiagonalStoneTileBlock;
import game_objects.Player;
import game_objects.StoneTileBlock;
import object_templates.PlayerData;
import object_templates.TileOrientation;
import window.HUD;
import window.KeyInput;

public class ObjectHandler {

	public static final int BOTTOM_LAYER = 0;
	public static final int MIDDLE_LAYER = 1;
	public static final int TOP_LAYER = 2;
	public static final int MENU_LAYER = 3;
	
	private ArrayList<GameObject> bottomLayer, middleLayer, topLayer, menuLayer;
	
	private KeyInput keyInput;
	private PlayerData playerData;
	
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
	public void setupGame(KeyInput keyInput) {
		this.keyInput = keyInput;
		playerData = new PlayerData(100, 70);
		
		HUD hud = (HUD) createObjectByName(Name.HUD, 10, 10);
		addObject(hud, ObjectHandler.MENU_LAYER);
		
		FileIO fileIO = new FileIO();
		int[][] objectUIDs = fileIO.loadLevel("levels.txt", 0);
		
		for (int i = 0; i < TILE_ROWS; i++) {
			for (int j = 0; j < TILE_COLUMNS; j++) {
				Name objectName = ObjectId.Name.getByUID(objectUIDs[i][j]);
				if (objectName == null)
					continue;
				
				int x = j * TILE_SIZE;
				int y = i * TILE_SIZE;
				
				GameObject gameObject = createObjectByName(objectName, x, y);
				addObject(gameObject, MIDDLE_LAYER);
			}
		}
	}
	
	/**
	 * Updates all objects in the game.
	 * This should be called in every frame of the game loop.
	 */
	public void updateObjects() {
		for (GameObject go : middleLayer)
			go.tick();
		for (GameObject go : bottomLayer)
			go.tick();
		for (GameObject go : topLayer)
			go.tick();
		for (GameObject go : menuLayer)
			go.tick();
	}

	/**
	 * Renders all objects in the game.
	 * This should be called in every update of the game loop.
	 * @param g graphics object to use for rendering
	 */
	public void renderObjects(Graphics g) {
		for (GameObject go : bottomLayer)
			go.render(g);
		for (GameObject go : middleLayer)
			go.render(g);
		for (GameObject go : topLayer)
			go.render(g);
		for (GameObject go : menuLayer)
			go.render(g);
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
			gameObject = new Player(x, y, playerData, this, keyInput);
			break;
		case HUD:
			gameObject = new HUD(x, y, TILE_SIZE * 3, TILE_SIZE / 2, playerData);
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
		}
		
		if (gameObject == null)
			System.err.println("Could not create game object with the name: " + objectName);
		
		return gameObject;
	}
	
}
