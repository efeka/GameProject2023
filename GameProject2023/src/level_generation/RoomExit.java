package level_generation;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Color;
import java.awt.Graphics;

import abstract_templates.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public class RoomExit extends GameObject {
	
	private ObjectHandler objectHandler;
	
	public RoomExit(float x, float y, ObjectHandler objectHandler, Name objectName) {
		super(x, y, TILE_SIZE, TILE_SIZE, new ObjectId(Category.Exit, objectName));
		this.objectHandler = objectHandler;
		
		int imageIndex = -1;
		switch(objectName) {
		case RoomExitUp:
			imageIndex = 0;
			break;
		case RoomExitDown:
			imageIndex = 2;
			break;
		case RoomExitLeft:
			imageIndex = 1;
			break;
		case RoomExitRight:
			imageIndex = 3;
			break;		
		}
		texture = TextureLoader.getInstance().getTextures(TextureName.ExitTiles)[imageIndex];
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}

}
