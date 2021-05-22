package com.simple.game;

import com.simple.engine.Coordinate;
import com.simple.engine.GameObject;
import com.simple.engine.Renderer;

public class Room extends GameObject {

	protected Room() {
		super("room", "/images/room.png", 160, 120);
		this.addCollidingRectangleBoundingArea("east-wall", 
				new Coordinate(288, 16), 
				new Coordinate(320, 16), 
				new Coordinate(320, 208), 
				new Coordinate(288, 208) 
			)
		;
		this.addCollidingRectangleBoundingArea("west-wall", 
				new Coordinate(0, 16), 
				new Coordinate(32, 16), 
				new Coordinate(32, 208), 
				new Coordinate(0, 208) 
			)
		;
		this.addCollidingRectangleBoundingArea("north-wall", 
				new Coordinate(0, 0), 
				new Coordinate(320, 0), 
				new Coordinate(320, 16), 
				new Coordinate(0, 16) 
			)
		;
		this.addCollidingRectangleBoundingArea("south-wall", 
				new Coordinate(0, 208), 
				new Coordinate(320, 208), 
				new Coordinate(320, 240), 
				new Coordinate(0, 240) 
			)
		;
	}

	@Override
	public void renderObject(Renderer renderer) {
		renderer.drawImage(this.staticImage, 0, 0, 0, false);
	}

}
