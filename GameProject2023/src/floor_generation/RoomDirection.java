package floor_generation;

import framework.ObjectId.Name;

public enum RoomDirection {
	None(-1),
	Up(0),
	Down(1),
	Left(2),
	Right(3);

	private int value;
	
	RoomDirection(int value) {
		this.value = value;
	}
	
	public static RoomDirection getOppositeDirection(RoomDirection roomDirection) {
		RoomDirection oppositeDirection = null;
		switch (roomDirection) {
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

	public static RoomDirection convertNameToDirection(Name name) {
		RoomDirection roomDirection;
		switch (name) {
		case RoomExitUp:
		case PlayerExitDestinationUp:
			roomDirection = Up;
			break;
		case RoomExitDown:
		case PlayerExitDestinationDown:
			roomDirection = Down;
			break;
		case RoomExitLeft:
		case PlayerExitDestinationLeft:
			roomDirection = Left;
			break;
		case RoomExitRight:
		case PlayerExitDestinationRight:
			roomDirection = Right;
			break;
		default:
			roomDirection = None;
		}
		return roomDirection;
	}
	
	public int getValue() {
		return value;
	}
	
    public static RoomDirection getByValue(int value) {
        for (RoomDirection dir : values())
            if (dir.getValue() == value)
                return dir;
        return null;
    }
}
