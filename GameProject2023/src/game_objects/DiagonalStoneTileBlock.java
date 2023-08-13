package game_objects;

import java.awt.Graphics;

import framework.ObjectId.Name;
import object_templates.DiagonalTileBlock;
import object_templates.TileOrientation;
import framework.TextureLoader;

public class DiagonalStoneTileBlock extends DiagonalTileBlock {

	public DiagonalStoneTileBlock(float x, float y, Name objectName,
			TileOrientation orientation) {
		super(x, y, objectName, orientation);
		
		TextureLoader textureLoader = TextureLoader.getInstance();
		switch (diagonalDirection) {
		case AscendLeft:
			texture = textureLoader.diagonalStoneSprites[0]; 
			break;
		case AscendRight:
			texture = textureLoader.diagonalStoneSprites[1];
			break;
		case DescendLeft:
			texture = textureLoader.diagonalStoneSprites[2];
			break;
		case DescendRight:
			texture = textureLoader.diagonalStoneSprites[3];
			break;
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int) x, (int) y, width, height, null);
	}

}
