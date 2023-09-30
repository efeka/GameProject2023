package level_generation;

public enum ExitLocation {
	Up,
	Down,
	Left,
	Right;
	
	public ExitLocation getOppositeExitLocation(ExitLocation exitLocation) {
		ExitLocation oppositeLocation = null;
		switch (exitLocation) {
		case Up:
			oppositeLocation = Down;
			break;
		case Down:
			oppositeLocation = Up;
			break;
		case Left:
			oppositeLocation = Right;
			break;
		case Right:
			oppositeLocation = Left;
			break;
		}
		return oppositeLocation;
	}
}
