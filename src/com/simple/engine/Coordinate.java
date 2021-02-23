package com.simple.engine;

public class Coordinate {

	private int x;
	
	private int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void move(Vector vector) {
		this.x += vector.getX();
		this.y += vector.getY();
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
