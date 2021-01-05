package com.simple.game;

import static com.simple.engine.GameRunner.TILE_SIZE;

import java.awt.event.KeyEvent;

import com.simple.engine.Engine;
import com.simple.engine.GameObject;
import com.simple.engine.ImageTile;
import com.simple.engine.Input;
import com.simple.engine.Renderer;

public class Player extends GameObject {
	
	private ImageTile image = new ImageTile("/images/player.png", TILE_SIZE, TILE_SIZE);
	
	private int imgTileX = 0;
	
	private int imgTileY = 0;
	
	private int walkAnimationCounter = 0;
	
	private int walkAnimationDuration = 3;

	private int landAnimationCounter = 0;
	
	private int landAnimationDuration = 2;
	
	private boolean isLandingAnimation = false;
	
	public Player(int tileX, int tileY) {
		super("player", tileX, tileY, TILE_SIZE, TILE_SIZE, 4, 2, 6, 5);
		this.disabled = false;
		this.passThroughtCollision = false;
		this.isOnFloor = false;
		this.isUnderGravityEffect = true;
		this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 6f;
//		this.verticalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 6f;
		this.gravityAcceleration = Engine.UPDATE_CAP * TILE_SIZE;
		this.jumpInitialVelocity = -Engine.UPDATE_CAP * TILE_SIZE * 20f;
		this.addAxisAlignedBoundingBox();
	}

	@Override
	public void applyAxisAlignedBoundingBoxEvent(GameObject other) {
		if (this.offsetX != 0) {
			if (this.offsetX > 0) {
				this.positionX = other.getPositionX() - this.width + this.paddingRight;		
			} else {
				this.positionX = other.getPositionX() + other.getWidth() - this.paddingLeft;
			}
//			this.offsetX = 0;
		}
//		if (this.offsetY != 0) {
//			if (this.offsetY > 0) {
//				this.positionY = other.getPositionY() - this.height + this.paddingBottom;		
//				this.fallVelocity = 0;
//				this.isOnFloor = true;
//			} else {
//				this.positionY = other.getPositionY() + other.getHeight() - this.paddingTop;
//			}
//			this.offsetY = 0;
//		}
	}
	
	@Override
	public void applyControlEvents(Input input) {
		
		// calculate offsets

		if (input.isKeyDown(KeyEvent.VK_UP) && this.isOnFloor) {
			this.jump();
		}
		
//		if (engine.getInput().isKey(KeyEvent.VK_UP)) {
//			this.offsetY -= Math.round(this.verticalVelocity);
//		}
//
//		if (engine.getInput().isKey(KeyEvent.VK_DOWN)) {
//			this.offsetY += Math.round(this.verticalVelocity);
//		}

		if (input.isKey(KeyEvent.VK_LEFT)) {
			this.moveLeft();
		}

		if (input.isKey(KeyEvent.VK_RIGHT)) {
			this.moveRight();
		}
						
	}

	@Override
	public void applyObjectAnimation() {
		
		// walking and jump animation
		if (this.offsetX == 0) {
			if (this.isOnFloor) {				
				this.imgTileX = 0;			
			} else {
				this.imgTileX = 1;
			}
		} else {
			this.imgTileY = this.offsetX > 0 ? 1 : 0;
			if (this.isOnFloor) {
				if (this.walkAnimationCounter > this.walkAnimationDuration) {					
					this.imgTileX += 1;
					this.imgTileX %= 3;
					this.walkAnimationCounter = 0;
				} else {
					this.walkAnimationCounter++;
				}
			} else {
				this.imgTileX = 1;
			}			
		}
		
		// landing animation
		if ((!this.wasOnFloor && this.isOnFloor) || this.isLandingAnimation) {
			if (this.landAnimationCounter < this.landAnimationDuration) {					
				this.imgTileX = 3;
				this.landAnimationCounter++;
				this.isLandingAnimation = true;
			} else {
				this.landAnimationCounter = 0;
				this.isLandingAnimation = false;
			}			
		}

	}
	
	@Override
	public void renderObject(Renderer renderer) {
		renderer.drawImageTile(this.image, this.positionX, this.positionY, this.imgTileX, this.imgTileY);
	}

}
