package window;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class MouseInput implements MouseListener, MouseMotionListener {

	private List<MouseInputObserver> observers = new ArrayList<>();
	
	private boolean attackButtonPressed = false;
	private int x, y;
	
    public void registerObserver(MouseInputObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(MouseInputObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyClickObservers(MouseEvent e) {
        for (MouseInputObserver observer : observers)
            observer.onMouseClick(e);
    }
    
    private void notifyPressObservers(MouseEvent e) {
        for (MouseInputObserver observer : observers)
            observer.onMousePress(e);
    }
    
    private void notifyReleaseObservers(MouseEvent e) {
    	for (MouseInputObserver observer : observers)
    		observer.onMouseRelease(e);
    }

	@Override
	public void mousePressed(MouseEvent e) {
		notifyPressObservers(e);
		
		if (e.getButton() == MouseEvent.BUTTON1)
			attackButtonPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		notifyReleaseObservers(e);
		
		if (e.getButton() == MouseEvent.BUTTON1)
			attackButtonPressed = false;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		notifyClickObservers(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	public boolean isAttackButtonPressed() {
		return attackButtonPressed;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

}
