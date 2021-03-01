package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameRunner {
	
	public static final int TILE_SIZE = 16;
	
	private Engine engine;
	
	private Camera camera = new Camera(0, 0);

	private boolean isChangeSceneryUpdateStep = true;
	
	private String currentSceneryTag;
	
	private Scenery currentScenery;

	private List<Scenery> sceneries = new ArrayList<>();
	
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
				
		this.loadSceneryStep();
		
		this.defineGeneralEvents(input);
		
		this.processGameObjects(input);
		
	}
	
	public void renderGameFrame(Renderer renderer) {

		renderer.setCameraPosition();
		
		this.defineFrame(renderer);

		this.renderScenery(renderer);
		
		this.renderGameObjects(renderer);
	
	}

	private void processGameObjects(Input input) {
		this.clearGameObjectsOffsets();
		this.processGameObjectsOffsetsChanges(input);
		this.synchronizeAttachedGameObjectsOffsets();
		this.processGameObjectsBoundingAreasInteractions();
		this.processGameObjectsImagesAnimations(input);
		this.updateGameObjectsPosition();
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
			gameObject.processBoundingAreasInteractions();
		}
		BoundingAreasInteractionsResolver.resolveCollisionInteractions();
	}

	private void processGameObjectsImagesAnimations(Input input) {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.processImageAnimations(input);
		}
	}

	private void synchronizeAttachedGameObjectsOffsets() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.synchronizeOffsetWithAttachedGameObject();
		}
	}
	
	private void updateGameObjectsPosition() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.updatePosition();
		}
	}

	private void renderScenery(Renderer renderer) {
		if (this.currentScenery != null) {			
			this.currentScenery.render(renderer);
		}
	}
	
	private void loadSceneryStep() {
		if (this.isChangeSceneryUpdateStep) {
			this.isChangeSceneryUpdateStep = false;
			this.currentScenery = this.getSceneryByTag(this.currentSceneryTag);
		}
	}
	
	public Camera getCamera() {
		return camera;
	}

	public void setGameObjectFixedCamera(String tag) {
		this.camera.setTarget(this.getGameObjectByTag(tag));
	}
	
	public void addScenery(Scenery scenery) {
		this.sceneries.add(scenery);
	}

	public Scenery getSceneryByTag(String tag) {
		for (Scenery scenery : this.sceneries) {
			if (scenery.getTag().equals(tag)) {
				return scenery;
			}
		}
		return null;
	}

	public Scenery getCurrentScenery() {
		return this.currentScenery;
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

	public void setCurrentSceneryTag(String currentPhaseTag) {
		this.isChangeSceneryUpdateStep = true;
		this.currentSceneryTag = currentPhaseTag;
	}

	public String getCurrentSceneryTag() {
		return currentSceneryTag;
	}

}
