package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

	protected String tag;

	protected Coordinate position;
	
	protected double angle = 0;

	protected double angularVelocity = 0;

	protected Vector linearOffset = new Vector(0, 0);

	protected double horizontalVelocity = 0;
	
	protected double verticalVelocity = 0;

	protected int width;

	protected int height;

	protected int halfWidth;

	protected int halfHeight;

	protected Image staticImage;
	
	protected ImageTile animatedImage;
	
	protected int tileX = 0;

	protected int tileY = 0;

	protected int tileTimeCounter = 0;
	
	protected int changeTileTime = 0;
	
	protected GameObject attachedGameObject;

	protected List<BoundingArea> intersectingBoundingAreas = new ArrayList<>();

	protected List<BoundingArea> collidingBoundingAreas = new ArrayList<>();

	protected List<String> ignoredIntersectionInteractionGameObjectsTags = new ArrayList<>();

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

	protected GameObject(String tag, int width, int height, String animatedImagePath, int tileWidth, int tileHeight, int positionX, int positionY) {
		this.tag = tag;
		this.width = width;
		this.height = height;
		this.halfWidth = this.width / 2;
		this.halfHeight = this.height / 2;
		this.animatedImage = new ImageTile(animatedImagePath, tileWidth, tileHeight);
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

	public void addIgnoredIntersectionInteractionGameObjectTag(String gameObjectTag) {
		this.ignoredIntersectionInteractionGameObjectsTags.add(gameObjectTag);
	}

	public void addIgnoredCollisionInteractionGameObjectTag(String gameObjectTag) {
		this.ignoredCollisionInteractionGameObjectsTags.add(gameObjectTag);
	}
	
	public void processCollidingBoundingAreasInteractions() {
		for (BoundingArea boundingArea : this.collidingBoundingAreas) {
			boundingArea.move(this.linearOffset);
		}
		BoundingAreasInteractionsResolver.incrementCollisionQueue(this.collidingBoundingAreas);
	}

	public void processIntersectingBoundingAreasInteractions() {
		for (BoundingArea boundingArea : this.intersectingBoundingAreas) {
			boundingArea.move(this.linearOffset);
		}
		BoundingAreasInteractionsResolver.incrementIntersectionQueue(this.intersectingBoundingAreas);
	}

	public void synchronizeOffsetAndAngleWithAttachedGameObject() {
		if (this.attachedGameObject != null) {
			this.linearOffset.plus(this.attachedGameObject.getLinearOffset());
			this.angle = this.attachedGameObject.getAngle();
		}
	}

	public void applyIntersectionInteractionEvent(BoundingArea boudingArea) {
		// implement this method if this game object has a component
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
		if (this.angle >= 360) {
			this.angle = 0;
		}
	}

	protected void addIntersectingTriangularBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3) {
		this.intersectingBoundingAreas.add(new TriangleBoundingArea(tag, this, vertex1, vertex2, vertex3));
	}

	protected void addIntersectingRectangleBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3, Coordinate vertex4) {
		this.intersectingBoundingAreas.add(new RectangleBoundingArea(tag, this, vertex1, vertex2, vertex3, vertex4));
	}

	protected void addIntersectingCircleBoundingArea(String tag, Coordinate center, int radius) {
		this.intersectingBoundingAreas.add(new CircleBoundingArea(tag, this, center, radius));
	}

	protected void addCollidingTriangularBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3) {
		this.intersectingBoundingAreas.add(new TriangleBoundingArea(tag, this, vertex1, vertex2, vertex3));
	}

	protected void addCollidingRectangleBoundingArea(String tag, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3, Coordinate vertex4) {
		this.intersectingBoundingAreas.add(new RectangleBoundingArea(tag, this, vertex1, vertex2, vertex3, vertex4));
		this.collidingBoundingAreas.add(new RectangleBoundingArea(tag, this, vertex1, vertex2, vertex3, vertex4));
	}

	protected void addCollidingCircleBoundingArea(String tag, Coordinate center, int radius) {
		this.intersectingBoundingAreas.add(new CircleBoundingArea(tag, this, center, radius));
		this.collidingBoundingAreas.add(new CircleBoundingArea(tag, this, center, radius));
	}

	protected void addFittingCollidingTriangleRectangleBoundingArea(String tag, int mode) {
		switch (mode) {
		case 0:
			this.addCollidingTriangularBoundingArea(tag, 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight)
			);			
			break;
		case 1:
			this.addCollidingTriangularBoundingArea(tag, 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight), 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight)
			);			
			break;
		case 2:
			this.addCollidingTriangularBoundingArea(tag, 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
				new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight), 
				new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight)
			);			
			break;
		case 3:
			this.addCollidingTriangularBoundingArea(tag, 
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

	protected void addFittingCollidingRectangleBoundingArea(String tag) {
		this.addCollidingRectangleBoundingArea(tag, 
			new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() - this.halfHeight), 
			new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() - this.halfHeight), 
			new Coordinate(this.position.getX() + this.halfWidth, this.position.getY() + this.halfHeight),
			new Coordinate(this.position.getX() - this.halfWidth, this.position.getY() + this.halfHeight)
		);
	}

	protected void addFittingCollidingCircleBoundingArea(String tag) {
		this.addCollidingCircleBoundingArea(tag, this.position, this.halfWidth);
	}

	protected void moveRight(double horizontalVelocity) {
		this.linearOffset.plus(new Vector(horizontalVelocity, 0));
	}

	protected void moveLeft(double horizontalVelocity) {
		this.linearOffset.plus(new Vector(-horizontalVelocity, 0));
	}

	protected void moveDown(double verticalVelocity) {
		this.linearOffset.plus(new Vector(0, verticalVelocity));
	}

	protected void moveUp(double verticalVelocity) {
		this.linearOffset.plus(new Vector(0, -verticalVelocity));
	}

	protected void spinClock(double angularVelocity) {
		this.angle += angularVelocity;
	}

	protected void spinAntiClock(double angularVelocity) {
		this.angle -= angularVelocity;
	}

	protected void moveRight() {
		this.linearOffset.plus(new Vector(this.horizontalVelocity, 0));
	}

	protected void moveLeft() {
		this.linearOffset.plus(new Vector(-this.horizontalVelocity, 0));
	}

	protected void moveDown() {
		this.linearOffset.plus(new Vector(0, this.verticalVelocity));
	}

	protected void moveUp() {
		this.linearOffset.plus(new Vector(0, -this.verticalVelocity));
	}

	protected void spinClock() {
		this.angle += this.angularVelocity;
	}

	protected void spinAntiClock() {
		this.angle -= this.angularVelocity;
	}

	/* render functions */
	
	public abstract void renderObject(Renderer renderer);
	
	protected void renderAnimatedImage(Renderer renderer, boolean blockLight) {
		renderer.drawImageTile(this.animatedImage, (int) Math.round(this.position.getX() - this.halfWidth), (int) Math.round(this.position.getY() - this.halfHeight), this.tileX, this.tileY, this.angle, blockLight);
	}

	protected void renderStaticImage(Renderer renderer, boolean blockLight) {
		renderer.drawImage(this.staticImage, (int) Math.round(this.position.getX() - this.halfWidth), (int) Math.round(this.position.getY() - this.halfHeight), this.angle, blockLight);
	}

	protected void renderRectArea(Renderer renderer, int color, boolean blockLight) {
		renderer.drawRectArea(this.width, this.height, color, (int) Math.round(this.position.getX() - this.halfWidth), (int) Math.round(this.position.getY() - this.halfHeight), this.angle, blockLight);
	}

	protected void renderTriangleRectangleArea(Renderer renderer, int color, int mode, boolean blockLight) {
		renderer.drawTriangleRectangleArea(this.width, this.height, color, (int) Math.round(this.position.getX() - this.halfWidth), (int) Math.round(this.position.getY() - this.halfHeight), mode, this.angle, blockLight);
	}

	protected void renderCircleArea(Renderer renderer, int color, boolean blockLight) {
		renderer.drawCircleArea(this.halfWidth, color, (int) Math.round(this.position.getX() - this.halfWidth), (int) Math.round(this.position.getY() - this.halfHeight), blockLight);
	}

	protected void showBoundingAreasBorders(Renderer renderer, int color) {
		for (BoundingArea boundingArea : this.collidingBoundingAreas) {
			if (boundingArea instanceof TriangleBoundingArea) {				
				TriangleBoundingArea triangleBoundingArea = (TriangleBoundingArea) boundingArea.spin(this.angle);
				renderer.drawMinThicknessLine(triangleBoundingArea.getVertex1().getX(), triangleBoundingArea.getVertex1().getY(), triangleBoundingArea.getVertex2().getX(), triangleBoundingArea.getVertex2().getY(), color);
				renderer.drawMinThicknessLine(triangleBoundingArea.getVertex2().getX(), triangleBoundingArea.getVertex2().getY(), triangleBoundingArea.getVertex3().getX(), triangleBoundingArea.getVertex3().getY(), color);
				renderer.drawMinThicknessLine(triangleBoundingArea.getVertex3().getX(), triangleBoundingArea.getVertex3().getY(), triangleBoundingArea.getVertex1().getX(), triangleBoundingArea.getVertex1().getY(), color);
			}
			if (boundingArea instanceof RectangleBoundingArea) {				
				RectangleBoundingArea rectangleBoundingArea = (RectangleBoundingArea) boundingArea.spin(this.angle);
				renderer.drawMinThicknessLine(rectangleBoundingArea.getVertex1().getX(), rectangleBoundingArea.getVertex1().getY(), rectangleBoundingArea.getVertex2().getX(), rectangleBoundingArea.getVertex2().getY(), color);
				renderer.drawMinThicknessLine(rectangleBoundingArea.getVertex2().getX(), rectangleBoundingArea.getVertex2().getY(), rectangleBoundingArea.getVertex3().getX(), rectangleBoundingArea.getVertex3().getY(), color);
				renderer.drawMinThicknessLine(rectangleBoundingArea.getVertex3().getX(), rectangleBoundingArea.getVertex3().getY(), rectangleBoundingArea.getVertex4().getX(), rectangleBoundingArea.getVertex4().getY(), color);
				renderer.drawMinThicknessLine(rectangleBoundingArea.getVertex4().getX(), rectangleBoundingArea.getVertex4().getY(), rectangleBoundingArea.getVertex1().getX(), rectangleBoundingArea.getVertex1().getY(), color);
			}
		}
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

	public List<String> getIgnoredIntersectionInteractionGameObjectsTags() {
		return this.ignoredIntersectionInteractionGameObjectsTags;
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

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

}
