package floor_generation;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstracts.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class RoomExit extends GameObject {
	
	private ObjectHandler objectHandler;
	
	private boolean locked = false;
	private BufferedImage lockedImage;
	
	/**
	 * These objects are placed in rooms that are connected to other rooms on the floor.
	 * They are used as an exiting point for the room they belong to, and the direction
	 * of the exit is defined by the {@code objectName}.
	 * 
	 * @param x the x coordinate of the exit
	 * @param y the y coordinate of the exit
	 * @param objectHandler the objectHandler
	 * @param objectName the name of this object which defines its direction
	 */
	public RoomExit(float x, float y, ObjectHandler objectHandler, Name objectName) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(Category.RoomExit, objectName));
		this.objectHandler = objectHandler;
		
		int imageIndex;
		switch(objectName) {
		case RoomExitUp:
			imageIndex = 0;
			break;
		case RoomExitDown:
			imageIndex = 2;
			break;
		case RoomExitLeft:
			imageIndex = 3;
			break;
		case RoomExitRight:
			imageIndex = 1;
			break;
		default:
			imageIndex = -1;
			break;		
		}
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = textureLoader.getTextures(TextureName.ExitTiles)[imageIndex];
		lockedImage = textureLoader.getTextures(TextureName.Missing)[0];
	}

	@Override
	public void tick() {
		if (locked)
			return;
		
		// If the player is colliding with this object, move the them to the next room
		if (getBounds().intersects(objectHandler.getPlayer().getBounds())) {
			RoomDirection roomDirection = RoomDirection.convertNameToDirection(objectId.getName());
			objectHandler.loadNeighboringRoom(this, roomDirection);
		}
	}
	
	@Override
	public void render(Graphics g) {
		BufferedImage image = locked ? lockedImage : texture;
		g.drawImage(image, (int) x, (int) y, width, height, null);
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

}
