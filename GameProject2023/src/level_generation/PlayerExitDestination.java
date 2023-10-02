package level_generation;

import java.awt.Graphics;

import abstract_templates.GameObject;
import framework.ObjectId;
import framework.ObjectId.Category;
import framework.ObjectId.Name;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;

public class PlayerExitDestination extends GameObject {

	/**
	 * The player gets moved to the location of this object when going through an exit. 
	 * The player location differs depending on the location of the exit.
	 * 
	 * The direction assigned to this class should be the opposite of the exit that
	 * the player will come out of. 
	 * For example, to set the player destination for when they come out of a downwards exit,
	 * the object of this class should be pointing upwards. This behaviour is defined by
	 * the {@code objectName}.
	 */
	public PlayerExitDestination(float x, float y, Name objectName) {
		super(x, y, 0, 0, new ObjectId(Category.PlayerExitDestination, objectName));

		int imageIndex;
		switch(objectName) {
		case PlayerExitDestinationUp:
			imageIndex = 6;
			break;
		case PlayerExitDestinationDown:
			imageIndex = 4;
			break;
		case PlayerExitDestinationLeft:
			imageIndex = 5;
			break;
		case PlayerExitDestinationRight:
			imageIndex = 7;
			break;
		default:
			imageIndex = -1;
			break;		
		}
		texture = TextureLoader.getInstance().getTextures(TextureName.ExitTiles)[imageIndex];
	}

	@Override
	public void tick() {}

	@Override
	public void render(Graphics g) {}

}
