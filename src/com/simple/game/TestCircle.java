package com.simple.game;

import java.awt.event.KeyEvent;

import com.simple.engine.BoundingArea;
import com.simple.engine.GameObject;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class TestCircle extends GameObject {

	private int color;
	
	private boolean control;
	
	protected TestCircle(String tag, int radius, int positionX, int positionY, int color, boolean control) {
		super(tag, radius * 2, radius * 2, positionX, positionY);
		this.color = color;
		this.control = control;
		this.addFittingCollidingCircleBoundingArea(tag);
	}

	@Override
	public void processControlBasedOffsetsChanges(Input input) {
		if (this.control) {
			if (input.isKey(KeyEvent.VK_DOWN)) {
				this.moveDown(1f);
			}
			if (input.isKey(KeyEvent.VK_UP)) {
				this.moveUp(1f);
			}
			if (input.isKey(KeyEvent.VK_LEFT)) {
				this.moveLeft(1f);
			}
			if (input.isKey(KeyEvent.VK_RIGHT)) {
				this.moveRight(1f);
			}	
		}					
	}

	@Override
	public void applyCollisionInteractionEvent(BoundingArea boudingArea) {
//		this.color = 0xffff0000;
	}

	@Override
	public void renderObject(Renderer renderer) {
		this.showBoundingAreasBorders(renderer, 0xffff0000);
		this.renderCircleArea(renderer, this.color, false);
//		this.color = 0xff00ff00;
	}

}
