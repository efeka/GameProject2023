package general_object_types;

import framework.GameConstants;
import framework.GameObject;
import framework.ObjectId;

public abstract class Creature extends GameObject {

	protected final float GRAVITY = GameConstants.GRAVITY;
	protected final int TERMINAL_VELOCITY = GameConstants.TERMINAL_VELOCITY;
	
	protected float velX, velY;
	protected boolean falling, jumping;
	
	public Creature(int x, int y, int width, int height, ObjectId objectId) {
		super(x, y, width, height, objectId);
		
		falling = true;
		jumping = false;
	}

}
