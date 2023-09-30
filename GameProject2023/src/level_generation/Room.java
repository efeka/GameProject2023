package level_generation;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.util.ArrayList;
import java.util.List;

import abstract_templates.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Name;

public class Room {
	
	private enum ExitLocation {
		Up,
		Down,
		Left,
		Right;
	}
	
	private class ExitTuple {
		RoomExit roomExit;
		ExitLocation exitLocation;
		
		public ExitTuple(RoomExit exit, ExitLocation exitLocation) {
			this.roomExit = exit;
			this.exitLocation = exitLocation;
		}
	}
	private ArrayList<ExitTuple> exitTupleList;
	
	private Room up, down, left, right;
	private Room room;
	
	private ObjectHandler objectHandler;
	private ArrayList<GameObject> bottomLayer, middleLayer, topLayer;

	public Room(int[][] bottomLayerUIDs, int[][] middleLayerUIDs, int[][] topLayerUIDs, ObjectHandler objectHandler) {
		this.objectHandler = objectHandler;

		ArrayList<ArrayList<GameObject>> layers = objectHandler.loadLevel(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs);
		bottomLayer = layers.get(0);
		middleLayer = layers.get(1);
		topLayer = layers.get(2);
		
		up = down = left = right = room = null;
		exitTupleList = new ArrayList<>(4);
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

	/*
	public boolean hasUp() {
		return hasUpExit;
	}
	
	public boolean hasDown() {
		return hasDownExit;
	}
	
	public boolean hasLeft() {
		return hasLeftExit;
	}
	
	public boolean hasRight() {
		return hasRightExit;
	}
	*/
}
