package game_objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstract_objects.JumpThroughTileBlock;
import abstract_objects.TileOrientation;
import framework.TextureLoader;
import framework.ObjectId.Name;
import framework.TextureLoader.TextureName;

public class WoodJumpThroughTileBlock extends JumpThroughTileBlock {

	public WoodJumpThroughTileBlock(float x, float y, Name objectName, TileOrientation orientation) {
		super(x, y, objectName, orientation);
		
		BufferedImage[] tileSet = TextureLoader.getInstance().getTextures(TextureName.WoodJumpThroughTiles);
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
