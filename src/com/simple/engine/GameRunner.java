package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameRunner {
	
	public static final int TILE_SIZE = 16;
	
	private Engine engine;
	
	private Camera camera = new Camera(0, 0);
	
	private List<GameObject> gameObjects = new ArrayList<>();	
	
	protected GameRunner() {
		this.engine = new Engine();
		this.engine.setGameRunner(this);
	}

	protected abstract void setWindowConfig(Engine engine);	
	
	protected abstract void defineGeneralEvents(Input input);
	
	protected abstract void defineFrame(Renderer renderer);
	
	public void run() {
		this.engine.start();
	}
	
	public void processGameEvents(Input input) {
		
		this.defineGeneralEvents(input);
		
		this.processGameObjects(input);
		
	}
	
	public void renderGameFrame(Renderer renderer) {

		renderer.setCameraPosition();
		
		this.defineFrame(renderer);
		
		this.renderGameObjects(renderer);
	
	}

	private void processGameObjects(Input input) {
		this.clearGameObjectsOffsets();
		this.processGameObjectsOffsetsChanges(input);
		this.synchronizeAttachedGameObjectsOffsetsAndAngles();
		this.processGameObjectsBoundingAreasInteractions();
		this.updateGameObjectsPositionAndAngle();
		this.processGameObjectsImagesAnimations(input);
	}
	
	private void renderGameObjects(Renderer renderer) {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.renderObject(renderer);
		}
	}
	
	private void clearGameObjectsOffsets() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.clearOffsets();
		}
	}
	
	private void processGameObjectsOffsetsChanges(Input input) {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.processOffsetsChanges(input);
		}
	}

	private void processGameObjectsBoundingAreasInteractions() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.processCollidingBoundingAreasInteractions();
		}
		BoundingAreasInteractionsResolver.resolveCollisionInteractions();
		for (GameObject gameObject : this.gameObjects) {
			gameObject.processIntersectingBoundingAreasInteractions();
		}
		BoundingAreasInteractionsResolver.resolveIntersectionInteractions();
	}

	private void processGameObjectsImagesAnimations(Input input) {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.processImageAnimations(input);
		}
	}

	private void synchronizeAttachedGameObjectsOffsetsAndAngles() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.synchronizeOffsetAndAngleWithAttachedGameObject();
		}
	}
	
	private void updateGameObjectsPositionAndAngle() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.updatePositionAndAngle();
		}
	}
	
	public Camera getCamera() {
		return camera;
	}

	public void setGameObjectFixedCamera(String tag) {
		this.camera.setTarget(this.getGameObjectByTag(tag));
	}
	
	public void addGameObject(GameObject gameObject) {
		this.gameObjects.add(gameObject);
	}
	
	public GameObject getGameObjectByTag(String tag) {
		for (GameObject gameObject : this.gameObjects) {
			if (gameObject.getTag().equals(tag)) {
				return gameObject;
			}
		}
		return null;
	}

}
