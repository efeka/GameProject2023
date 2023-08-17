package window;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferStrategy;

import framework.ObjectHandler;

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
		
		// Load the first level
		objectHandler = new ObjectHandler();
		objectHandler.setupGame(keyInput);

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
				System.out.println("FPS: " + frames + " | UPS: " + updates);
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
