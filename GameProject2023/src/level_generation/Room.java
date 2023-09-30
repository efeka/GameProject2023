package level_generation;

import java.util.ArrayList;

import abstract_templates.GameObject;
import framework.ObjectHandler;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public class Room {

	private int[][] bottomLayerUIDs, middleLayerUIDs, topLayerUIDs;
	
	private Room upNeighbor, downNeighbor, leftNeighbor, rightNeighbor;
	private boolean hasUpExit, hasDownExit, hasLeftExit, hasRightExit;
	
	private ObjectHandler objectHandler;
	private ArrayList<GameObject> bottomLayer, middleLayer, topLayer;

	public Room(int[][] bottomLayerUIDs, int[][] middleLayerUIDs, int[][] topLayerUIDs, ObjectHandler objectHandler) {
		this.bottomLayerUIDs = bottomLayerUIDs;
		this.middleLayerUIDs = middleLayerUIDs;
		this.topLayerUIDs = topLayerUIDs;
		this.objectHandler = objectHandler;

		ArrayList<ArrayList<GameObject>> layers = objectHandler.loadLevel(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs);
		bottomLayer = layers.get(0);
		middleLayer = layers.get(1);
		topLayer = layers.get(2);
		
		upNeighbor = downNeighbor = leftNeighbor = rightNeighbor = null;
		findExits(middleLayer);
	}
	
	private void findExits(ArrayList<GameObject> middleLayer) {
		for (int i = middleLayer.size() - 1; i >= 0; i--) {
			GameObject gameObject = middleLayer.get(i);
			Name objectName = gameObject.getObjectId().getName();
			if (gameObject.getObjectId().getCategory() == Category.Exit) {
				switch(objectName) {
				case RoomExitUp:
					hasUpExit = true;
					break;
				case RoomExitDown:
					hasDownExit = true;
					break;
				case RoomExitLeft:
					hasLeftExit = true;
					break;
				case RoomExitRight:
					hasRightExit = true;
					break;
				default:
					break;
				}
			}
		}
	}
	
	public boolean hasExitLocation(ExitLocation exitLocation) {
		boolean hasExit = false;
		switch (exitLocation) {
		case Up:
			hasExit = hasUpExit;
			break;
		case Down:
			hasExit = hasDownExit;
			break;
		case Left:
			hasExit = hasLeftExit;
			break;
		case Right:
			hasExit = hasRightExit;
			break;
		}
		return hasExit;
	}
	
	public Room createCopy() {
		return new Room(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs, objectHandler);
	}
	
	public ArrayList<ExitLocation> getUnusedExitLocations() {
		ArrayList<ExitLocation> list = new ArrayList<>();
		if (hasUpExit && upNeighbor == null)
			list.add(ExitLocation.Up);
		if (hasDownExit && downNeighbor == null)
			list.add(ExitLocation.Down);
		if (hasLeftExit && leftNeighbor == null)
			list.add(ExitLocation.Left);
		if (hasRightExit && rightNeighbor == null)
			list.add(ExitLocation.Right);
		return list;
	}
	
	public void setNeighbor(ExitLocation exitLocation, Room neighbor) {
		switch (exitLocation) {
		case Up:
			upNeighbor = neighbor;
			break;
		case Down:
			downNeighbor = neighbor;
			break;
		case Left:
			leftNeighbor = neighbor;
			break;
		case Right:
			rightNeighbor = neighbor;
			break;
		}
	}
	
	public ArrayList<GameObject> getBottomLayer() {
		return bottomLayer;
	}

	public ArrayList<GameObject> getMiddleLayer() {
		return middleLayer;
	}

	public ArrayList<GameObject> getTopLayer() {
		return topLayer;
	}

	public Room getUpNeighbor() {
		return upNeighbor;
	}

	public void setUpNeighbor(Room upNeighbor) {
		this.upNeighbor = upNeighbor;
	}

	public Room getDownNeighbor() {
		return downNeighbor;
	}

	public void setDownNeighbor(Room downNeighbor) {
		this.downNeighbor = downNeighbor;
	}

	public Room getLeftNeighbor() {
		return leftNeighbor;
	}

	public void setLeftNeighbor(Room leftNeighbor) {
		this.leftNeighbor = leftNeighbor;
	}

	public Room getRightNeighbor() {
		return rightNeighbor;
	}

	public void setRightNeighbor(Room rightNeighbor) {
		this.rightNeighbor = rightNeighbor;
	}
	
}
