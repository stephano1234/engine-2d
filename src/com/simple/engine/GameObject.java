package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

	protected String tag;

	protected Coordinate position;

	protected Vector offset = new Vector(0, 0);

	protected int width;

	protected int height;
	
	protected int paddingTop;
	
	protected int paddingBottom;
	
	protected int paddingLeft;
	
	protected int paddingRight;
	
	protected float rotationAngle = 0f;

	protected float horizontalVelocity = 0f;
	
	protected float verticalVelocity = 0f;
	
	protected float gravityAcceleration = 0f;

	protected float jumpInitialVelocity = 0f;

	protected float fallVelocity = 0f;
	
	protected boolean isOnFloor = false;
	
	protected boolean wasOnFloor = false;

	protected boolean isUnderGravityEffect = false;
	
	protected boolean disabled = false;
	
	protected GameObject attachedGameObject;
	
	protected List<BoundingArea> boundingAreas = new ArrayList<>();
	
	protected GameObject(String tag, int positionX, int positionY) {
		this.tag = tag;
		this.position = new Coordinate(positionX, positionY);
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
	
	public void processImageAnimations(Input input) {
		// implement this method to define this game object image animations
	}

	public void processBoundingAreasInteractions() {
		// reset on the floor check
		this.isOnFloor = false;
		// update the component
		for (BoundingArea boundingArea : this.boundingAreas) {
			boundingArea.move(this.offset);
		}
		CollisionDetector.incrementCollisionQueue(boundingAreas);
	}
	
	public void synchronizeOffsetWithAttachedGameObject() {
		if (this.attachedGameObject != null && this.isOnFloor) {
			this.offset.plus(this.attachedGameObject.getOffset());
		}
	}
	
	public void applyCollisionEvent(GameObject other) {
		// implement this method if this game object has a component
	}

	public void clearOffsets() {
		this.offset.setX(0);
		this.offset.setY(0);
	}

	public void updatePosition() {
		this.position.move(this.offset);
	}
	
	private void calculateGravityVariables() {
		// check if it's actually on the floor in the last update
		this.wasOnFloor = this.isOnFloor;
		// calculate gravity
		if (!this.isOnFloor) {
			this.fallVelocity += this.gravityAcceleration;
			this.offset.plus(new Vector(0, Math.round(this.fallVelocity)));
		}
	}
	
	protected void addTriangularBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3) {
		this.boundingAreas.add(new TriangularBoundingArea(tag, this, vertex1, vertex2, vertex3));
	}

	protected void addRectangleBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3, Coordinate vertex4) {
		this.boundingAreas.add(new RectangleBoundingArea(tag, this, vertex1, vertex2, vertex3, vertex4));
	}

	protected void jump() {
		this.fallVelocity = Math.round(this.jumpInitialVelocity);
		this.offset.plus(new Vector(0, Math.round(this.jumpInitialVelocity)));
	}

	protected void moveRight() {
		this.offset.plus(new Vector(Math.round(this.horizontalVelocity), 0));
	}

	protected void moveLeft() {
		this.offset.plus(new Vector(-Math.round(this.horizontalVelocity), 0));
	}

	protected void moveDown() {
		this.offset.plus(new Vector(0, Math.round(this.verticalVelocity)));
	}

	protected void moveUp() {
		this.offset.plus(new Vector(0, -Math.round(this.verticalVelocity)));
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
	
	public Coordinate getPosition() {
		return this.position;
	}

	public void setPosition(Coordinate position) {
		this.position = position;
	}

	public Vector getOffset() {
		return this.offset;
	}

	public void setOffset(Vector offset) {
		this.offset = offset;
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
