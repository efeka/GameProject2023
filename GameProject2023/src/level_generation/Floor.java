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

	private ArrayList<Room> roomList;
	private Room startingRoom;
	private Room currentRoom;

	public Floor(int roomCount, ObjectHandler objectHandler) {
		this.objectHandler = objectHandler;
		roomList = new ArrayList<>();
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
					roomList.add(new Room(bottomLayerUIDs, middleLayerUIDs, topLayerUIDs, objectHandler));
			}
		} catch (NullPointerException e) {}
		
		currentRoom = startingRoom;
	}
	
	public Room getCurrentRoom() {
		return currentRoom;
	}
	
	public void setCurrentRoom(Room currentRoom) {
		this.currentRoom = currentRoom;
	}

}
