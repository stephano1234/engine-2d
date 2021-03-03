package com.simple.game;

import com.simple.engine.Engine;
import com.simple.engine.GameRunner;
import com.simple.engine.Input;
import com.simple.engine.Renderer;
import com.simple.engine.Scenery;

public class GameManager extends GameRunner {
		
	public GameManager() {

		super();
		
		this.addGameObject(new TestGira("gira-gira", 32 * 3, 64 * 3, 300, 390));

		this.addGameObject(new TestObj(200, 200));
		
		Scenery scenery = new Scenery("fase teste");
		
		scenery.setBackgroundImage("/images/landscape-test.png");
		
		this.addScenery(scenery);
		
		this.setCurrentSceneryTag(scenery.getTag());
		
		this.setGameObjectFixedCamera("triTeste");
	
	}

	@Override
	protected void setWindowConfig(Engine engine) {
		engine.setWidth(640);
		engine.setHeight(480);
		engine.setScale(1f);
		engine.setTitle("My Game");
	}

	@Override
	protected void defineGeneralEvents(Input input) {
		// implement logic
	}

	@Override
	protected void defineFrame(Renderer renderer) {
		renderer.setAmbientLightness(0xffffffff);
	}

}
