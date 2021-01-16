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
	
	private GameObject belowMovingGameObject;
	
	public Player(int positionX, int positionY) {
		super("player", positionX, positionY);
		this.isOnFloor = false;
		this.isUnderGravityEffect = true;
		this.horizontalVelocity = Engine.UPDATE_CAP * TILE_SIZE * 6f;
		this.gravityAcceleration = Engine.UPDATE_CAP * TILE_SIZE * 1f;
		this.jumpInitialVelocity = -Engine.UPDATE_CAP * TILE_SIZE * 20f;
		this.width = TILE_SIZE;
		this.height = TILE_SIZE;
		this.paddingTop = 4;
		this.paddingBottom = 2;
		this.paddingLeft = 6;
		this.paddingRight = 5;
		this.addAxisAlignedBoundingBox();
	}

	@Override
	public void applyAxisAlignedBoundingBoxEvent(GameObject other) {
		int disX = 0;
		int disY = 0;
		if (this.positionY <= other.getPositionY()) {
			disY = other.getPositionY() + other.getOffsetY() - (this.positionY + this.offsetY + this.height - this.paddingBottom);
		} else {
			disY = other.getPositionY() + other.getOffsetY() + other.getHeight() - (this.positionY + this.offsetY + this.paddingTop);
		}
		if (this.positionX <= other.getPositionX()) {
			disX = other.getPositionX() + other.getOffsetX() - (this.positionX + this.offsetX + this.width - this.paddingRight);			
		} else {
			disX = other.getPositionX() + other.getOffsetX() + other.getWidth() - (this.positionX + this.offsetX + this.paddingLeft);			
		}
		if (Math.abs(disX) < Math.abs(disY)) {
			disX += this.offsetX;
			this.offsetX = disX;
		} else {
			if (this.positionY <= other.getPositionY()) {
				this.belowMovingGameObject = other;
				this.isOnFloor = true;
				this.fallVelocity = this.fallVelocity > 0 ? 0 : this.fallVelocity;
			} else {
				this.fallVelocity = this.fallVelocity < 0 ? 0 : this.fallVelocity;
			}
			disY += this.offsetY;
			this.offsetY = disY;
		}
	}
	
	@Override
	public void applyControlEvents(Input input) {
		
		// calculate offsets

		if (input.isKeyDown(KeyEvent.VK_UP) && this.isOnFloor) {
			this.jump();
		}
		
		if (input.isKey(KeyEvent.VK_LEFT)) {
			this.moveLeft();
		}

		if (input.isKey(KeyEvent.VK_RIGHT)) {
			this.moveRight();
		}
						
	}

	@Override
	public void updateObjectAnimation(Input input) {
		
		// walking and jump animation
		if (!input.isKey(KeyEvent.VK_LEFT) && !input.isKey(KeyEvent.VK_RIGHT)) {
			if (this.isOnFloor) {				
				this.imgTileX = 0;			
			} else {
				this.imgTileX = 1;
			}
		} else {
			this.imgTileY = input.isKey(KeyEvent.VK_RIGHT) ? 1 : 0;
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

		// follow the below moving game object
		if (this.belowMovingGameObject != null && this.isOnFloor) {
//			System.out.println(this.belowMovingGameObject.getTag());
			this.offsetX += this.belowMovingGameObject.getOffsetX();
			this.offsetY += this.belowMovingGameObject.getOffsetY();
		}

	}
	
	@Override
	public void renderObject(Renderer renderer) {
		renderer.drawImageTile(this.image, this.positionX, this.positionY, this.imgTileX, this.imgTileY);
	}

}
