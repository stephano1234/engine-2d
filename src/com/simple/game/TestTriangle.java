package com.simple.game;

import static com.simple.engine.GameRunner.TILE_SIZE;

import java.awt.event.KeyEvent;

import com.simple.engine.Coordinate;
import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class TestTriangle extends GameObject {
		
	public TestTriangle(int positionX, int positionY) {
		super("triTeste", positionX, positionY);
		this.isOnFloor = false;
		this.isUnderGravityEffect = false;
		this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;
		this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;
		this.width = TILE_SIZE;
		this.height = TILE_SIZE;
		this.addTriangularBoundingArea("b", 
				new Coordinate(this.position.getX(), this.position.getY()), 
				new Coordinate(this.position.getX() + this.width, this.position.getY()), 
				new Coordinate(this.position.getX() + this.width, this.position.getY() + this.height)
				);
	}

	@Override
	public void applyCollisionEvent(GameObject other) {

	}
	
	@Override
	public void updateControlBasedOffsetsChanges(Input input) {

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
	public void renderObject(Renderer renderer) {
		renderer.drawFilledTriangle(this.width, this.height, 0xff000000, this.position.getX(), this.position.getY());
	}

}
