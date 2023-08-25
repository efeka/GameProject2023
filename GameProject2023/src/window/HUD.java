package window;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import abstract_objects.GameObject;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import game_objects.Player;
import player_weapons.Weapon;

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
	
		// Ability cooldowns
		int ovalSize = 40;
		g.setColor(Color.WHITE);
		g.drawOval((int) x, 50, ovalSize, ovalSize);
		Font font = new Font("Calibri", Font.BOLD, 18);
		FontMetrics metrics = g.getFontMetrics(font);
		int strX = (int) x + (ovalSize - metrics.stringWidth("Q")) / 2;
		int strY = 53 + ((ovalSize - metrics.getHeight()) / 2) + metrics.getAscent();
		g.setFont(font);
		g.drawString("Q", strX, strY);
		
		Weapon weapon = player.getWeapon();
		int timeLeft = weapon.getAbility(0).timeLeftUntilReady();
		int maxTime = weapon.getAbility(0).getCooldown();
		if (timeLeft > 0) {
			g.setColor(new Color(255, 255, 255, 80));
			float cooldownRatio = (float) timeLeft / maxTime;
			g.fillArc((int) x, 50, ovalSize, ovalSize, 90, (int) (-360 * cooldownRatio));
		}
		strX += 60;
		g.setColor(Color.WHITE);
		g.drawOval((int) x + 58, 50, ovalSize, ovalSize);
		g.drawString("E", strX, strY);
		timeLeft = weapon.getAbility(1).timeLeftUntilReady();
		maxTime = weapon.getAbility(1).getCooldown();
		if (timeLeft > 0) {
			g.setColor(new Color(255, 255, 255, 80));
			float cooldownRatio = (float) timeLeft / maxTime;
			g.fillArc((int) x + 60, 50, ovalSize, ovalSize, 90, (int) (-360 * cooldownRatio));
		}
	}

}
