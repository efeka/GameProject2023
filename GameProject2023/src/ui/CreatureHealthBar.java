package ui;

import static framework.GameConstants.ScaleConstants.TILE_SIZE;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import abstracts.Creature;
import abstracts.GameObject;
import framework.BufferedImageUtil;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class CreatureHealthBar extends GameObject {

	private Creature parentObject;
	private int yOffset = 0;

	private BufferedImage[] textures;
	
	private int prevHealth;
	private float healthDiff = 0, deltaHealthDiff = 0.5f;

	public CreatureHealthBar(Creature parentObject, int yOffset) {
		super(0, 0, 0, 0, new ObjectId(Category.Missing, Name.Missing));
		this.parentObject = parentObject;
		this.yOffset = yOffset;
		
		float unitHealthWidth = TILE_SIZE / 50f;
		width = (int) (unitHealthWidth * parentObject.getMaxHealth());
		height = (int) (TILE_SIZE * 0.3f);
		
		loadTextures();
	}
	
	@Override
	public void tick() {
		if (parentObject == null)
			return;

		x = parentObject.getX() + parentObject.getWidth() / 2 - width / 2;
		y = parentObject.getY() + yOffset;
		
		int health = parentObject.getHealth();
		if (prevHealth != health) {
			healthDiff = health - prevHealth; 
			prevHealth = health;
		}
		if (healthDiff < -0.1f)
			healthDiff += deltaHealthDiff;
		else
			healthDiff = 0;
	}

	@Override
	public void render(Graphics g) {
		int health = parentObject.getHealth();
		int maxHealth = parentObject.getMaxHealth();
		// Don't display the health bar if health is full or at 0
		/*
		if (health == parentObject.getMaxHealth())
			return;
		if (health == 0)
			return;
		*/
		// Health bar damage effect
		if (healthDiff < 0) {
			float healthRatio = (float) (health - healthDiff) / maxHealth;
			int newBarWidth = (int) (width * healthRatio);
			BufferedImage damagedBarImage = BufferedImageUtil.getScaledInstance(
					textures[1],
					width,
					height);
			damagedBarImage = BufferedImageUtil.getLeftClippedImage(damagedBarImage, newBarWidth);
			g.drawImage(damagedBarImage, (int) x, (int) y, null);
		}
		
		// Health bar
		float healthRatio = (float) health / parentObject.getMaxHealth();
		int newBarWidth = (int) (width * healthRatio);
		BufferedImage healthBarImage = BufferedImageUtil.getScaledInstance(
				textures[0],
				width,
				height);
		healthBarImage = BufferedImageUtil.getLeftClippedImage(healthBarImage, newBarWidth);
		g.drawImage(healthBarImage, (int) x, (int) y, null);
	}
	
	private void loadTextures() {
		TextureLoader textureLoader = TextureLoader.getInstance();
		textures = new BufferedImage[] {
				textureLoader.getTextures(TextureName.HUD)[3],
				textureLoader.getTextures(TextureName.HUD)[4],
		};
	}

}
