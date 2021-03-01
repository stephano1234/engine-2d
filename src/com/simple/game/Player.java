package com.simple.game;

import static com.simple.engine.GameRunner.TILE_SIZE;

import java.awt.event.KeyEvent;

import com.simple.engine.BoundingArea;
import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class Player extends GameObject {
	
	private int walkAnimationCounter = 0;
	
	private int walkAnimationDuration = 3;
	
	private float horizontalVelocity = 0f;
	
	private float verticalVelocity = 0f;
	
	public Player(int positionX, int positionY) {
		super("player", TILE_SIZE, TILE_SIZE, positionX, positionY);
		this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;
		this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 2f;
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
	public void processImageAnimations(Input input) {
		
	}
	
	@Override
	public void renderObject(Renderer renderer) {

	}

}
