package game_objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import framework.ObjectId.Name;
import general_object_types.Block;

public class GrassBlock extends Block {

	public GrassBlock(int x, int y, int width, int height, Name objectName) {
		super(x, y, width, height, objectName);
		
	}

	@Override
	public void update() {
	}

	@Override
	public void render(Graphics g) {
	}

	@Override
	public BufferedImage getTexture() {
		return null;
	}

}
