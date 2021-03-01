package com.simple.game;

import com.simple.engine.BoundingArea;
import com.simple.engine.CircleBoundingArea;
import com.simple.engine.Coordinate;
import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.GameRunner;
import com.simple.engine.Input;
import com.simple.engine.Light;
import com.simple.engine.Renderer;
import com.simple.engine.Scenery;

public class GameManager extends GameRunner {
	
	private Light light = new Light(500, 0xffffffff);
	
	public GameManager() {

		super();
		
		this.addGameObject(new GameObject("gira-gira", 32, 64, 300, 390) {
			
			private boolean isIn = false;
			
			private int dirCount = 0;

			@Override
			public void applyCollisionInteractionEvent(BoundingArea boudingArea) {
				this.isIn = true;
			}
			
			@Override
			public void processAutomaticOffsetsChanges() {
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
				renderer.drawRectAreaWithRotation(this.width, this.height, color, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, this.rotationAngle, false);
			}

		});
		
		this.addGameObject(new GameObject("teste", TILE_SIZE * 2, TILE_SIZE, 50, 200) {
						
			private boolean isIn = false;
			
			private int dirCount = 0;
						
			private float verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 4f;

			@Override
			public void applyCollisionInteractionEvent(BoundingArea boudingArea) {
				this.isIn = true;
			}
			
			@Override
			public void processAutomaticOffsetsChanges() {
				if (this.dirCount < 250) {					
					this.moveDown(this.verticalVelocity);
					this.dirCount++;
				} else if (this.dirCount >= 250 && this.dirCount < 499) {
					this.moveUp(this.verticalVelocity);
					this.dirCount++;
				} else {
					this.moveUp(this.verticalVelocity);
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
				this.renderRectArea(renderer, color, false);
			}

		});
	
		this.addGameObject(new GameObject("teste2", TILE_SIZE * 2, TILE_SIZE, 100, 448) {
			
			private boolean isIn = false;
			
			private int dirCount = 0;
				
			private float horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;
			
			@Override
			public void applyCollisionInteractionEvent(BoundingArea boudingArea) {
				if (!boudingArea.getGameObject().getTag().equals("floor")) {					
					this.isIn = true;
				}
			}
			
			@Override
			public void processAutomaticOffsetsChanges() {
				if (this.dirCount < 150) {					
					this.moveRight(this.horizontalVelocity);
					this.dirCount++;
				} else if (this.dirCount >= 150 && this.dirCount < 299) {
					this.moveLeft(this.horizontalVelocity);
					this.dirCount++;
				} else {
					this.moveLeft(this.horizontalVelocity);
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
				this.renderRectArea(renderer, color, false);
			}

		});

		this.addGameObject(new GameObject("testeTrianguloParado", TILE_SIZE, TILE_SIZE, 400, 448) {
			
			@Override
			protected void setConfigs() {
				this.addTriangularBoundingArea("a", 
					new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
					new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight),
					new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight)
				);
				this.priorityCollisionOrder = 1;
			}
			
			@Override
			public void renderObject(Renderer renderer) {
				renderer.drawTriangleArea(this.width, this.height, 0xff000000, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, false);
			}

		});

		this.addGameObject(new GameObject("testeRetanguloParado", TILE_SIZE, TILE_SIZE, 450, 448) {
			
			@Override
			protected void setConfigs() {
				this.addRectangleBoundingArea("a", 
					new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
					new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
					new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight),
					new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight)
				);
				this.priorityCollisionOrder = 2;
			}
			
			@Override
			public void renderObject(Renderer renderer) {
				this.renderRectArea(renderer, 0xff000000, false);
			}

		});

		this.addGameObject(new GameObject("testeCirculoParado", TILE_SIZE * 3, TILE_SIZE * 3, 450, 148) {
			
			@Override
			protected void setConfigs() {
				this.boundingAreas.add(new CircleBoundingArea("a", this, this.position, this.halfHeight));
				this.priorityCollisionOrder = 3;
			}
			
			@Override
			public void renderObject(Renderer renderer) {
				renderer.drawCircleArea(this.halfWidth, 0xff00ff00, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, false);
			}

		});

//		this.addGameObject(new Player(50, 10));

		this.addGameObject(new TestTriangle(100, 100));
		
		Scenery scenery = new Scenery("fase teste");
		
		scenery.setBackgroundImage("/images/landscape-test.png");
		
		this.addScenery(scenery);
		
		this.setCurrentSceneryTag(scenery.getTag());
		
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
//		renderer.setBackgroundColor(0xffffffff);
		renderer.setAmbientLightness(0xffffffff);
//		renderer.drawLight(this.light, 320, 240);
	}

}
