package com.simple.engine;

public class Camera {

	private Coordinate position;
	
	private GameObject target;
	
	public Camera(int x, int y) {
		this.position = new Coordinate(x, y);
	}
	
	public void updatePosition() {
		if (this.target != null) {
			this.position = this.target.getPosition();
		}
	}
	
	public Coordinate getPosition() {
		return position;
	}

	public void setPosition(Coordinate position) {
		this.position = position;
	}

	public GameObject getTarget() {
		return target;
	}

	public void setTarget(GameObject target) {
		this.target = target;
	}
	
}
