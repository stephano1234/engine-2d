package com.simple.game;

import static com.simple.engine.GameRunner.TILE_SIZE;

import java.awt.event.KeyEvent;

import com.simple.engine.BoundingArea;
import com.simple.engine.CircleBoundingArea;
import com.simple.engine.Coordinate;
import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class TestObj extends GameObject {

	private float horizontalVelocity = 0f;
	
	private float verticalVelocity = 0f;
	
	public TestObj(int positionX, int positionY) {
		super("triTeste", TILE_SIZE, TILE_SIZE, positionX, positionY);
		this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;
		this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;

//		this.addTriangularBoundingArea("a", 
//			new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
//			new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight),
//			new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight)
//		);
		
//		this.addRectangleBoundingArea("a", 
//			new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
//			new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
//			new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight),
//			new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight)
//		);
		
		this.boundingAreas.add(new CircleBoundingArea("a", this, this.position, this.halfHeight));
		
		this.priorityCollisionOrder = 0;
		this.addIgnoredCollisionInteractionGameObjectTag("testeRetanguloParado");
	}

	@Override
	public void applyCollisionInteractionEvent(BoundingArea boudingArea) {

	}
	
	@Override
	public void processControlBasedOffsetsChanges(Input input) {
		
		if (input.isKey(KeyEvent.VK_DOWN)) {
			this.moveDown(this.verticalVelocity);
		}

		if (input.isKey(KeyEvent.VK_UP)) {
			this.moveUp(this.verticalVelocity);
		}

		if (input.isKey(KeyEvent.VK_LEFT)) {
			this.moveLeft(this.horizontalVelocity);
		}

		if (input.isKey(KeyEvent.VK_RIGHT)) {
			this.moveRight(this.horizontalVelocity);
		}
						
	}

	@Override
	public void renderObject(Renderer renderer) {
//		renderer.drawTriangleArea(this.width, this.height, 0xffff0000, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, false);
//		this.renderRectArea(renderer, 0xffff0000, false);
		renderer.drawCircleArea(this.halfWidth, 0xffff0000, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, false);
	}

}
