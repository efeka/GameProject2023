package window;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

	// TODO Load key configurations from a certain source instead of hard coding
	
	// Player movement keys
	private boolean moveUpKeyPressed = false;
	private boolean moveRightKeyPressed = false;
	private boolean moveLeftKeyPressed = false;
	private boolean jumpKeyPressed = false;
	private boolean crouchKeyPressed = false;
	private boolean dodgeKeyPressed = false;
	// Player ability keys
	private boolean firstAbilityKeyPressed = false;
	private boolean secondAbilityKeyPressed = false;
	// Menu navigation keys
	private boolean navigateUpKeyPressed = false;
	private boolean navigateDownKeyPressed = false;
	private boolean selectionKeyPressed = false;
	private boolean inventoryKeyToggled = true, inventoryToggleFlag = false;
	// Interaction keys
	private boolean interactKeyPressed = false;
	private boolean hotkey1Pressed = false;
	private boolean hotkey2Pressed = false;
	// Debug
	private boolean ctrlPressed = false;
	public boolean debugPressed = false;

	@Override
	public void keyPressed(KeyEvent e) {
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
		case KeyEvent.VK_F:
			interactKeyPressed = true;
			break;
		case KeyEvent.VK_SHIFT:
			dodgeKeyPressed = true;
			break;
		case KeyEvent.VK_TAB:
			if (inventoryKeyToggled && !inventoryToggleFlag)
				inventoryKeyToggled = false;
			else if (!inventoryKeyToggled && inventoryToggleFlag)
				inventoryKeyToggled = true;
			break;
		case KeyEvent.VK_1:
			hotkey1Pressed = true;
			break;
		case KeyEvent.VK_2:
			hotkey2Pressed = true;
			break;
			// Debug
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
		case KeyEvent.VK_CONTROL:
			ctrlPressed = true;
			break;
		case KeyEvent.VK_X:
			if (ctrlPressed) {
				debugPressed = !debugPressed;
				ctrlPressed = false;
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
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
		case KeyEvent.VK_F:
			interactKeyPressed = false;
			break;
		case KeyEvent.VK_SHIFT:
			dodgeKeyPressed = false;
			break;
		case KeyEvent.VK_TAB:
			inventoryToggleFlag = !inventoryToggleFlag;
			break;
		case KeyEvent.VK_1:
			hotkey1Pressed = false;
			break;
		case KeyEvent.VK_2:
			hotkey2Pressed = false;
			break;
			// Debug
		case KeyEvent.VK_CONTROL:
			ctrlPressed = false;
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

	public void setJumpKeyPressed(boolean isPressed) {
		jumpKeyPressed = isPressed;
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

	public boolean isInteractKeyPressed() {
		return interactKeyPressed;
	}

	public boolean isDodgeKeyPressed() {
		return dodgeKeyPressed;
	}

	public boolean isInventoryKeyToggled() {
		return inventoryKeyToggled;
	}
	
	public boolean isHotkey1Pressed() {
		return hotkey1Pressed;
	}
	
	public boolean isHotkey2Pressed() {
		return hotkey2Pressed;
	}

}
