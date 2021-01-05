package com.simple.engine;

import static com.simple.engine.GameRunner.HALF_TILE_SIZE;
import static com.simple.engine.GameRunner.TILE_SIZE;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

	protected String tag;

	protected TileEventMap collisionMap;
	
	protected int positionX;

	protected int positionY;

	protected int tileX;

	protected int tileY;

	protected int nearestTileX;

	protected int nearestTileY;

	protected int nextTileX;

	protected int nextTileY;

	protected int nextNearestTileX;

	protected int nextNearestTileY;
	
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

	protected boolean passThroughtCollision = false;
	
	protected boolean isUnderGravityEffect = true;
	
	protected boolean disabled = false;
	
	protected List<Component> components = new ArrayList<>();
	
	protected GameObject(String tag, int tileX, int tileY, int width, int height, int paddingTop, int paddingBottom, int paddingLeft, int paddingRight) {
		this.tag = tag;
		this.positionX = tileX * TILE_SIZE;
		this.positionY = tileY * TILE_SIZE;
		this.tileX = tileX;
		this.tileY = tileY;
		this.nearestTileX = tileX;
		this.nearestTileY = tileY;
		this.nextTileX = tileX;
		this.nextTileY = tileY;
		this.nextNearestTileX = tileX;
		this.nextNearestTileY = tileY;
		this.width = width;
		this.height = height;
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
		// apply initial configurations of this game object
		this.setConfigs();
	}

	protected void setConfigs() {
		// implement this method to set the initial configurations of this game object
	}
	
	public void updateObjectState(Input input) {
		
		this.clearOffsets();
		
		if (this.isUnderGravityEffect) {
			this.calculateGravityVariables();
		}
		
		this.applyControlEvents(input);
		
		this.updateOffsets();
		
		this.applyObjectAnimation();

		this.updateComponents();
		
		// ------------
		if (!this.passThroughtCollision) {			
			this.calculateCollision();
		}
		// ------------
		
		this.updatePosition();
		
		// ------------
		this.updateTile();
		// ------------
		
	}

	public abstract void renderObject(Renderer renderer);

	public void applyControlEvents(Input input) {
		// implement this method if this game object is controlled by the player
	}

	public void applyObjectAnimation() {
		// implement this method to define this game object animations
	}
	
	private void updateComponents() {
		for (Component component : this.components) {
			component.update();
		}
	}
	
	private void addComponent(Component component) {
		this.components.add(component);
	}
	
	public void applyAxisAlignedBoundingBoxEvent(GameObject other) {
		// implement this method if this game object is an axis aligned bounding box component
	}
	
	private void clearOffsets() {
		this.offsetX = 0;
		this.offsetY = 0;
	}

	private void updateOffsets() {
		this.offsetY += Math.round(this.fallVelocity);		
	}
	
	private void updatePosition() {
		this.positionX += this.offsetX;
		this.positionY += this.offsetY;
	}

	private void updateTile() {
		this.tileX = this.roundTilePosition(this.positionX + this.paddingLeft, this.paddingRight + this.paddingLeft);
		this.tileY = this.roundTilePosition(this.positionY + this.paddingTop, this.paddingBottom + this.paddingTop);
		this.nearestTileX = this.getNearestTileX();
		this.nearestTileY = this.getNearestTileY();
	}
	
	private int roundTilePosition(int position, int padding) {
		if (position % TILE_SIZE <= HALF_TILE_SIZE + padding / 2) {
			return position / TILE_SIZE;
		}
		return (position / TILE_SIZE) + 1;
	}

	private int getNearestTileX() {
		if (this.positionX + this.paddingLeft < this.tileX * TILE_SIZE) {
			return this.tileX - 1;
		}
		if (this.positionX - this.paddingRight > this.tileX * TILE_SIZE) {
			return this.tileX + 1;
		}
		return this.tileX;
	}

	private int getNearestTileY() {
		if (this.positionY + this.paddingTop < this.tileY * TILE_SIZE) {
			return this.tileY - 1;
		}
		if (this.positionY - this.paddingBottom > this.tileY * TILE_SIZE) {
			return this.tileY + 1;
		}
		return this.tileY;
	}

	private void calculateGravityVariables() {
		// check if it's actually on the floor in the last update
		this.wasOnFloor = this.isOnFloor;
		// check if it's actually on the floor
		this.isOnFloor = (
			this.collisionMap.isEventTile(this.tileX, this.tileY + 1)
			||
			this.collisionMap.isEventTile(this.nearestTileX, this.tileY + 1)
			)
			&&
			this.positionY == (this.tileY * TILE_SIZE) + this.paddingBottom
		;
		// calculate gravity
		this.fallVelocity += this.gravityAcceleration;
	}
	
	private void calculateCollision() {
		
		// calculate perfect lateral collisions
		
		if (
			this.offsetY > 0
			&&
			this.collisionMap.isEventTile(this.tileX, this.tileY + 1)
			&&
			this.positionY - this.paddingBottom >= this.tileY * TILE_SIZE
			) {
			this.fallVelocity = 0;
			this.offsetY = 0;
		}

		if (
			this.offsetY < 0
			&&
			this.collisionMap.isEventTile(this.tileX, this.tileY - 1)
			&&
			this.positionY + this.paddingTop <= this.tileY * TILE_SIZE
			) {
			this.fallVelocity = 0;
			this.offsetY = 0;
		}

		if (
			this.offsetX > 0
			&&
			this.collisionMap.isEventTile(this.tileX + 1, this.tileY)
			&&
			this.positionX - this.paddingRight >= this.tileX * TILE_SIZE
			) {
			this.offsetX = 0;
		}

		if (
			this.offsetX < 0
			&&
			this.collisionMap.isEventTile(this.tileX - 1, this.tileY)
			&&
			this.positionX + this.paddingLeft <= this.tileX * TILE_SIZE
			) {
			this.offsetX = 0;
		}

		// calculate offset ratios
		
		float ratioYX = this.offsetX != 0 ? (float) this.offsetY / this.offsetX : 0;
		float ratioXY = this.offsetY != 0 ? (float) this.offsetX / this.offsetY : 0;
		
		// calculate collisions

		int posX = 0;
		int posY = 0;
		
		if (this.offsetY > 0) {
			int x = 0;
			for (int y = 0; y <= this.offsetY; y++) {
				int aux = (int) (y * ratioXY);
				if (aux % TILE_SIZE == 0) {					
					x = aux;
				}
				this.calculateNextNearestTilesCoordinates(x, y);
		 		if (
					this.collisionMap.isEventTile(this.nextTileX, this.nextNearestTileY)
					||
					this.collisionMap.isEventTile(this.nextNearestTileX, this.nextNearestTileY)
					) {
					this.fallVelocity = 0;
					this.offsetY = 0;
					posY = (this.nextNearestTileY - 1) * TILE_SIZE + this.paddingBottom;
					break;
				}
			}
		}

		if (this.offsetY < 0) {
			int x = 0;
			for (int y = 0; y >= this.offsetY; y--) {
				int aux = (int) (y * ratioXY);
				if (aux % TILE_SIZE == 0) {					
					x = aux;
				}
				this.calculateNextNearestTilesCoordinates(x, y);
				if (
					this.collisionMap.isEventTile(this.nextTileX, this.nextNearestTileY)
					||
					this.collisionMap.isEventTile(this.nextNearestTileX, this.nextNearestTileY)
					) {
					this.fallVelocity = 0;
					this.offsetY = 0;
					posY = (this.nextNearestTileY + 1) * TILE_SIZE - this.paddingTop;
					break;
				}
			}
		}

		if (this.offsetX > 0) {
			int y = 0;
			for (int x = 0; x <= this.offsetX; x++) {
				int aux = (int) (x * ratioYX);
				if (aux % TILE_SIZE == 0) {					
					y = aux;
				}
				this.calculateNextNearestTilesCoordinates(x, y);
				if (
					this.collisionMap.isEventTile(this.nextNearestTileX, this.nextTileY)
					||
					this.collisionMap.isEventTile(this.nextNearestTileX, this.nextNearestTileY)
					) {
					this.offsetX = 0;
					posX = (this.nextNearestTileX - 1) * TILE_SIZE + this.paddingRight;
					break;
				}
			}
		}

		if (this.offsetX < 0) {
			int y = 0;
			for (int x = 0; x >= this.offsetX; x--) {
				int aux = (int) (x * ratioYX);
				if (aux % TILE_SIZE == 0) {					
					y = aux;
				}
				this.calculateNextNearestTilesCoordinates(x, y);
				if (
					this.collisionMap.isEventTile(this.nextNearestTileX, this.nextTileY)
					||
					this.collisionMap.isEventTile(this.nextNearestTileX, this.nextNearestTileY)
					) {
					this.offsetX = 0;
					posX = (this.nextNearestTileX + 1) * TILE_SIZE - this.paddingLeft;
					break;
				}
			}
		}

		if (posX != 0) {
			this.positionX = posX;
		}
		if (posY != 0) {
			this.positionY = posY;
		}
		
	}
	
	private void calculateNextNearestTilesCoordinates(int offsetX, int offsetY) {
		this.calculateNextTile(offsetX, offsetY);
		this.nextNearestTileX = this.getNextNearestTileX(offsetX);
		this.nextNearestTileY = this.getNextNearestTileY(offsetY);
	}

	private int getNextNearestTileX(int offset) {
		if (this.positionX + offset + this.paddingLeft < this.nextTileX * TILE_SIZE) {
			return this.nextTileX - 1;
		}
		if (this.positionX + offset - this.paddingRight > this.nextTileX * TILE_SIZE) {
			return this.nextTileX + 1;
		}
		return this.nextTileX;
	}

	private int getNextNearestTileY(int offset) {
		if (this.positionY + offset + this.paddingTop < this.nextTileY * TILE_SIZE) {
			return this.nextTileY - 1;
		}
		if (this.positionY + offset - this.paddingBottom > this.nextTileY * TILE_SIZE) {
			return this.nextTileY + 1;
		}
		return this.nextTileY;
	}

	private void calculateNextTile(int offsetX, int offsetY) {
		this.nextTileX = this.roundTilePosition(this.positionX + offsetX + this.paddingLeft, this.paddingLeft + this.paddingRight);
		this.nextTileY = this.roundTilePosition(this.positionY + offsetY + this.paddingTop, this.paddingTop + this.paddingBottom);
	}

	protected void addAxisAlignedBoundingBox() {
		this.addComponent(new AxisAlignedBoundingBox(this));
	}
	
	protected void jump() {
		this.fallVelocity = this.jumpInitialVelocity;
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
	
	public TileEventMap getCollisionMap() {
		return collisionMap;
	}

	public void setCollisionMap(TileEventMap collisionMap) {
		this.collisionMap = collisionMap;
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
