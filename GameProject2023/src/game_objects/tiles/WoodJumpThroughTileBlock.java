package game_objects.tiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstracts.JumpThroughTileBlock;
import abstracts.TileOrientation;
import framework.ObjectId.Name;
import framework.TextureLoader;
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
