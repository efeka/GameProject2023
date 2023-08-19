package window;

import java.awt.Color;
import java.awt.Graphics;

import framework.GameObject;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import game_objects.Player;

public class HUD extends GameObject {

	private Player player;

	private float healthWidthUnit;
	private float staminaWidthUnit;
	private int healthHeight;
	private int staminaHeight;

	public HUD(float x, float y, int width, int height, Player player) {
		super(x, y, width, height, new ObjectId(Category.Menu, Name.HUD));
		this.player = player;

		if (player == null) {
			healthWidthUnit = staminaWidthUnit = 0;
			healthHeight = staminaHeight = 0;
		}
		else {
			healthWidthUnit = (float) width / player.getMaxHealth();
			staminaWidthUnit = (float) width / player.getMaxStamina();
			healthHeight = 3 * height / 5;
			staminaHeight = height - healthHeight;
		}
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		if (player == null)
			return;
		
		// These are temporary
		// Max health and stamina
		g.setColor(new Color(51, 51, 51));
		g.fillRect((int) x, (int) y, (int) (player.getMaxHealth() * healthWidthUnit), healthHeight);
		g.fillRect((int) x, (int) y + healthHeight, (int) (player.getMaxStamina() * staminaWidthUnit), staminaHeight);

		// Health bar
		g.setColor(new Color(220, 80, 80));
		g.fillRect((int) x, (int) y, (int) (player.getHealth() * healthWidthUnit), healthHeight);
		// Stamina bar
		g.setColor(new Color(80, 220, 80));
		g.fillRect((int) x, (int) y + healthHeight, (int) (player.getStamina() * staminaWidthUnit), staminaHeight);

		// Max health and stam borders
		g.setColor(Color.BLACK);
		g.drawRect((int) x, (int) y, (int) (player.getMaxHealth() * healthWidthUnit), healthHeight);
		g.drawRect((int) x, (int) y + healthHeight, (int) (player.getMaxStamina() * staminaWidthUnit), staminaHeight);
	}

}
