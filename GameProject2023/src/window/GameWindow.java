package window;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import framework.GameConstants;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import main.Game;

public class GameWindow extends JFrame {

	private static final long serialVersionUID = -7086381604555111950L;
	
	/**
	 * Creates the main application window.
	 *
	 * @param main reference to the Game class, used to set up the JFrame with a Canvas.
	 */
	public GameWindow(Game game) {
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		//setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(GameConstants.ScaleConstants.GAME_WIDTH, GameConstants.ScaleConstants.GAME_HEIGHT);
		setTitle("Game Project 2023");
		
		BufferedImage cursorImage = TextureLoader.getInstance().getTextures(TextureName.Cursor)[0];
		Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0),"custom_cursor_name");
		setCursor(cursor);

		add(game);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
