package com.simple.engine;

import java.util.List;

public class Vector {

	private int x;

	private int y;

	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void multiplyByScalar(double scalar) {
		this.x = (int) Math.round(this.x * scalar);
		this.y = (int) Math.round(this.y * scalar);
	}

	public void plus(Vector other) {
		this.x += other.getX();
		this.y += other.getY();
	}

	public void plus(List<Vector> others) {
		others.forEach(other -> {
			this.x += other.getX();
			this.y += other.getY();
		});
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
