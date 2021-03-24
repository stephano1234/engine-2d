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

	@Override
	public CircleBoundingArea spin(double angle) {
		// a spin in a circle makes no effect in the positioning of its bounding area
		return this;
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
