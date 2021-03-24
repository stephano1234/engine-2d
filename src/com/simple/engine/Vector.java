package com.simple.engine;

import java.util.List;

public class Vector {

	private double x;

	private double y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector(Coordinate firstPoint, Coordinate lastPoint) {
		this.x = lastPoint.getX() - firstPoint.getX();
		this.y = lastPoint.getY() - firstPoint.getY();
	}

	public void multiplyByScalar(double scalar) {
		this.x = this.x * scalar;
		this.y = this.y * scalar;
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

	public double getNorm() {
		return Math.sqrt((this.x * this.x + this.y * this.y));
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
