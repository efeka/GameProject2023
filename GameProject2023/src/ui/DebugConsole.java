package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import abstracts.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import game_objects.Player;
import window.KeyInput;

public class DebugConsole extends GameObject {

	private KeyInput keyInput;
	private ObjectHandler objectHandler;
	
	public DebugConsole(float x, float y, KeyInput keyInput, ObjectHandler objectHandler) {
		super(x, y, 0, 0, new ObjectId(Category.Missing, Name.Missing));
		this.keyInput = keyInput;
		this.objectHandler = objectHandler;
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {
		// Debug
		if (keyInput.debugPressed && objectHandler.getPlayer() != null) {
			Player player = objectHandler.getPlayer();
			int x = (int) this.x;
			int y = (int) this.y;
			
			g.setFont(new Font("Calibri", Font.PLAIN, 15));
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(x - 5, y, 170, 215);

			g.setColor(new Color(220, 80, 80));
			g.drawString("Health......................" + player.getHealth(), x, y += 20);
			
			g.setColor(Color.CYAN);
			g.drawString("velX: " + player.getVelX(), x, y += 20);
			g.drawString("velY: " + player.getVelY(), x, y += 20);

			g.setColor(Color.white);
			g.drawString("Falling....................." + player.isFalling(), x, y += 20);
			g.drawString("Jumping.................." + player.isJumping(), x, y += 20);
			g.drawString("Double Jumping......." + player.isDoubleJumping(), x, y += 20);
			g.drawString("Landing..............." + player.isLanding(), x, y += 20);
			g.drawString("Invulnerable............" + player.isInvulnerable(), x, y += 20);
			g.drawString("Knocked back.........." + player.isKnockedBack(), x, y += 20);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.white);
			g2d.draw(player.getBounds());
		}
	}

}
