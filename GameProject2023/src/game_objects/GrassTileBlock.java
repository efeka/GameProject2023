package game_objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstract_templates.TileBlock;
import abstract_templates.TileOrientation;
import framework.ObjectId.Name;
import framework.TextureLoader.TextureName;
import framework.TextureLoader;

public class GrassTileBlock extends TileBlock {

	public GrassTileBlock(int x, int y, Name objectName, TileOrientation orientation) {
		super(x, y, objectName, orientation);
		
		BufferedImage[] tileSet = TextureLoader.getInstance().getTextures(TextureName.GrassTiles);
		texture = getImageByOrientation(tileSet, orientation);
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}
	
}
