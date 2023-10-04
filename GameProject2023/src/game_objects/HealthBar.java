package game_objects;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstracts.Creature;
import abstracts.GameObject;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class HealthBar extends GameObject {

	private Creature parent;
	private float unitHealthWidth = 15f / TILE_SIZE;
	
	private BufferedImage[] textures;
	
	public HealthBar(Creature parent) {
		super(0, 0, 0, 0, new ObjectId(Category.Missing, Name.Missing));
		this.parent = parent;
		
		width = (int) (unitHealthWidth * parent.getHealth());
		height = TILE_SIZE;
		
		textures = TextureLoader.getInstance().getTextures(TextureName.HealthBar);
	}

	@Override
	public void tick() {
		x = parent.getX() + (parent.getWidth() - width) / 2;
		y = parent.getY() - height + 20;
	}

	@Override
	public void render(Graphics g) {
		// Bar borders
		g.drawImage(textures[2], (int) x, (int) y, width, height, null);
		
		// Current health
		if (parent.getHealth() > 0) {
			float healthRatio = parent.getMaxHealth() / parent.getHealth();
			int croppedImageWidth = (int) (textures[1].getWidth() / healthRatio);
			BufferedImage healthTexture = textures[1].getSubimage(0, 0, croppedImageWidth, textures[1].getHeight());
			g.drawImage(healthTexture, (int) x, (int) y, (int) (parent.getHealth() * unitHealthWidth), height, null);
		}
	}
	
}
