package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import framework.GameConstants;
import framework.ObjectHandler;
import window.GameWindow;
import window.KeyInput;
import window.MouseInput;

@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable {

	private Thread thread;
	public static boolean running = false;
	
	private GameWindow window;
	private KeyInput keyInput;
	private ObjectHandler objectHandler;
	
	private final int MAX_FPS = 120;
	private final int MAX_UPS = 120;
	private int displayedFPS, displayedUPS;
	
	/**
	 * The Game class is responsible for setting up and running the game, serving as the entry point of the application.
	 */
	public Game() {
		keyInput = new KeyInput();
		MouseInput mouseInput = new MouseInput();
		addKeyListener(keyInput);
		addMouseListener(mouseInput);
		addMouseMotionListener(mouseInput);
		
		window = new GameWindow(this);

		// Load the first level
		objectHandler = new ObjectHandler(keyInput, mouseInput);
		objectHandler.setupGame(10);

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
				displayedFPS = frames;
				displayedUPS = updates;
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
		g.setColor(new Color(41, 41, 41));
		g.fillRect(0, 0, window.getWidth(), window.getHeight());
		
		if (shakeCamera && shakeDuration-- < 0)
			shakeCamera = false;
		if (shakeCamera) {
			g.translate(-oldTranslateX, -oldTranslateY);
			int randomX = (int) (Math.random() * shakeMagnitude - shakeMagnitude / 2);
			int randomY = (int) (Math.random() * shakeMagnitude - shakeMagnitude / 2);
			g.translate(randomX, randomY);
			oldTranslateX = randomX;
			oldTranslateY = randomY;
		}
		
		// Render game objects
		objectHandler.renderObjects(g);
		
		// Display FPS and UPS
		if (keyInput.debugPressed) {
			g.setColor(Color.WHITE);
			g.setFont(GameConstants.FontConstants.DAMAGE_FONT);
			g.drawString("FPS: " + displayedFPS, GameConstants.ScaleConstants.GAME_WIDTH - 77, 15);
			g.drawString("UPS: " + displayedUPS, GameConstants.ScaleConstants.GAME_WIDTH - 80, 35);
		}
		
		g.dispose();
		bs.show();
	}
	
	// TODO temporary, ideally should be moved into a camera class
	private static int oldTranslateX = 0, oldTranslateY = 0;
	private static boolean shakeCamera = false;
	private static int shakeMagnitude;
	private static int shakeDuration;
	public static void shakeCamera(int duration, int magnitude) {
		shakeDuration = duration;
		shakeCamera = true;
		shakeMagnitude = magnitude;
	}
	
}
