package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

	protected String tag;

	protected Coordinate position;
	
	protected float angle;

	protected Vector linearOffset = new Vector(0, 0);

	protected int width;

	protected int height;

	protected int halfWidth;

	protected int halfHeight;

	protected Image staticImage;
	
	protected ImageTile animatedImage;
	
	protected int tileX = 0;

	protected int tileY = 0;
	
	protected GameObject attachedGameObject;
		
	protected List<BoundingArea> boundingAreas = new ArrayList<>();
	
	protected int priorityCollisionOrder = 0;
	
	protected List<String> ignoredCollisionInteractionGameObjectsTags = new ArrayList<>();
	
	protected GameObject(String tag, int width, int height, int positionX, int positionY) {
		this.tag = tag;
		this.width = width;
		this.height = height;
		this.halfWidth = this.width / 2;
		this.halfHeight = this.height / 2;
		this.position = new Coordinate(positionX, positionY);
	}

	protected GameObject(String tag, String staticImagePath, int positionX, int positionY) {
		this.tag = tag;
		this.staticImage = new Image(staticImagePath);
		this.width = this.staticImage.getWidth();
		this.height = this.staticImage.getHeight();
		this.halfWidth = this.width / 2;
		this.halfHeight = this.height / 2;
		this.position = new Coordinate(positionX, positionY);
	}

	protected GameObject(String tag, int width, int height, String animatedImagePath, int positionX, int positionY) {
		this.tag = tag;
		this.width = width;
		this.height = height;
		this.halfWidth = this.width / 2;
		this.halfHeight = this.height / 2;
		this.animatedImage = new ImageTile(animatedImagePath, width, height);
		this.position = new Coordinate(positionX, positionY);
	}

	/* game object event functions */
	
	public void processOffsetsChanges(Input input) {
		this.processAutomaticOffsetsChanges();
		this.processControlBasedOffsetsChanges(input);
	}

	public void processControlBasedOffsetsChanges(Input input) {
		// implement this method if this game object is controlled by the player
	}

	public void processAutomaticOffsetsChanges() {
		// implement this method to define this game object automatic moving animations
	}
	
	public void processImageAnimations(Input input) {
		// implement this method to define this game object image animations
	}

	public void addIgnoredCollisionInteractionGameObjectTag(String gameObjectTag) {
		this.ignoredCollisionInteractionGameObjectsTags.add(gameObjectTag);
	}
	
	public void processBoundingAreasInteractions() {
		for (BoundingArea boundingArea : this.boundingAreas) {
			boundingArea.move(this.linearOffset);
		}
		BoundingAreasInteractionsResolver.incrementCollisionQueue(this.boundingAreas, this.priorityCollisionOrder);
	}
	
	public void synchronizeOffsetAndAngleWithAttachedGameObject() {
		if (this.attachedGameObject != null) {
			this.linearOffset.plus(this.attachedGameObject.getLinearOffset());
		}
	}
	
	public void applyCollisionInteractionEvent(BoundingArea boudingArea) {
		// implement this method if this game object has a component
	}

	public void clearOffsets() {
		this.linearOffset.setX(0);
		this.linearOffset.setY(0);
	}

	public void updatePositionAndAngle() {
		this.position.move(this.linearOffset);
		
		// implement clean angle...............................
	}
	
	protected void addTriangularBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3) {
		this.boundingAreas.add(new TriangleBoundingArea(tag, this, vertex1, vertex2, vertex3));
	}

	protected void addRectangleBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3, Coordinate vertex4) {
		this.boundingAreas.add(new RectangleBoundingArea(tag, this, vertex1, vertex2, vertex3, vertex4));
	}

	protected void addCircleBoundingArea(String tag, Coordinate center, int radius) {
		this.boundingAreas.add(new CircleBoundingArea(tag, this, center, radius));
	}

	protected void addFittingTriangularBoundingArea(String tag, int mode) {
		switch (mode) {
		case 0:
			this.addTriangularBoundingArea(tag, 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight)
			);			
			break;
		case 1:
			this.addTriangularBoundingArea(tag, 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight), 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight)
			);			
			break;
		case 2:
			this.addTriangularBoundingArea(tag, 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight)
			);			
			break;
		case 3:
			this.addTriangularBoundingArea(tag, 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight)
			);			
			break;
		default:
			// put a log of error, because no available mode was chosen
			break;
		}
	}

	protected void addFittingRectangleBoundingArea(String tag) {
		this.addRectangleBoundingArea(tag, 
			new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
			new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
			new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight),
			new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight)
		);
	}

	protected void addFittingCircleBoundingArea(String tag) {
		this.addCircleBoundingArea(tag, this.position, this.halfWidth);
	}

	protected void moveRight(float horizontalVelocity) {
		this.linearOffset.plus(new Vector(Math.round(horizontalVelocity), 0));
	}

	protected void moveLeft(float horizontalVelocity) {
		this.linearOffset.plus(new Vector(-Math.round(horizontalVelocity), 0));
	}

	protected void moveDown(float verticalVelocity) {
		this.linearOffset.plus(new Vector(0, Math.round(verticalVelocity)));
	}

	protected void moveUp(float verticalVelocity) {
		this.linearOffset.plus(new Vector(0, -Math.round(verticalVelocity)));
	}

	protected void spinClock(float angularVelocity) {
		this.angle += angularVelocity;
	}

	protected void spinAntiClock(float angularVelocity) {
		this.angle -= angularVelocity;
	}

	/* render functions */
	
	public abstract void renderObject(Renderer renderer);
	
	protected void renderAnimatedImage(Renderer renderer, boolean blockLight) {
		renderer.drawImageTile(this.animatedImage, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, this.tileX, this.tileY, this.angle, blockLight);
	}

	protected void renderStaticImage(Renderer renderer, boolean blockLight) {
		renderer.drawImage(this.staticImage, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, this.angle, blockLight);
	}

	protected void renderRectArea(Renderer renderer, int color, boolean blockLight) {
		renderer.drawRectArea(this.width, this.height, color, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, this.angle, blockLight);
	}

	protected void renderTriangleRectangleArea(Renderer renderer, int color, int mode, boolean blockLight) {
		renderer.drawTriangleRectangleArea(this.width, this.height, color, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, mode, this.angle, blockLight);
	}

	protected void renderCircleArea(Renderer renderer, int color, boolean blockLight) {
		renderer.drawCircleArea(this.halfWidth, color, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, blockLight);
	}
	
	/* conventional getters and setters */
	
	public String getTag() {
		return this.tag;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
	
	public List<String> getIgnoredCollisionInteractionGameObjectsTags() {
		return this.ignoredCollisionInteractionGameObjectsTags;
	}

	public Coordinate getPosition() {
		return this.position;
	}

	public void setPosition(Coordinate position) {
		this.position = position;
	}

	public Vector getLinearOffset() {
		return this.linearOffset;
	}

	public void setLinearOffset(Vector offset) {
		this.linearOffset = offset;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

}
