package floor_generation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import abstracts.GameObject;
import framework.ObjectHandler;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public class Room {

	private ObjectHandler objectHandler;
	private int[][] bottomLayerUIDs, middleLayerUIDs, topLayerUIDs;
	private ArrayList<GameObject> bottomLayer, middleLayer, topLayer;

	private Room[] neighbors;
	private boolean[] hasRoomExit;
	private PlayerExitDestination[] playerExitDestinations;

	/**
	 * The Room class represent the vertices of the Floor graph.
	 * It stores the game objects of a singular level and allows easy access to them
	 * for updating and rendering.
	 * The objects of this class also maintain references to adjacent rooms which have linked exits.
	 * @param bottomLayerUIDs the uid's of the objects on the bottom layer of this level
	 * @param middleLayerUIDs the uid's of the objects on the middle layer of this level
	 * @param topLayerUIDs the uid's of the objects on the top layer of this level
	 * @param objectHandler the reference to the ObjectHandler
	 */
	public Room(int[][] bottomLayerUIDs, int[][] middleLayerUIDs, int[][] topLayerUIDs, ObjectHandler objectHandler) {
		this.bottomLayerUIDs = bottomLayerUIDs;
		this.middleLayerUIDs = middleLayerUIDs;
		this.topLayerUIDs = topLayerUIDs;
		this.objectHandler = objectHandler;

		ArrayList<ArrayList<GameObject>> layers = objectHandler.loadLevel(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs);
		bottomLayer = layers.get(0);
		middleLayer = layers.get(1);
		topLayer = layers.get(2);

		neighbors = new Room[4];
		hasRoomExit = new boolean[4];
		playerExitDestinations = new PlayerExitDestination[4];
		findExits(middleLayer);
	}

	// Search the objects in the middle layer to determine the room's
	// exit directions and player exit destinations.
	private void findExits(ArrayList<GameObject> middleLayer) {
		for (int i = middleLayer.size() - 1; i >= 0; i--) {
			GameObject gameObject = middleLayer.get(i);
			Name objectName = gameObject.getObjectId().getName();
			int directionIndex = Direction.convertNameToDirection(objectName).getValue();
			
			if (directionIndex == -1)
				continue;
			
			if (gameObject.getObjectId().getCategory() == Category.RoomExit)
				hasRoomExit[directionIndex] = true;
			else if (gameObject.getObjectId().getCategory() == Category.PlayerExitDestination)
				playerExitDestinations[directionIndex] = (PlayerExitDestination) gameObject;
		}
	}

	/**
	 * Checks if this room has an exit in the given direction.
	 * @param direction of the exit to search for
	 * @return true if an exit in the given direction exists, false otherwise
	 */
	public boolean hasExitInDirection(Direction direction) {
		int directionIndex = direction.getValue();
		return hasRoomExit[directionIndex];
	}

	public Room createDeepCopy() {
		return new Room(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs, objectHandler);
	}
	
	public PlayerExitDestination getPlayerExitDestination(Direction direction) {
		int directionIndex = direction.getValue();
		return playerExitDestinations[directionIndex];
	}

	public void disableUnusedExits() {
		List<Direction> unusedExitLocations = getAvailableExitDirections();
		for (int i = middleLayer.size() - 1; i >= 0; i--) {
			GameObject gameObject = middleLayer.get(i);
			Name objectName = gameObject.getObjectId().getName();
			if (gameObject.getObjectId().getCategory() == Category.RoomExit) {
				for (int j = unusedExitLocations.size() - 1; j >= 0; j--) {
					Direction locationFromName = Direction.convertNameToDirection(objectName);
					if (locationFromName == unusedExitLocations.get(j)) {
						// TODO implement a better way of removing exits which is visually appealing
						middleLayer.remove(i);
						GameObject disabledExitPlaceholder = 
								objectHandler.createObjectByName(Name.GrassBackgroundTileBlock_Center,
										(int) gameObject.getX(),
										(int) gameObject.getY());
						middleLayer.add(disabledExitPlaceholder);
					}
				}
			}
		}
	}

	/**
	 * Retrieves the exits of this room which are not yet linked to other rooms.
	 * @return the list of available exits
	 */
	public List<Direction> getAvailableExitDirections() {
		List<Direction> list = new ArrayList<>();
		for (int i = 0; i < neighbors.length; i++) {
			Direction direction = Direction.getByValue(i);
			if (hasExitInDirection(direction) && getNeighbor(direction) == null)
				list.add(direction);
		}
		return list;
	}

	/**
	 * Returns the neighboring room at the given direction.
	 * @param direction the direction towards the neighbor
	 * @return the neighbor
	 */
	public Room getNeighbor(Direction direction) {
		int directionIndex = direction.getValue();
		return neighbors[directionIndex];
	}

	/**
	 * Set the neighboring room that the given exit of this room leads to.
	 * @param direction the direction of the exit that leads to the neighbor
	 * @param neighbor the neighbor to be added at the given direction
	 */
	public void setNeighbor(Direction direction, Room neighbor) {
		int directionIndex = direction.getValue();
		neighbors[directionIndex] = neighbor;
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

}
