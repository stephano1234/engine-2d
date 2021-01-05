package com.simple.game;

import com.simple.engine.Camera;
import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.GameRunner;
import com.simple.engine.Phase;
import com.simple.engine.Renderer;
import com.simple.engine.TileImageMap;

public class GameManager extends GameRunner {
		
//	private Font font = new Font("/fonts/standard-font.png");
	
	public GameManager() {
		super();
		this.addGameObject(new Player(3, 10));
		this.addGameObject(new GameObject("teste", 10, 28, TILE_SIZE * 2, TILE_SIZE, 0, 0, 0, 0) {
			
			private boolean isIn = false;
			
			@Override
			protected void setConfigs() {
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
		phase.addCollisionMap("/images/collision-test.png");
		this.addPhase(phase);
		this.setCurrentPhaseTag(phase.getTag());
		this.setCamera(new Camera("player", this));
//		this.setCamera(new Camera(50, 400));
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
	protected void defineFrame(Engine engine) {
		
		engine.getRenderer().setAmbientLightness(0xffffffff);
		
//		engine.getRenderer().setAmbientLightness(0xffffffff);
//		engine.getRenderer().setBackgroundColor(0xffffffff);		
//		int width = this.getCurrentPhase().getCollisionMap().getWidth();
//		int height = this.getCurrentPhase().getCollisionMap().getHeight();
//		for (int x = 0; x < width; x++) {
//			engine.getRenderer().drawMinThicknessLine(x * TILE_SIZE, 0, x * TILE_SIZE, height * TILE_SIZE, 0xff000000);
//		}		
//		for (int y = 0; y < height; y++) {
//			engine.getRenderer().drawMinThicknessLine(0, y * TILE_SIZE, width * TILE_SIZE, y * TILE_SIZE, 0xff000000);
//		}
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				if (this.getCurrentPhase().getCollisionMap().isEventTile(x, y)) {
//					engine.getRenderer().drawFilledRect(TILE_SIZE, TILE_SIZE, 0xff000000, x * TILE_SIZE, y * TILE_SIZE);
//					engine.getRenderer().drawText(this.font, "x:" + x, x * TILE_SIZE + 1, y * TILE_SIZE + 1, 0xffffffff);
//					engine.getRenderer().drawText(this.font, "y:" + y, x * TILE_SIZE + 1, y * TILE_SIZE + 9, 0xffffffff);
//				} else {					
//					engine.getRenderer().drawText(this.font, "x:" + x, x * TILE_SIZE + 1, y * TILE_SIZE + 1, 0xff000000);
//					engine.getRenderer().drawText(this.font, "y:" + y, x * TILE_SIZE + 1, y * TILE_SIZE + 9, 0xff000000);
//				}
//			}
//		}
		
		engine.getRenderer().setCameraPosition(this.getCamera());
	
	}

}
