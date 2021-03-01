package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

	protected String tag;

	protected Coordinate position;

	protected Vector offset = new Vector(0, 0);

	protected int width;

	protected int height;

	protected int halfWidth;

	protected int halfHeight;

	protected Image staticImage;
	
	protected ImageTile animatedImage;
	
	protected int tileX = 0;

	protected int tileY = 0;

	protected float rotationAngle = 0f;
	
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
		this.setConfigs();
	}

	protected GameObject(String tag, String staticImagePath, int positionX, int positionY) {
		this.tag = tag;
		this.staticImage = new Image(staticImagePath);
		this.width = this.staticImage.getWidth();
		this.height = this.staticImage.getHeight();
		this.halfWidth = this.width / 2;
		this.halfHeight = this.height / 2;
		this.position = new Coordinate(positionX, positionY);
		this.setConfigs();
	}

	protected GameObject(String tag, int width, int height, String animatedImagePath, int positionX, int positionY) {
		this.tag = tag;
		this.width = width;
		this.height = height;
		this.halfWidth = this.width / 2;
		this.halfHeight = this.height / 2;
		this.animatedImage = new ImageTile(animatedImagePath, width, height);
		this.position = new Coordinate(positionX, positionY);
		this.setConfigs();
	}

	protected void setConfigs() {
		// implement this method to set the initial configurations of this game object
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
			boundingArea.move(this.offset);
		}
		BoundingAreasInteractionsResolver.incrementCollisionQueue(this.boundingAreas, this.priorityCollisionOrder);
	}
	
	public void synchronizeOffsetWithAttachedGameObject() {
		if (this.attachedGameObject != null) {
			this.offset.plus(this.attachedGameObject.getOffset());
		}
	}
	
	public void applyCollisionInteractionEvent(BoundingArea boudingArea) {
		// implement this method if this game object has a component
	}

	public void clearOffsets() {
		this.offset.setX(0);
		this.offset.setY(0);
	}

	public void updatePosition() {
		this.position.move(this.offset);
	}
	
	protected void addTriangularBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3) {
		this.boundingAreas.add(new TriangularBoundingArea(tag, this, vertex1, vertex2, vertex3));
	}

	protected void addRectangleBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3, Coordinate vertex4) {
		this.boundingAreas.add(new RectangleBoundingArea(tag, this, vertex1, vertex2, vertex3, vertex4));
	}

	protected void moveRight(float horizontalVelocity) {
		this.offset.plus(new Vector(Math.round(horizontalVelocity), 0));
	}

	protected void moveLeft(float horizontalVelocity) {
		this.offset.plus(new Vector(-Math.round(horizontalVelocity), 0));
	}

	protected void moveDown(float verticalVelocity) {
		this.offset.plus(new Vector(0, Math.round(verticalVelocity)));
	}

	protected void moveUp(float verticalVelocity) {
		this.offset.plus(new Vector(0, -Math.round(verticalVelocity)));
	}

	/* render functions */
	
	public abstract void renderObject(Renderer renderer);
	
	public void renderAnimatedImage(Renderer renderer, boolean blockLight) {
		renderer.drawImageTile(this.animatedImage, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, this.tileX, this.tileY, blockLight);
	}

	public void renderStaticImage(Renderer renderer, boolean blockLight) {
		renderer.drawImage(this.staticImage, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, blockLight);
	}

	public void renderRectArea(Renderer renderer, int color, boolean blockLight) {
		renderer.drawRectArea(this.width, this.height, color, this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight, blockLight);
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

	public Vector getOffset() {
		return this.offset;
	}

	public void setOffset(Vector offset) {
		this.offset = offset;
	}

}
