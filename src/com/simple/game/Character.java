package com.simple.game;

import static com.simple.engine.GameRunner.TILE_SIZE;

import java.awt.event.KeyEvent;

import com.simple.engine.Coordinate;
import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class Character extends GameObject {
	
//	private int color = 0xffff0000;
	
	public Character(int positionX, int positionY) {
		super("character", TILE_SIZE, TILE_SIZE, "/images/character.png", TILE_SIZE, TILE_SIZE, positionX, positionY);
		this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 4.0;
		this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 4.0;
		this.changeTileTime = 5;
		this.addCollidingRectangleBoundingArea(this.tag,
				new Coordinate(positionX - 6.5, positionY - 7.5),
				new Coordinate(positionX + 6.5, positionY - 7.5),
				new Coordinate(positionX + 6.5, positionY + 7.5),
				new Coordinate(positionX - 6.5, positionY + 7.5)
			)
		;
	}
	
	@Override
	public void processControlBasedOffsetsChanges(Input input) {

		if (input.isKey(KeyEvent.VK_A)) {
			this.spinAntiClock(0.01);
		}
		
//		if (input.isKey(KeyEvent.VK_A)) {
//			this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 8.0;
//			this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 8.0;
//		}
//
//		if (input.isKeyUp(KeyEvent.VK_A)) {
//			this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 4.0;
//			this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 4.0;
//		}

		if (input.isKey(KeyEvent.VK_DOWN)) {
			this.moveDown();
		}

		if (input.isKey(KeyEvent.VK_UP)) {
			this.moveUp();
		}

		if (input.isKey(KeyEvent.VK_LEFT)) {
			this.moveLeft();
		}

		if (input.isKey(KeyEvent.VK_RIGHT)) {
			this.moveRight();
		}
						
	}

	@Override
	public void processImageAnimations(Input input) {
		
		if 
		(
			input.isKey(KeyEvent.VK_DOWN)
			||
			input.isKey(KeyEvent.VK_UP)
			||
			input.isKey(KeyEvent.VK_LEFT)
			||
			input.isKey(KeyEvent.VK_RIGHT)
		) 
		{
			this.tileTimeCounter++;
		}
		
		if (input.isKey(KeyEvent.VK_DOWN)) {
			this.tileY = 0;
			if (this.tileTimeCounter >= this.changeTileTime) {
				this.tileX = ++this.tileX % 3;
				this.tileTimeCounter = 0;
			}
		}

		if (input.isKeyUp(KeyEvent.VK_DOWN)) {
			this.tileY = 0;	
			this.tileX = 0;
			this.tileTimeCounter = 0;
		}

		if (input.isKey(KeyEvent.VK_UP)) {
			this.tileY = 3;
			if (this.tileTimeCounter >= this.changeTileTime) {
				this.tileX = ++this.tileX % 3;
				this.tileTimeCounter = 0;
			}			
		}

		if (input.isKeyUp(KeyEvent.VK_UP)) {
			this.tileY = 3;	
			this.tileX = 0;
			this.tileTimeCounter = 0;
		}

		if (input.isKey(KeyEvent.VK_LEFT)) {
			this.tileY = 2;
			if (this.tileTimeCounter >= this.changeTileTime) {
				this.tileX = ++this.tileX % 4;
				this.tileTimeCounter = 0;
			}			
		}

		if (input.isKeyUp(KeyEvent.VK_LEFT)) {
			this.tileY = 2;
			this.tileX = 0;
			this.tileTimeCounter = 0;
		}

		if (input.isKey(KeyEvent.VK_RIGHT)) {
			this.tileY = 1;
			if (this.tileTimeCounter >= this.changeTileTime) {
				this.tileX = ++this.tileX % 4;
				this.tileTimeCounter = 0;
			}
		}		

		if (input.isKeyUp(KeyEvent.VK_RIGHT)) {
			this.tileY = 1;	
			this.tileX = 0;
			this.tileTimeCounter = 0;
		}

	}
	
//	@Override
//	public void applyCollisionInteractionEvent(BoundingArea boundingArea) {
//		this.color = 0xff00ff00;
//	}
	
	@Override
	public void renderObject(Renderer renderer) {
		this.renderAnimatedImage(renderer, false);
//		this.showBoundingAreasBorders(renderer, this.color);
//		this.color = 0xffff0000;
//		this.showBoundingAreasBorders(renderer, 0xffff0000);
	}

}
