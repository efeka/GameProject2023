package game_objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstract_objects.TileBlock;
import abstract_objects.TileOrientation;
import framework.ObjectId.Name;
import framework.TextureLoader;

public class RockTileBlock extends TileBlock {

	public RockTileBlock(int x, int y, Name objectName, TileOrientation orientation) {
		super(x, y, objectName, orientation);
		
		BufferedImage[] tileSet = TextureLoader.getInstance().rockSprites;
		texture = getImageByOrientation(tileSet, orientation);
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}
	
}
