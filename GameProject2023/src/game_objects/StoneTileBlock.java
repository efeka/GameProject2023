package game_objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import framework.ObjectId.Name;
import framework.TextureLoader;
import general_object_types.TileBlock;
import general_object_types.TileOrientation;

public class StoneTileBlock extends TileBlock {

	public StoneTileBlock(int x, int y, int width, int height, Name objectName, TileOrientation orientation) {
		super(x, y, width, height, objectName, orientation);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		BufferedImage[] tileSet = textureLoader.stoneSprites;
		texture = tileSet[orientation.getValue()];
	}

	@Override
	public void update() {}

	@Override
	public void render(Graphics g) {
		g.drawImage(getTexture(), x, y, width, height, null);
	}

}
