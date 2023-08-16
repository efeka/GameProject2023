package object_templates;

import framework.GameConstants;
import framework.GameObject;
import framework.ObjectId;

public abstract class Creature extends GameObject {

	protected final float GRAVITY = GameConstants.CreatureConstants.GRAVITY;
	protected final int TERMINAL_VELOCITY = GameConstants.CreatureConstants.TERMINAL_VELOCITY;
	
	protected float velX, velY;
	protected boolean falling, jumping;
	// 1 for right, -1 for left
	protected int direction = 1;
	
	public Creature(int x, int y, int width, int height, ObjectId objectId) {
		super(x, y, width, height, objectId);
		
		falling = true;
		jumping = false;
	}
	
	public Creature() {}

}
