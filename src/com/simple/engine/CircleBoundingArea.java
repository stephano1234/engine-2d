package com.simple.engine;

public class CircleBoundingArea extends BoundingArea {

	private Coordinate center;
	
	private int radius;
	
	public CircleBoundingArea(String tag, GameObject gameObject, Coordinate center, int radius) {
		super(tag, gameObject);
		this.center = center;
		this.radius = radius;
	}

	@Override
	public void move(Vector offset) {
		this.center.move(offset);
	}

	public Coordinate getCenter() {
		return center;
	}

	public void setCenter(Coordinate center) {
		this.center = center;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

}
