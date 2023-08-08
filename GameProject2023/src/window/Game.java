package window;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.SwingConstants;

import framework.ObjectHandler;
import framework.ObjectId;
import game_objects.StoneTileBlock;
import general_object_types.TileBlock;
import general_object_types.TileOrientation;

@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable {

	private Thread thread;
	public static boolean running = false;
	
	private GameWindow window;
	
	private ObjectHandler objectHandler;
	
	/**
	 * The Game class is responsible for setting up and running the game, serving as the entry point of the application.
	 */
	public Game() {
		requestFocus();
		window = new GameWindow(this);
		
		objectHandler = new ObjectHandler();
		// TODO temporary, for debug
		objectHandler.addObject(new StoneTileBlock(300, 300, 32, 32,
				ObjectId.Name.StoneTileBlock, TileOrientation.OuterTopLeft),
				ObjectHandler.MIDDLE_LAYER);
		for (int i = 1; i < 16; i++) {
			TileOrientation orientation = i != 15 ? TileOrientation.OuterTop : TileOrientation.OuterTopRight;
			objectHandler.addObject(new StoneTileBlock(300 + i * 32, 300, 32, 32,
					ObjectId.Name.StoneTileBlock, orientation),
					ObjectHandler.MIDDLE_LAYER);
		}

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
	
	// This is where the game loop is handled.
	// Objects are updated and rendered in here.
	// TODO Limit frame rate
	@Override
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1_000_000_000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				update();
				delta--;
			}
			render();
			
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
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
		// Graphics2D g2d = (Graphics2D) g;
		
		// Draw background
		g.setColor(new Color(25, 51, 45));
		g.fillRect(0, 0, window.getWidth(), window.getHeight());
		
		// g2d.translate(cam.getX(), cam.getY());
		
		objectHandler.renderObjects(g);
		
		// g2d.translate(cam.getX(), -cam.getY());

		g.dispose();
		bs.show();
	}
	
}
