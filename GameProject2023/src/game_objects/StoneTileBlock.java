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
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		BufferedImage[] tileSet = textureLoader.stoneSprites;
		texture = tileSet[orientation.getValue()];
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}

}
