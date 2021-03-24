package com.simple.engine;

public class Coordinate {

	private double x;
	
	private double y;

	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void move(Vector vector) {
		this.x += vector.getX();
		this.y += vector.getY();
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}
