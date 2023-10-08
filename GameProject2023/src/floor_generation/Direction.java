package floor_generation;

import framework.ObjectId.Name;

public enum Direction {
	None(-1),
	Up(0),
	Down(1),
	Left(2),
	Right(3);

	private int value;
	
	Direction(int value) {
		this.value = value;
	}
	
	public static Direction getOppositeDirection(Direction direction) {
		Direction oppositeDirection = null;
		switch (direction) {
		case Up:
			oppositeDirection = Down;
			break;
		case Down:
			oppositeDirection = Up;
			break;
		case Left:
			oppositeDirection = Right;
			break;
		case Right:
			oppositeDirection = Left;
			break;
		default:
			oppositeDirection = None;
			break;
		}
		return oppositeDirection;
	}

	public static Direction convertNameToDirection(Name name) {
		Direction direction;
		switch (name) {
		case RoomExitUp:
		case PlayerExitDestinationUp:
			direction = Up;
			break;
		case RoomExitDown:
		case PlayerExitDestinationDown:
			direction = Down;
			break;
		case RoomExitLeft:
		case PlayerExitDestinationLeft:
			direction = Left;
			break;
		case RoomExitRight:
		case PlayerExitDestinationRight:
			direction = Right;
			break;
		default:
			direction = None;
		}
		return direction;
	}
	
	public int getValue() {
		return value;
	}
	
    public static Direction getByValue(int value) {
        for (Direction dir : values())
            if (dir.getValue() == value)
                return dir;
        return null;
    }
}
