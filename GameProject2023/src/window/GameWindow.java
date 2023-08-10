package window;

import javax.swing.JFrame;

public class GameWindow extends JFrame {
	
	/**
	 * Creates the main application window.
	 *
	 * @param main reference to the Game class, used to set up the JFrame with a Canvas.
	 */
	public GameWindow(Game game, KeyInput keyInput) {
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Game Project 2023");
		
		add(game);
		addKeyListener(keyInput);
		
		setVisible(true);
	}

}
