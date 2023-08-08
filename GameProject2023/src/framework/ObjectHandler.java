package framework;

import java.awt.Graphics;
import java.util.ArrayList;

public class ObjectHandler {

	public static final int BOTTOM_LAYER = 0;
	public static final int MIDDLE_LAYER = 1;
	public static final int TOP_LAYER = 2;
	public static final int MENU_LAYER = 3;
	
	private ArrayList<GameObject> bottomLayer, middleLayer, topLayer, menuLayer;
	
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
	 * Updates all objects in the game.
	 * This should be called in every frame of the game loop.
	 */
	public void updateObjects() {
		for (int i = 0; i < middleLayer.size(); i++) 
			middleLayer.get(i).update();
		for (int i = 0; i < bottomLayer.size(); i++) 
			bottomLayer.get(i).update();
		for (int i = 0; i < topLayer.size(); i++) 
			topLayer.get(i).update();
		for (int i = 0; i < menuLayer.size(); i++) 
			menuLayer.get(i).update();
	}

	/**
	 * Renders all objects in the game.
	 * This should be called in every frame of the game loop.
	 * @param g graphics object to use for rendering
	 */
	public void renderObjects(Graphics g) {
		for (int i = 0; i < bottomLayer.size(); i++) 
			bottomLayer.get(i).render(g);
		for (int i = 0; i < middleLayer.size(); i++) 
			middleLayer.get(i).render(g);
		for (int i = 0; i < topLayer.size(); i++) 
			topLayer.get(i).render(g);
		for (int i = 0; i < menuLayer.size(); i++) 
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
	
}
