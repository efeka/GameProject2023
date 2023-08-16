package game_objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import framework.ObjectId.Name;
import object_templates.TileBlock;
import object_templates.TileOrientation;
import framework.TextureLoader;

public class StoneTileBlock extends TileBlock {

	public StoneTileBlock(int x, int y, Name objectName, TileOrientation orientation) {
		super(x, y, objectName, orientation);
		
		BufferedImage[] tileSet = TextureLoader.getInstance().stoneSprites;
		texture = getImageByOrientation(tileSet, orientation);
	}
	
	public StoneTileBlock(TileOrientation orientation) {
		super(orientation);
		BufferedImage[] tileSet = TextureLoader.getInstance().stoneSprites;
		texture = getImageByOrientation(tileSet, orientation);
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}

}
