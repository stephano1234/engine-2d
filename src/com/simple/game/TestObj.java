package com.simple.game;

import static com.simple.engine.GameRunner.TILE_SIZE;

import java.awt.event.KeyEvent;

import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class TestObj extends GameObject {

	private float horizontalVelocity = 0f;
	
	private float verticalVelocity = 0f;
	
	public TestObj(int positionX, int positionY) {
		super("triTeste", TILE_SIZE * 3, TILE_SIZE * 3, positionX, positionY);
		this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;
		this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;
//		this.addFittingCircleBoundingArea("a");
		this.addFittingTriangularBoundingArea("a");
		this.priorityCollisionOrder = 0;
	}

	@Override
	public void processControlBasedOffsetsChanges(Input input) {
		
		if (input.isKey(KeyEvent.VK_S)) {
			this.spinClock(0.02f);
		}
		
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
//		this.renderCircleArea(renderer, 0xffff0000, false);
		this.renderTriangleRectangleArea(renderer, 0xffff0000, 1, false);
	}

}
