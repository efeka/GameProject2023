package game_objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import framework.TextureLoader;
import framework.ObjectId.Name;
import object_templates.JumpThroughTileBlock;
import object_templates.TileOrientation;

public class WoodJumpThroughTileBlock extends JumpThroughTileBlock {

	public WoodJumpThroughTileBlock(float x, float y, Name objectName, TileOrientation orientation) {
		super(x, y, objectName, orientation);
		
		BufferedImage[] tileSet = TextureLoader.getInstance().woodJumpThroughSprites;
		texture = getImageByOrientation(tileSet, orientation);
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}

}
