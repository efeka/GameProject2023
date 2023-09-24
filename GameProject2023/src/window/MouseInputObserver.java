package window;

import java.awt.event.MouseEvent;

public interface MouseInputObserver {
	void onMouseClick(MouseEvent e);
	void onMousePress(MouseEvent e);
}
