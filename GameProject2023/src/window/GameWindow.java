package window;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import framework.GameConstants;
import framework.TextureLoader;
import framework.TextureLoader.TextureName;
import main.Game;

public class GameWindow extends JFrame implements MouseInputObserver {

	private static final long serialVersionUID = -7086381604555111950L;
	
	private Cursor[] cursors;
	
	/**
	 * Creates the main application window.
	 *
	 * @param main reference to the Game class, used to set up the JFrame with a Canvas.
	 */
	public GameWindow(Game game) {
		// setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
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

	@Override
	public void onMousePress(MouseEvent e) {
		if (cursors == null)
			setupCursors();
		setCursor(cursors[1]);
	}

	@Override
	public void onMouseRelease(MouseEvent e) {
		if (cursors == null)
			setupCursors();
		setCursor(cursors[0]);
	}
	
	@Override
	public void onMouseClick(MouseEvent e) {}
	
	private void setupCursors() {
		BufferedImage[] cursorImages = TextureLoader.getInstance().getTextures(TextureName.Cursor);
		cursors = new Cursor[2];
		cursors[0] = Toolkit.getDefaultToolkit().createCustomCursor(cursorImages[0], new Point(0, 0),"custom_cursor_name1");
		cursors[1] = Toolkit.getDefaultToolkit().createCustomCursor(cursorImages[1], new Point(0, 0),"custom_cursor_name2");
	}

}
