package com.simple.game;

import com.simple.engine.Coordinate;
import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.GameRunner;
import com.simple.engine.Image;
import com.simple.engine.Input;
import com.simple.engine.Phase;
import com.simple.engine.Renderer;

public class GameManager extends GameRunner {
	
	public GameManager() {

		super();
		
		this.addGameObject(new GameObject("gira-gira", 300, 390) {
			
			private boolean isIn = false;
			
			private int dirCount = 0;
				
			@Override
			protected void setConfigs() {
				this.width = 32;
				this.height = 64;
				this.isUnderGravityEffect = false;
			}
			
			@Override
			public void applyCollisionEvent(GameObject other) {
				this.isIn = true;
			}
			
			@Override
			public void updateAutomaticOffsetsChanges() {
				if (this.dirCount < 628) {
					this.dirCount++;
				} else {
					this.dirCount = 0;
				}
				this.rotationAngle = this.dirCount / 100f;
			}
			
			@Override
			public void renderObject(Renderer renderer) {
				int color = 0xff000000;
				if (this.isIn) {
					this.isIn = false;
					color = 0xffff0000;
				}
				renderer.drawFilledRectWithRotation(this.width, this.height, color, this.position.getX(), this.position.getY(), this.rotationAngle);
			}

		});
		
		this.addGameObject(new GameObject("teste", 50, 200) {
						
			private boolean isIn = false;
			
			private int dirCount = 0;
				
			@Override
			protected void setConfigs() {
				this.width = TILE_SIZE * 2;
				this.height = TILE_SIZE;
				this.isUnderGravityEffect = false;
				this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 4f;
			}
			
			@Override
			public void applyCollisionEvent(GameObject other) {
				this.isIn = true;
			}
			
			@Override
			public void updateAutomaticOffsetsChanges() {
				if (this.dirCount < 250) {					
					this.moveDown();
					this.dirCount++;
				} else if (this.dirCount >= 250 && this.dirCount < 499) {
					this.moveUp();
					this.dirCount++;
				} else {
					this.moveUp();
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
				renderer.drawFilledRect(this.width, this.height, color, this.position.getX(), this.position.getY());
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
			}
			
			@Override
			public void applyCollisionEvent(GameObject other) {
				if (!other.getTag().equals("floor")) {					
					this.isIn = true;
				}
			}
			
			@Override
			public void updateAutomaticOffsetsChanges() {
				if (this.dirCount < 150) {					
					this.moveRight();
					this.dirCount++;
				} else if (this.dirCount >= 150 && this.dirCount < 299) {
					this.moveLeft();
					this.dirCount++;
				} else {
					this.moveLeft();
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
				renderer.drawFilledRect(this.width, this.height, color, this.position.getX(), this.position.getY());
			}

		});

		this.addGameObject(new GameObject("testeTrianguloParado", 400, 448) {
			
			@Override
			protected void setConfigs() {
				this.width = TILE_SIZE;
				this.height = TILE_SIZE;
				this.isUnderGravityEffect = false;
				this.addTriangularBoundingArea("a", new Coordinate(this.position.getX(), this.position.getY()), 
						new Coordinate(this.position.getX() + this.width, this.position.getY()), 
						new Coordinate(this.position.getX() + this.width, this.position.getY() + this.height)
						);
			}
			
			@Override
			public void renderObject(Renderer renderer) {
				renderer.drawFilledTriangle(this.width, this.height, 0xff000000, this.position.getX(), this.position.getY());
			}

		});

		this.addGameObject(new GameObject("testeRetanguloParado", 450, 448) {
			
			@Override
			protected void setConfigs() {
				this.width = TILE_SIZE;
				this.height = TILE_SIZE;
				this.isUnderGravityEffect = false;
				this.addRectangleBoundingArea("b", new Coordinate(this.position.getX(), this.position.getY()), 
						new Coordinate(this.position.getX() + this.width, this.position.getY()), 
						new Coordinate(this.position.getX() + this.width, this.position.getY() + this.height),
						new Coordinate(this.position.getX(), this.position.getY() + this.height)
						);
			}
			
			@Override
			public void renderObject(Renderer renderer) {
				renderer.drawFilledRect(this.width, this.height, 0xff000000, this.position.getX(), this.position.getY());
			}

		});

		this.addGameObject(new GameObject("floor", 0, 464) {
			
			@Override
			protected void setConfigs() {
				this.width = TILE_SIZE * 40;
				this.height = TILE_SIZE;
				this.isUnderGravityEffect = false;
				this.paddingTop = 2;
			}
			
			@Override
			public void renderObject(Renderer renderer) {
				Image image = new Image("/images/earth-grass-collision-tile.png");
				for (int i = 0; i < 40; i++) {					
					renderer.drawImage(image, i * TILE_SIZE, 464);
				}
			}

		});
		
//		this.addGameObject(new Player(50, 10));

		this.addGameObject(new TestTriangle(50, 10));
		
		Phase phase = new Phase("fase teste");
		
		phase.setLandscape("/images/landscape-test.png");
		
		this.addPhase(phase);
		
		this.setCurrentPhaseTag(phase.getTag());
		
		this.setGameObjectFixedCamera("triTeste");
	
	}

	@Override
	protected void setWindowConfig(Engine engine) {
		engine.setWidth(320);
		engine.setHeight(240);
		engine.setScale(2f);
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
