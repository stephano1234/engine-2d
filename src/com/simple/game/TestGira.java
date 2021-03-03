package com.simple.game;

import java.awt.event.KeyEvent;

import com.simple.engine.GameObject;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class TestGira extends GameObject {

	public TestGira(String tag, int width, int height, int positionX, int positionY) {
		super(tag, width, height, positionX, positionY);
		this.addFittingRectangleBoundingArea("a");
		this.priorityCollisionOrder = 0;
	}
	
	@Override
	public void processControlBasedOffsetsChanges(Input input) {
		
//		if (input.isKey(KeyEvent.VK_A)) {
//			this.spinClock(0.02f);
//		}
		
	}
	
	@Override
	public void renderObject(Renderer renderer) {
		renderer.drawRectArea(this.width, this.height, 0xff00ff00, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, this.angle, false);
	}

}
