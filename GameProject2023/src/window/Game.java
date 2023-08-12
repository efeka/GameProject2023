package window;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferStrategy;

import framework.GameConstants;
import framework.ObjectHandler;
import framework.ObjectId;
import game_objects.DiagonalStoneTileBlock;
import game_objects.Player;
import game_objects.StoneTileBlock;
import general_object_types.TileOrientation;

@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable {

	private Thread thread;
	public static boolean running = false;
	
	private Window window;
	
	private ObjectHandler objectHandler;
	
	private final int MAX_FPS = 120;
	private final int MAX_UPS = 120;
	
	/**
	 * The Game class is responsible for setting up and running the game, serving as the entry point of the application.
	 */
	public Game() {
		KeyInput keyInput = new KeyInput();
		addKeyListener(keyInput);
		window = new GameWindow(this);
		
		objectHandler = new ObjectHandler();
		
		// TODO temporary, for debug
		int x = 200;
		int y = 600;
		int blocks = 20;
		int blockSize = GameConstants.ScaleConstants.TILE_SIZE;
		objectHandler.addObject(new StoneTileBlock(x, y,
				ObjectId.Name.StoneTileBlock, TileOrientation.OuterTopLeft),
				ObjectHandler.MIDDLE_LAYER);
		for (int i = 1; i < blocks; i++) {
			TileOrientation orientation = i != blocks - 1 ? TileOrientation.OuterTop : TileOrientation.OuterTopRight;
			objectHandler.addObject(new StoneTileBlock(x + i * blockSize, y,
					ObjectId.Name.StoneTileBlock, orientation),
					ObjectHandler.MIDDLE_LAYER);
		}
		objectHandler.addObject(new Player(x, y - 80, objectHandler, keyInput), ObjectHandler.MIDDLE_LAYER);
		
		objectHandler.addObject(new DiagonalStoneTileBlock(x + blockSize * 10, y - blockSize,
				ObjectId.Name.DiagonalStoneBlock, TileOrientation.OuterTopLeft),
				ObjectHandler.MIDDLE_LAYER);
		objectHandler.addObject(new DiagonalStoneTileBlock(x + blockSize * 11, y - blockSize * 2,
				ObjectId.Name.DiagonalStoneBlock, TileOrientation.OuterTopLeft),
				ObjectHandler.MIDDLE_LAYER);
		objectHandler.addObject(new DiagonalStoneTileBlock(x + blockSize * 12, y - blockSize * 3,
				ObjectId.Name.DiagonalStoneBlock, TileOrientation.OuterTopLeft),
				ObjectHandler.MIDDLE_LAYER);
		objectHandler.addObject(new StoneTileBlock(x + blockSize * 13, y - blockSize * 3,
				ObjectId.Name.StoneTileBlock, TileOrientation.OuterTop),
				ObjectHandler.MIDDLE_LAYER);
		objectHandler.addObject(new StoneTileBlock(x + blockSize * 14, y - blockSize * 3,
				ObjectId.Name.StoneTileBlock, TileOrientation.OuterTop),
				ObjectHandler.MIDDLE_LAYER);
		objectHandler.addObject(new DiagonalStoneTileBlock(x + blockSize * 17, y - blockSize,
				ObjectId.Name.DiagonalStoneBlock, TileOrientation.OuterTopRight),
				ObjectHandler.MIDDLE_LAYER);
		objectHandler.addObject(new DiagonalStoneTileBlock(x + blockSize * 16, y - blockSize * 2,
				ObjectId.Name.DiagonalStoneBlock, TileOrientation.OuterTopRight),
				ObjectHandler.MIDDLE_LAYER);
		objectHandler.addObject(new DiagonalStoneTileBlock(x + blockSize * 15, y - blockSize * 3,
				ObjectId.Name.DiagonalStoneBlock, TileOrientation.OuterTopRight),
				ObjectHandler.MIDDLE_LAYER);
		
		requestFocus();
		start();
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// The game loop which handles updating and rendering the entire game,
	// while keeping track of and limiting the updates and frames per second.
	@Override
	public void run() {
		final double timePerFrame = 1_000_000_000.0 / MAX_FPS;
		final double timePerUpdate = 1_000_000_000.0 / MAX_UPS;

		int frames = 0;
		int updates = 0;
		double deltaUpdates = 0;
		double deltaFrames = 0;		
		
		long lastCheck = 0;
		long previousTime = System.nanoTime();				
		
		while (true) {
			long currentTime = System.nanoTime();
			
			deltaUpdates += (currentTime - previousTime) / timePerUpdate;
			deltaFrames += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;
			
			// Update the game
			if (deltaUpdates >= 1) {
				update();
				updates++;
				deltaUpdates--;
			}
			
			// Render the game
			if (deltaFrames >= 1) {
				render();
				frames++;
				deltaFrames--;
			}
			
			if (System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames + " | UPS:" + updates);
				frames = updates = 0;
			}
		}
	}
	
	private void update() {
		objectHandler.updateObjects();
	}
	
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();

		// Draw background
		g.setColor(new Color(25, 51, 45));
		g.fillRect(0, 0, window.getWidth(), window.getHeight());
		
		// Render game objects
		objectHandler.renderObjects(g);

		g.dispose();
		bs.show();
	}
	
}
