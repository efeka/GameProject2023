package window;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

	// Player movement keys
	private boolean moveUpKeyPressed = false;
	private boolean moveRightKeyPressed = false;
	private boolean moveLeftKeyPressed = false;
	private boolean jumpKeyPressed = false;
	private boolean crouchKeyPressed = false;
	// Player ability keys
	private boolean firstAbilityKeyPressed = false;
	private boolean secondAbilityKeyPressed = false;
	// Menu navigation keys
	private boolean navigateUpKeyPressed = false;
	private boolean navigateDownKeyPressed = false;
	private boolean selectionKeyPressed = false;
	
	// Debug
	private boolean ctrlPressed = false;
	public boolean debugPressed = false;
	public boolean[] numberKeyPressed = new boolean[5]; 
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Load key configurations from a certain source instead of hard coding
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			moveUpKeyPressed = true;
			navigateUpKeyPressed = true;
			break;
		case KeyEvent.VK_D:
			moveRightKeyPressed = true;
			break;
		case KeyEvent.VK_A:
			moveLeftKeyPressed = true;
			break;
		case KeyEvent.VK_SPACE:
			jumpKeyPressed = true;
			break;
		case KeyEvent.VK_S:
			crouchKeyPressed = true;
			navigateDownKeyPressed = true;
			break;
		case KeyEvent.VK_ENTER:
			selectionKeyPressed = true;
			break;
		case KeyEvent.VK_Q:
			firstAbilityKeyPressed = true;
			break;
		case KeyEvent.VK_E:
			secondAbilityKeyPressed = true;
			break;
		// Debug
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
		case KeyEvent.VK_CONTROL:
			ctrlPressed = true;
		case KeyEvent.VK_X:
			if (ctrlPressed) {
				debugPressed = !debugPressed;
				ctrlPressed = false;
			}
			break;
		case KeyEvent.VK_1:
			numberKeyPressed[0] = true;
			break;
		case KeyEvent.VK_2:
			numberKeyPressed[1] = true;
			break;
		case KeyEvent.VK_3:
			numberKeyPressed[2] = true;
			break;
		case KeyEvent.VK_4:
			numberKeyPressed[3] = true;
			break;
		case KeyEvent.VK_5:
			numberKeyPressed[4] = true;
			break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Load key configurations from a certain source instead of hard coding
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			moveUpKeyPressed = false;
			navigateUpKeyPressed = false;
			break;
		case KeyEvent.VK_D:
			moveRightKeyPressed = false;
			break;
		case KeyEvent.VK_A:
			moveLeftKeyPressed = false;
			break;
		case KeyEvent.VK_SPACE:
			jumpKeyPressed = false;
			break;
		case KeyEvent.VK_S:
			crouchKeyPressed = false;
			navigateDownKeyPressed = false;
			break;
		case KeyEvent.VK_ENTER:
			selectionKeyPressed = false;
			break;
		case KeyEvent.VK_Q:
			firstAbilityKeyPressed = false;
			break;
		case KeyEvent.VK_E:
			secondAbilityKeyPressed = false;
			break;
		// Debug
		case KeyEvent.VK_CONTROL:
			ctrlPressed = false;
		case KeyEvent.VK_1:
			numberKeyPressed[0] = false;
			break;
		case KeyEvent.VK_2:
			numberKeyPressed[1] = false;
			break;
		case KeyEvent.VK_3:
			numberKeyPressed[2] = false;
			break;
		case KeyEvent.VK_4:
			numberKeyPressed[3] = false;
			break;
		case KeyEvent.VK_5:
			numberKeyPressed[4] = false;
			break;
		}
	}
	
	public boolean isMoveUpKeyPressed() {
		return moveUpKeyPressed;
	}
	
	public boolean isMoveRightKeyPressed() {
		return moveRightKeyPressed;
	}
	
	public boolean isMoveLeftKeyPressed() {
		return moveLeftKeyPressed;
	}
	
	public boolean isJumpKeyPressed() {
		return jumpKeyPressed;
	}
	
	public boolean isCrouchKeyPressed() {
		return crouchKeyPressed;
	}
	
	public boolean isNavigateUpKeyPressed() {
		return navigateUpKeyPressed;
	}
	
	public boolean isNavigateDownKeyPressed() {
		return navigateDownKeyPressed;
	}
	
	public boolean isSelectionKeyPressed() {
		return selectionKeyPressed;
	}
	
	public boolean isFirstAbilityKeyPressed() {
		return firstAbilityKeyPressed;
	}
	
	public boolean isSecondAbilityKeyPressed() {
		return secondAbilityKeyPressed;
	}
}
