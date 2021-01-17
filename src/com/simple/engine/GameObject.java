package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

	protected String tag;

	protected int positionX;

	protected int positionY;

	protected int offsetX;

	protected int offsetY;

	protected int width;

	protected int height;
	
	protected int paddingTop;
	
	protected int paddingBottom;
	
	protected int paddingLeft;
	
	protected int paddingRight;

	protected float horizontalVelocity = 0f;
	
	protected float verticalVelocity = 0f;
	
	protected float gravityAcceleration = 0f;

	protected float jumpInitialVelocity = 0f;

	protected float fallVelocity = 0f;
	
	protected boolean isOnFloor = false;
	
	protected boolean wasOnFloor = false;

	protected boolean isUnderGravityEffect = true;
	
	protected boolean disabled = false;
	
	protected GameObject synchronizedOffsetsGameObject;
	
	protected List<Component> components = new ArrayList<>();
	
	protected GameObject(String tag, int positionX, int positionY) {
		this.tag = tag;
		this.positionX = positionX;
		this.positionY = positionY;
		// apply initial configurations of this game object
		this.setConfigs();
	}

	protected void setConfigs() {
		// implement this method to set the initial configurations of this game object
	}
	
	public void updateOffsets(Input input) {
		
		if (this.isUnderGravityEffect) {
			this.calculateGravityVariables();
		}
		
		this.updateControlBasedOffsetsChanges(input);
		
		this.updateAutomaticOffsetsChanges();
		
	}

	public abstract void renderObject(Renderer renderer);

	public void updateControlBasedOffsetsChanges(Input input) {
		// implement this method if this game object is controlled by the player
	}

	public void updateAutomaticOffsetsChanges() {
		// implement this method to define this game object automatic moving animations
	}
	
	public void updateImageAnimations(Input input) {
		// implement this method to define this game object image animations
	}
	
	public void updateComponents() {
		// reset on the floor check
		this.isOnFloor = false;
		// update the components
		for (Component component : this.components) {
			component.update();
		}
	}
	
	public void updateSynchronizedOffsets() {
		if (this.synchronizedOffsetsGameObject != null && this.isOnFloor) {
			this.offsetX += this.synchronizedOffsetsGameObject.getOffsetX();
			this.offsetY += this.synchronizedOffsetsGameObject.getOffsetY();
		}
	}
	
	public void applyAxisAlignedBoundingBoxCollisionEvent(GameObject other) {
		// implement this method if this game object is an axis aligned bounding box component
	}
	
	public void clearOffsets() {
		this.offsetX = 0;
		this.offsetY = 0;
	}

	public void updatePosition() {
		this.positionX += this.offsetX;
		this.positionY += this.offsetY;
	}
	
	private void calculateGravityVariables() {
		// check if it's actually on the floor in the last update
		this.wasOnFloor = this.isOnFloor;
		// calculate gravity
		if (!this.isOnFloor) {
			this.fallVelocity += this.gravityAcceleration;
			this.offsetY += Math.round(this.fallVelocity);		
		}
	}
	
	protected void addAxisAlignedBoundingBox() {
		this.components.add(new AxisAlignedBoundingBox(this));
	}
	
	protected void collideWithoutBouncing(GameObject other) {
		int minimumDistanceX = 0;
		int minimumDistanceY = 0;
		if (this.positionY <= other.getPositionY()) {
			minimumDistanceY = other.getPositionY() + other.getOffsetY() + other.getPaddingTop() - (this.positionY + this.offsetY + this.height - this.paddingBottom);
		} else {
			minimumDistanceY = other.getPositionY() + other.getOffsetY() - other.getPaddingBottom() + other.getHeight() - (this.positionY + this.offsetY + this.paddingTop);
		}
		if (this.positionX <= other.getPositionX()) {
			minimumDistanceX = other.getPositionX() + other.getOffsetX() + other.getPaddingLeft() - (this.positionX + this.offsetX + this.width - this.paddingRight);			
		} else {
			minimumDistanceX = other.getPositionX() + other.getOffsetX() - other.getPaddingRight() + other.getWidth() - (this.positionX + this.offsetX + this.paddingLeft);			
		}
		if (Math.abs(minimumDistanceX) < Math.abs(minimumDistanceY)) {
			this.offsetX += minimumDistanceX;
		} else {
			if (this.positionY <= other.getPositionY()) {
				this.synchronizedOffsetsGameObject = other;
				this.isOnFloor = this.fallVelocity >= 0;
				this.fallVelocity = this.fallVelocity > 0 ? 0 : this.fallVelocity;
			} else {
				this.fallVelocity = this.fallVelocity < 0 ? 0 : this.fallVelocity;
			}
			this.offsetY += minimumDistanceY;
		}
	}
	
	protected void jump() {
		this.fallVelocity = Math.round(this.jumpInitialVelocity);
		this.offsetY = Math.round(this.jumpInitialVelocity);
	}

	protected void moveRight() {
		this.offsetX += Math.round(this.horizontalVelocity);
	}

	protected void moveLeft() {
		this.offsetX -= Math.round(this.horizontalVelocity);
	}

	// conventional getters and setters
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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
	
	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
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

	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}	

}
