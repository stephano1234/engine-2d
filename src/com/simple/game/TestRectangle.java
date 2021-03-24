package com.simple.game;

import java.awt.event.KeyEvent;

import com.simple.engine.GameObject;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class TestRectangle extends GameObject {

	private int color;
	
	private boolean control;
	
	protected TestRectangle(String tag, int width, int height, int positionX, int positionY, int color, boolean control) {
		super(tag, width, height, positionX, positionY);
		this.color = color;
		this.control = control;
		this.addFittingCollidingRectangleBoundingArea(tag);
	}

	@Override
	public void processControlBasedOffsetsChanges(Input input) {
		if (this.control) {
			if (input.isKey(KeyEvent.VK_A)) {
				this.spinAntiClock(0.01f);
			}		
			if (input.isKey(KeyEvent.VK_S)) {
				this.spinClock(0.01f);
			}		
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
	public void renderObject(Renderer renderer) {
		this.renderRectArea(renderer, this.color, false);
		this.showBoundingAreasBorders(renderer, 0xffff0000);
	}

}
