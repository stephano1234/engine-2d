package com.simple.engine;

public class Engine implements Runnable {

	private boolean running = true;
	
	private GameRunner gameRunner;
	
	private Window window;
	private Renderer renderer;
	private Input input;
	
	public static final float UPDATE_CAP = 1f/60f;

	// window default configurations
	private int width = 320;
	private int height = 240;
	private float scale = 1f;
	private String title = "engine-2d";

	// font variable for FPS rendering
	private Font font = new Font("/fonts/standard-font.png");
	
	// font color code for FPS rendering
	private static final int FPS_COLOR = 0xff00ffff;
	
	public void start() {
		// run method of this object will be executed in a separate thread
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		// set window configurations
		this.gameRunner.setWindowConfig(this);

		// set this engine object to the main game application objects
		this.window = new Window(this);
		this.renderer = new Renderer(this);
		this.input = new Input(this);
		
		// calculate FPS
		double frameTime = 0;
		int frames = 0;
		int fps = 0;
		
		boolean render;
		
		double firstTime = 0;
		double lastTime = System.nanoTime() / 1000000000.0;
		double passedTime = 0;
		double unprocessedTime = 0;
		
		while (this.running) {
			
			render = false;
			
			firstTime = System.nanoTime() / 1000000000.0;
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			unprocessedTime += passedTime;
			
			// calculate FPS
			frameTime += passedTime;
			
			while (unprocessedTime >= UPDATE_CAP) {
				
				unprocessedTime -= UPDATE_CAP;
				
				render = true;
				
				// update the game logical state
				this.gameRunner.loopLogicalStep(this);
				
				// feed last game controls state
				this.input.update();
				
				// calculate FPS
				if (frameTime >= 1.0) {
					frameTime = 0;
					fps = frames;
					frames = 0;
				}
				
			}
			
			if (render) {
				
				// clear frame for render the new image
				this.renderer.clear();
	
				// draw elements on the frame and define its pixels lightness and darkness
				this.gameRunner.renderFrame(this.renderer);

				// process lightness and darkness of the pixels
				this.renderer.enlight();
				
				// render FPS
				this.renderer.drawFPS(font, fps, 0, 0, FPS_COLOR);
				
				// update screen with new rendered pixels
				this.window.update();

				// calculate FPS
				frames++;
				
			} else {

				// stops this thread for one millisecond, 
				// if it doesn't have to render anything on loop iteration
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				
			}
			
		}
		
		this.dispose();
		
	}

	private void dispose() {
		// dispose
	}

	public Window getWindow() {
		return window;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public Input getInput() {
		return input;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public GameRunner getGameRunner() {
		return gameRunner;
	}

	public void setGameRunner(GameRunner gameRunner) {
		this.gameRunner = gameRunner;
	}

}
