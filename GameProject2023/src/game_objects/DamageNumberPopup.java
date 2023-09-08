package game_objects;

import java.awt.Color;
import java.awt.Graphics;

import abstract_templates.GameObject;
import framework.GameConstants;
import framework.ObjectHandler;

public class DamageNumberPopup extends GameObject {

	private ObjectHandler objectHandler;
	private String text;
	
	private Color color;
	private int colorAlpha = 255; 
	private float alphaReductionRate;
	
	public DamageNumberPopup(float x, float y, int damage, ObjectHandler objectHandler) {
		super(x, y, 0, 0, null);
		this.objectHandler = objectHandler;

		if (damage >= 30) {
			color = Color.RED;
			text = damage + "!";
		}
		else {
			color = Color.WHITE;
			text = damage + "";
		}
		
		int fadingRate = 500;
		alphaReductionRate = fadingRate / 255f;
	}

	@Override
	public void tick() {
		colorAlpha -= alphaReductionRate;
		y -= 0.1f;
		
		if (colorAlpha < 0) {
			colorAlpha = 0;
			objectHandler.removeObjectFromLayer(this, ObjectHandler.MENU_LAYER);
		}
	}

	@Override
	public void render(Graphics g) {
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), colorAlpha);
		g.setColor(color);
		g.setFont(GameConstants.FontConstants.DAMAGE_FONT);
		g.drawString(text, (int) x, (int) y);
	}

}
