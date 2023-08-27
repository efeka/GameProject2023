package game_objects;

import java.awt.Graphics;

import abstract_objects.DiagonalTileBlock;
import abstract_objects.TileOrientation;
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
