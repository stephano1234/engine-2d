package com.simple.game;

import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.GameRunner;
import com.simple.engine.Phase;
import com.simple.engine.Renderer;
import com.simple.engine.TileImageMap;

public class GameManager extends GameRunner {
	
	public GameManager() {
		super();
		this.addGameObject(new Player(50, 10));
		this.addGameObject(new GameObject("teste", 50, 200) {
			
			private boolean isIn = false;
			
			@Override
			protected void setConfigs() {
				this.width = TILE_SIZE * 2;
				this.height = TILE_SIZE;
				this.addAxisAlignedBoundingBox();
			}
			
			@Override
			public void applyAxisAlignedBoundingBoxEvent(GameObject other) {
				this.isIn = true;
			}

			@Override
			public void renderObject(Renderer renderer) {
				int color = 0xff000000;
				if (this.isIn) {
					this.isIn = false;
					color = 0xffff0000;
				}
				renderer.drawFilledRect(this.width, this.height, color, this.positionX, this.positionY);
			}

		});
		Phase phase = new Phase("fase teste");
		phase.setLandscape("/images/landscape-test.png");
		TileImageMap floor = new TileImageMap("/images/earth-grass-image-map.png", "/images/earth-grass-collision-tile.png", "floor");
		phase.addTileImageMap(floor);
		this.addPhase(phase);
		this.setCurrentPhaseTag(phase.getTag());
		this.setGameObjectFixedCamera("player");
	}

	@Override
	protected void setWindowConfig(Engine engine) {
		engine.setWidth(320);
		engine.setHeight(240);
		engine.setScale(2f);
		engine.setTitle("My Game");
	}

	@Override
	protected void defineLogicalStep(Engine engine) {
		// implement logic
	}

	@Override
	protected void defineFrame(Renderer renderer) {		
		renderer.setAmbientLightness(0xffffffff);
	}

}
