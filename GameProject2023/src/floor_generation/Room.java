package floor_generation;

import java.util.ArrayList;
import java.util.List;

import abstracts.Creature;
import abstracts.GameObject;
import framework.ObjectHandler;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public class Room {

	private ObjectHandler objectHandler;
	private int[][] bottomLayerUIDs, middleLayerUIDs, topLayerUIDs;
	private int[][][] enemyWavesUIDs;
	private List<GameObject> bottomLayer, middleLayer, topLayer;
	
	private List<List<Creature>> enemyWaves;
	private int currentWave = -1;
	
	private boolean areExistsLocked = false;
	
	private Room[] neighbors;
	private List<RoomExit> roomExits;
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
	public Room(int[][] bottomLayerUIDs, int[][] middleLayerUIDs, int[][] topLayerUIDs, 
			int[][][] enemyWavesUIDs, ObjectHandler objectHandler) {
		this.bottomLayerUIDs = bottomLayerUIDs;
		this.middleLayerUIDs = middleLayerUIDs;
		this.topLayerUIDs = topLayerUIDs;
		this.enemyWavesUIDs = enemyWavesUIDs;
		this.objectHandler = objectHandler;

		List<List<GameObject>> layers = objectHandler.loadLevel(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs);
		bottomLayer = layers.get(0);
		middleLayer = layers.get(1);
		topLayer = layers.get(2);
		enemyWaves = objectHandler.loadEnemyWaves(enemyWavesUIDs[0], enemyWavesUIDs[1], enemyWavesUIDs[2]);
		
		neighbors = new Room[4];
		hasRoomExit = new boolean[4];
		roomExits = new ArrayList<>(4);
		playerExitDestinations = new PlayerExitDestination[4];
		findExits(middleLayer);
	}

	// Search the objects in the middle layer to determine the room's
	// exit directions and player exit destinations.
	private void findExits(List<GameObject> middleLayer) {
		for (int i = middleLayer.size() - 1; i >= 0; i--) {
			GameObject gameObject = middleLayer.get(i);
			Name objectName = gameObject.getObjectId().getName();
			int directionIndex = RoomDirection.convertNameToDirection(objectName).getValue();
			
			if (directionIndex == -1)
				continue;
			
			if (gameObject.compareCategory(Category.RoomExit)) {
				hasRoomExit[directionIndex] = true;
				roomExits.add((RoomExit) gameObject);
			}
			else if (gameObject.compareCategory(Category.PlayerExitDestination))
				playerExitDestinations[directionIndex] = (PlayerExitDestination) gameObject;
		}
	}

	/**
	 * Checks if this room has an exit in the given direction.
	 * @param roomDirection of the exit to search for
	 * @return true if an exit in the given direction exists, false otherwise
	 */
	public boolean hasExitInDirection(RoomDirection roomDirection) {
		int directionIndex = roomDirection.getValue();
		return hasRoomExit[directionIndex];
	}

	public Room createDeepCopy() {
		return new Room(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs, enemyWavesUIDs, objectHandler);
	}
	
	public PlayerExitDestination getPlayerExitDestination(RoomDirection roomDirection) {
		int directionIndex = roomDirection.getValue();
		return playerExitDestinations[directionIndex];
	}

	public void disableUnusedExits() {
		List<RoomDirection> unusedExitLocations = getAvailableExitDirections();
		for (int i = middleLayer.size() - 1; i >= 0; i--) {
			GameObject gameObject = middleLayer.get(i);
			Name objectName = gameObject.getObjectId().getName();
			if (gameObject.compareCategory(Category.RoomExit)) {
				for (int j = unusedExitLocations.size() - 1; j >= 0; j--) {
					RoomDirection locationFromName = RoomDirection.convertNameToDirection(objectName);
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
	public List<RoomDirection> getAvailableExitDirections() {
		List<RoomDirection> list = new ArrayList<>();
		for (int i = 0; i < neighbors.length; i++) {
			RoomDirection roomDirection = RoomDirection.getByValue(i);
			if (hasExitInDirection(roomDirection) && getNeighbor(roomDirection) == null)
				list.add(roomDirection);
		}
		return list;
	}

	/**
	 * Returns the neighboring room at the given direction.
	 * @param roomDirection the direction towards the neighbor
	 * @return the neighbor
	 */
	public Room getNeighbor(RoomDirection roomDirection) {
		int directionIndex = roomDirection.getValue();
		return neighbors[directionIndex];
	}

	/**
	 * Set the neighboring room that the given exit of this room leads to.
	 * @param roomDirection the direction of the exit that leads to the neighbor
	 * @param neighbor the neighbor to be added at the given direction
	 */
	public void setNeighbor(RoomDirection roomDirection, Room neighbor) {
		int directionIndex = roomDirection.getValue();
		neighbors[directionIndex] = neighbor;
	}

	public void lockExits() {
		areExistsLocked = true;
		for (RoomExit roomExit : roomExits)
			roomExit.setLocked(true);
	}
	
	public void unlockExits() {
		areExistsLocked = false;
		for (RoomExit roomExit : roomExits)
			roomExit.setLocked(false);
	}
	
	public boolean areExitsLocked() {
		return areExistsLocked;
	}
	
	public void removeEnemyFromCurrentWave(Creature creature) {
		if (enemyWaves == null)
			return;
		if (enemyWaves.get(currentWave).contains(creature))
			enemyWaves.get(currentWave).remove(creature);
	}
	
	public boolean areAllEnemyWavesCleared() {
		if (enemyWaves == null)
			return true;
		
		boolean allWavesCompleted = true;
		for (List<Creature> wave : enemyWaves) {
			if (wave.size() != 0) {
				allWavesCompleted = false;
				break;
			}
		}
		return allWavesCompleted;
	}
	
	/**
	 * Spawn the next wave of enemies, if it exists.
	 */
	public void spawnNextEnemyWave() {
		if (enemyWaves == null)
			return;
		
		boolean nextWaveExists = false;
		for (currentWave += 1; currentWave < enemyWaves.size(); currentWave++) {
			List<Creature> enemyWave = enemyWaves.get(currentWave);
			if (enemyWave != null && !enemyWave.isEmpty()) {
				nextWaveExists = true;
				break;
			}
		}
		
		if (nextWaveExists) {
			List<Creature> nextWave = enemyWaves.get(currentWave);
			for (int i = 0; i < nextWave.size(); i++)
				objectHandler.addObject(nextWave.get(i), ObjectHandler.MIDDLE_LAYER);
		}
	}
	
	public boolean isCurrentWaveCleared() {
	    return enemyWaves == null ||
	           currentWave >= enemyWaves.size() ||
	           enemyWaves.get(currentWave) == null ||
	           enemyWaves.get(currentWave).isEmpty();
	}
	
	public List<GameObject> getBottomLayer() {
		return bottomLayer;
	}

	public List<GameObject> getMiddleLayer() {
		return middleLayer;
	}

	public List<GameObject> getTopLayer() {
		return topLayer;
	}

}
