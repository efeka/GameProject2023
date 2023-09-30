package level_generation;

import java.util.ArrayList;

import framework.FileIO;
import framework.ObjectHandler;

public class Floor {
	/*
	 * Levellar teker teker dosyalardan okunup Room objelerine donusturulmeli.
	 * Roomlar hasUp, hasLeft gibi exit konumlarini tutmali.
	 * 
	 * Tum odalar dosyadan okunduktan sonra Floorun icinde procedural bi floor olusturulmali.
	 * Bu asamada roomlarin up, down, left, right referanslari tanimlanacak.
	 * Floorun icinde current room olmali ki player bi exita girdiginde,
	 * current roomun linklerinden kolayca siradaki rooma eriselim. 	
	 */

	private ObjectHandler objectHandler;

	// Contains all the available rooms which are loaded from the file system
	private ArrayList<Room> roomPool;
	// Contains the randomly generated room layout for this floor
	private ArrayList<Room> floorRooms;
	private Room startingRoom;
	private Room currentRoom;

	public Floor(ObjectHandler objectHandler) {
		this.objectHandler = objectHandler;
		roomPool = new ArrayList<>();
		createRooms();
	}

	public void createRooms() {
		FileIO fileIO = new FileIO();
		try {
			for (int i = 0; ; i++) {
				int[][] bottomLayerUIDs = fileIO.loadLevel("levels_bg.txt", i);
				int[][] middleLayerUIDs = fileIO.loadLevel("levels.txt", i);
				int[][] topLayerUIDs = fileIO.loadLevel("levels_fg.txt", i);
				if (i == 0)
					startingRoom = new Room(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs, objectHandler);
				else
					roomPool.add(new Room(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs, objectHandler));
			}
		} catch (NullPointerException e) {}
		
		currentRoom = startingRoom;
	}
	
	/**
	 * Procedurally generates a random floor using rooms selected from the room pool.
	 * Reaching the max room count may not be possible due to randomness.
	 * @param maxRoomCount the maximum number of rooms in the floor
	 */
	public void generateRandomFloor(int maxRoomCount) {
		// Keeps track of rooms that still have unused exits
		ArrayList<Room> leafRooms = new ArrayList<>();
		leafRooms.add(startingRoom);
		floorRooms.add(startingRoom);
		
		while (!leafRooms.isEmpty() && maxRoomCount-- > 0) {
			// Randomly select a leaf room and remove it from the leaf list
			int randomLeafRoomIndex = (int) (Math.random() * leafRooms.size());
			Room randomLeafRoom = leafRooms.remove(randomLeafRoomIndex);
			
			// Randomly select an unused exit of the randomLeafRoom
			ArrayList<ExitLocation> unusedExits = randomLeafRoom.getUnusedExitLocations();
			if (!unusedExits.isEmpty()) { 
				int randomExitIndex = (int) (Math.random() * unusedExits.size());
				ExitLocation selectedExit = unusedExits.get(randomExitIndex);
				
				// Randomly select a room from the room pool which has the opposite of the selected exit
				ExitLocation oppositeExit = selectedExit.getOppositeExitLocation(selectedExit);
				Room newRoom = getRandomRoomWithExit(oppositeExit);
				
	            if (newRoom != null) {
	                // Link the currentLeafRoom and the newRoom together
	            	randomLeafRoom.setNeighbor(selectedExit, newRoom);
	            	newRoom.setNeighbor(oppositeExit, randomLeafRoom);
	            	floorRooms.add(newRoom);

	                // Check to see if the randomLeafRoom or newRoom have more unused exits.
	            	// If they do, add them into the leafRooms list.
	            	if (!randomLeafRoom.getUnusedExitLocations().isEmpty())
	            		leafRooms.add(randomLeafRoom);
	            	if (!newRoom.getUnusedExitLocations().isEmpty())
	            		leafRooms.add(newRoom);
	            }
			}
		}
	}
	
	/**
	 * Retrieves a randomly selected room from the roomPool with the required exit location.
	 * @param neededExitLocation the exit location to search for
	 * @return the room with the required exit location
	 */
	private Room getRandomRoomWithExit(ExitLocation neededExitLocation) {
		ArrayList<Room> eligibleRooms = new ArrayList<>();
		for (Room room : roomPool)
			if (room.hasExitLocation(neededExitLocation))
				eligibleRooms.add(room);
			
		int randomRoomIndex = (int) (Math.random() * eligibleRooms.size());
		Room randomRoom = eligibleRooms.get(randomRoomIndex);
		if (randomRoom != null)
			return randomRoom.createCopy();
		else
			return null;
	}
	
	public Room getCurrentRoom() {
		return currentRoom;
	}
	
	public void setCurrentRoom(Room currentRoom) {
		this.currentRoom = currentRoom;
	}

}
