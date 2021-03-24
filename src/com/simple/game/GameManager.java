package com.simple.game;

import com.simple.engine.Engine;
import com.simple.engine.GameRunner;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class GameManager extends GameRunner {
		
	private int scale;
	
	public GameManager() {
		super();
		this.scale = 3;
		this.addGameObject(new TestRectangle("east-wall", 16 * this.scale, 240 * this.scale, 312 * this.scale, 120 * this.scale, 0xff0000ff, false));
		this.addGameObject(new TestRectangle("west-wall", 16 * this.scale, 240 * this.scale, 8 * this.scale, 120 * this.scale, 0xff0000ff, false));
		this.addGameObject(new TestRectangle("north-wall", 288 * this.scale, 16 * this.scale, 160 * this.scale, 8 * this.scale, 0xff0000ff, false));
		this.addGameObject(new TestRectangle("south-wall", 288 * this.scale, 16 * this.scale, 160 * this.scale, 232 * this.scale, 0xff0000ff, false));
		this.addGameObject(new TestCircle("testCircle", 16 * this.scale, -100 * this.scale, -100 * this.scale, 0xff00ff00, true));
		this.setGameObjectFixedCamera("testCircle");

//		this.addGameObject(new Room());
//		this.addGameObject(new Character(100, 80));
//		this.setGameObjectFixedCamera("character");			
	}

	@Override
	protected void setWindowConfig(Engine engine) {
		engine.setWidth(320 * this.scale);
		engine.setHeight(240 * this.scale);
		engine.setScale(1f);
		engine.setTitle("My Game");
	}

	@Override
	protected void defineGeneralEvents(Input input) {
		// implement logic
	}

	@Override
	protected void defineFrame(Renderer renderer) {
		renderer.setBackgroundColor(0xff5555ff);
		renderer.setAmbientLightness(0xffffffff);
	}

}
