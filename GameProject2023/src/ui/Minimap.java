package ui;

import static framework.GameConstants.ScaleConstants.GAME_WIDTH;
import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abstracts.GameObject;
import floor_generation.Floor;
import floor_generation.Room;
import floor_generation.RoomDirection;
import framework.GameConstants;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class Minimap extends GameObject {

	private Floor floor;
	private Map<Room, Rectangle> roomBounds;
	private List<Rectangle> neighborConnections;

	private BufferedImage[] textures;
	
	public Minimap(int width, int height, Floor floor) {
		super(GAME_WIDTH - width, 0, width, height, new ObjectId(Category.Menu, Name.Missing));
		this.floor = floor;

		textures = TextureLoader.getInstance().getTextures(TextureName.Minimap);
		
		roomBounds = new HashMap<>();
		neighborConnections = new ArrayList<>();
		
		int roomSize = TILE_SIZE / 3;
		calculateRoomBounds(roomSize, TILE_SIZE / 4, TILE_SIZE / 12);
		stickMapToTopRight(roomSize);
	}

	// TODO Make the map transparent if it collides with important game objects
	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		// Draw the connector shapes
		for (Rectangle rect : neighborConnections)
			g.drawImage(textures[3], rect.x, rect.y, rect.width, rect.height, null);
		
		// Draw the room shapes
		for (Map.Entry<Room, Rectangle> entry : roomBounds.entrySet()) {
			BufferedImage texture = textures[0];
			if (entry.getKey().equals(floor.getCurrentRoom()))
				texture = textures[2];
			else if (entry.getKey().equals(floor.getStartingRoom()))
				texture = textures[1];
			
			Rectangle rect = entry.getValue();
			g.drawImage(texture, rect.x, rect.y, rect.width, rect.height, null);
		}
	}

	private void calculateRoomBounds(int roomSize, int connectionLength, int connectionWidth) {
		Map<Room, Point2D> roomPositions = floor.getRoomPositions();
		for (Map.Entry<Room, Point2D> entry : roomPositions.entrySet()) {
			Point2D roomPosition = entry.getValue();
			int boundsX = (int) x + (int) roomPosition.getX() * (roomSize + connectionLength / 2);
			int boundsY = (int) y + (int) roomPosition.getY() * (roomSize + connectionLength / 2);
			roomBounds.put(entry.getKey(), new Rectangle(boundsX, boundsY, roomSize, roomSize));

			Room room = entry.getKey();
			int connectionOffset = (roomSize - connectionWidth) / 2;
			// Add rectangles between rooms to signify neighbor status
			if (room.getNeighbor(RoomDirection.Up) != null) {
				Rectangle rect = new Rectangle(
						boundsX + connectionOffset,
						boundsY - connectionLength,
						connectionWidth,
						connectionLength);
				neighborConnections.add(rect);
			}
			if (room.getNeighbor(RoomDirection.Down) != null) {
				Rectangle rect = new Rectangle(
						boundsX + connectionOffset,
						boundsY + roomSize,
						connectionWidth,
						connectionLength);
				neighborConnections.add(rect);
			}
			if (room.getNeighbor(RoomDirection.Left) != null) {
				Rectangle rect = new Rectangle(
						boundsX - connectionLength,
						boundsY + connectionOffset,
						connectionLength,
						connectionWidth);
				neighborConnections.add(rect);
			}
			if (room.getNeighbor(RoomDirection.Right) != null) {
				Rectangle rect = new Rectangle(
						boundsX + roomSize,
						boundsY + connectionOffset,
						connectionLength,
						connectionWidth);
				neighborConnections.add(rect);
			}
		}
	}

	// Places the map on the top right corner of the screen with the given margin
	private void stickMapToTopRight(int margin) {
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		for (Map.Entry<Room, Rectangle> entry : roomBounds.entrySet()) {
			Rectangle rect = entry.getValue();
			if (rect.x > maxX)
				maxX = rect.x;
			if (rect.y < minY)
				minY = rect.y;
		}
		
		int yOffset = margin - minY;
		int xOffset = (GameConstants.ScaleConstants.GAME_WIDTH - margin * 2) - maxX;
		for (Map.Entry<Room, Rectangle> entry : roomBounds.entrySet()) {
			Rectangle rect = entry.getValue();
			entry.setValue(new Rectangle(rect.x + xOffset, rect.y + yOffset, rect.width, rect.height));
		}
		
		for (Rectangle rect : neighborConnections) {
			rect.x += xOffset;
			rect.y += yOffset;
		}
	}
	
}
