package game_objects.tiles;

import java.awt.Graphics;

import abstracts.DiagonalTileBlock;
import abstracts.TileOrientation;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class DiagonalStoneTileBlock extends DiagonalTileBlock {

	public DiagonalStoneTileBlock(float x, float y, Name objectName,
			TileOrientation orientation) {
		super(x, y, objectName, orientation);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		texture = getImageByOrientation(textureLoader.getTextures(TextureName.DiagonalStoneTiles), orientation);
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}

}
