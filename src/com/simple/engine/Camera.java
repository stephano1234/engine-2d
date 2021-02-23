package com.simple.engine;

public class Camera {

	private int width;
	
	private int height;
	
	private int offsetX;
	
	private int offsetY;
	
	private GameObject target;
	
	public Camera(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public Camera(String tag, GameRunner gameRunner) {
		if (this.target == null) {
			this.target = gameRunner.getGameObjectByTag(tag);
		}
	}
	
	public void updateOffsets() {
		if (this.target != null) {
			this.offsetX = this.target.getPosition().getX() + this.target.getWidth() / 2;
			this.offsetY = this.target.getPosition().getY() + this.target.getHeight() / 2;
		}
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public GameObject getTarget() {
		return target;
	}

	public void setTarget(GameObject target) {
		this.target = target;
	}
	
}
