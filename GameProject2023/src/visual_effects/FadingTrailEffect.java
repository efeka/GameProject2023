package visual_effects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import abstracts.GameObject;
import framework.ObjectHandler;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;

public class FadingTrailEffect extends GameObject {

	private float alpha = 1;
	private float fadingRate;
	private BufferedImage image;
	
	private ObjectHandler objectHandler;
	
	public FadingTrailEffect(float x, float y, int width, int height, BufferedImage image, float fadingRate, ObjectHandler objectHandler) {
		super(x, y, width, height, new ObjectId(Category.Missing, Name.Missing));
		this.image = image;
		this.fadingRate = fadingRate;
		this.objectHandler = objectHandler;
	}
	
	public FadingTrailEffect(float x, float y, int width, int height, BufferedImage image, float initialAlpha, float fadingRate, ObjectHandler objectHandler) {
		super(x, y, width, height, new ObjectId(Category.Missing, Name.Missing));
		this.image = image;
		this.fadingRate = fadingRate;
		this.objectHandler = objectHandler;
		this.alpha = initialAlpha;
	}

	@Override
	public void tick() {
		if (alpha > fadingRate) 
			alpha -= fadingRate;
		else
			objectHandler.removeObject(this);
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(makeTransparent(alpha));
		g2d.drawImage(image, (int) x, (int) y, width, height, null);
		g2d.setComposite(makeTransparent(1));
	}
	
	private AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	
}
