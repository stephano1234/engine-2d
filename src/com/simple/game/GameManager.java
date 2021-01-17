package com.simple.game;

import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.GameRunner;
import com.simple.engine.Image;
import com.simple.engine.Phase;
import com.simple.engine.Renderer;

public class GameManager extends GameRunner {
	
	public GameManager() {
		super();
		this.addGameObject(new GameObject("teste", 50, 200) {
						
			private boolean isIn = false;
			
			private int dirCount = 0;
				
			@Override
			protected void setConfigs() {
				this.width = TILE_SIZE * 2;
				this.height = TILE_SIZE;
				this.isUnderGravityEffect = false;
				this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 4f;
				this.addAxisAlignedBoundingBox();
			}
			
			@Override
			public void applyAxisAlignedBoundingBoxCollisionEvent(GameObject other) {
				this.isIn = true;
			}
			
			@Override
			public void updateAutomaticOffsetsChanges() {
				if (this.dirCount < 250) {					
					this.offsetY += this.verticalVelocity;
					this.dirCount++;
				} else if (this.dirCount >= 250 && this.dirCount < 499) {
					this.offsetY -= this.verticalVelocity;
					this.dirCount++;
				} else {
					this.offsetY -= this.verticalVelocity;
					this.dirCount = 0;
				}
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
		this.addGameObject(new GameObject("teste2", 100, 448) {
			
			private boolean isIn = false;
			
			private int dirCount = 0;
				
			@Override
			protected void setConfigs() {
				this.width = TILE_SIZE * 2;
				this.height = TILE_SIZE;
				this.isUnderGravityEffect = false;
				this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 4f;
				this.addAxisAlignedBoundingBox();
			}
			
			@Override
			public void applyAxisAlignedBoundingBoxCollisionEvent(GameObject other) {
				if (!other.getTag().equals("floor")) {					
					this.isIn = true;
				}
			}
			
			@Override
			public void updateAutomaticOffsetsChanges() {
				if (this.dirCount < 150) {					
					this.offsetX += this.horizontalVelocity;
					this.dirCount++;
				} else if (this.dirCount >= 150 && this.dirCount < 299) {
					this.offsetX -= this.horizontalVelocity;
					this.dirCount++;
				} else {
					this.offsetX -= this.horizontalVelocity;
					this.dirCount = 0;
				}
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
		this.addGameObject(new GameObject("floor", 0, 464) {
			
			@Override
			protected void setConfigs() {
				this.width = TILE_SIZE * 40;
				this.height = TILE_SIZE;
				this.isUnderGravityEffect = false;
				this.paddingTop = 2;
				this.addAxisAlignedBoundingBox();
			}
			
			@Override
			public void renderObject(Renderer renderer) {
				Image image = new Image("/images/earth-grass-collision-tile.png");
				for (int i = 0; i < 40; i++) {					
					renderer.drawImage(image, i * TILE_SIZE, 464);
				}
			}

		});
		this.addGameObject(new Player(50, 10));
		Phase phase = new Phase("fase teste");
		phase.setLandscape("/images/landscape-test.png");
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
