package window;

import java.awt.Color;
import java.awt.Graphics;

import framework.GameObject;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import object_templates.PlayerData;

public class HUD extends GameObject {

	private PlayerData playerData;
	
	private float healthWidthUnit;
	private float staminaWidthUnit;
	private int healthHeight;
	private int staminaHeight;
	
	public HUD(float x, float y, int width, int height, PlayerData playerData) {
		super(x, y, width, height, new ObjectId(Category.Menu, Name.Menu));
		this.playerData = playerData;
		
		healthWidthUnit = (float) width / playerData.getMaxHealth();
		staminaWidthUnit = (float) width / playerData.getMaxStamina();
		healthHeight = 3 * height / 5;
		staminaHeight = height - healthHeight;
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		// These are temporary
		// Max health and stamina
		g.setColor(new Color(51, 51, 51));
		g.fillRect((int) x, (int) y, (int) (playerData.getMaxHealth() * healthWidthUnit), healthHeight);
		g.fillRect((int) x, (int) y + healthHeight, (int) (playerData.getMaxStamina() * staminaWidthUnit), staminaHeight);
	
		// Health bar
		g.setColor(new Color(220, 80, 80));
		g.fillRect((int) x, (int) y, (int) (playerData.getCurrentHealth() * healthWidthUnit), healthHeight);
		// Stamina bar
		g.setColor(new Color(80, 220, 80));
		g.fillRect((int) x, (int) y + healthHeight, (int) (playerData.getCurrentStamina() * staminaWidthUnit), staminaHeight);
		
		// Max health and stam borders
		g.setColor(Color.BLACK);
		g.drawRect((int) x, (int) y, (int) (playerData.getMaxHealth() * healthWidthUnit), healthHeight);
		g.drawRect((int) x, (int) y + healthHeight, (int) (playerData.getMaxStamina() * staminaWidthUnit), staminaHeight);
	}
	
}
