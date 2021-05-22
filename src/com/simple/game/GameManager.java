package com.simple.game;

import com.simple.engine.Engine;
import com.simple.engine.GameRunner;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class GameManager extends GameRunner {
		
	public GameManager() {
		super();
		this.addGameObject(new Room());
		this.addGameObject(new Character(100, 80));
		this.setGameObjectFixedCamera("character");			
	}

	@Override
	protected void setWindowConfig(Engine engine) {
		engine.setWidth(320);
		engine.setHeight(240);
		engine.setScale(1f);
		engine.setTitle("My Game");
	}

	@Override
	protected void defineGeneralEvents(Input input) {
		// implement logic
	}

	@Override
	protected void defineFrame(Renderer renderer) {
		renderer.setBackgroundColor(0xff000000);
		renderer.setAmbientLightness(0xffffffff);
	}

}
