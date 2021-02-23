package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameRunner {
	
	public static final int TILE_SIZE = 16;
	
	private Engine engine;
	
	private Camera camera;

	private boolean isChangePhaseUpdateStep = true;
	
	private String currentPhaseTag;
	
	private Phase currentPhase;

	private List<Phase> phases = new ArrayList<>();
	
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
				
		this.loadPhaseStep();
		
		this.defineGeneralEvents(input);
		
		this.processGameObjects(input);
		
	}
	
	public void renderGameFrame(Renderer renderer) {

		this.updateCameraPosition(renderer);
		
		this.defineFrame(renderer);

		this.renderPhase(renderer);
		
		this.renderGameObjects(renderer);
	
	}

	private void processGameObjects(Input input) {
		this.clearGameObjectsOffsets();
		this.updateGameObjectsOffsets(input);
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
	
	private void updateGameObjectsOffsets(Input input) {
		int i = 0;
		while (i < this.gameObjects.size()) {			
			if (this.gameObjects.get(i).isDisabled()) {
				this.gameObjects.remove(i);
			} else {
				this.gameObjects.get(i).updateOffsets(input);
				i++;
			}
		}
	}

	private void processGameObjectsBoundingAreasInteractions() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.processBoundingAreasInteractions();
		}
		CollisionDetector.resolveCollisions();
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

	private void renderPhase(Renderer renderer) {
		this.currentPhase.render(renderer);
	}
	
	private void loadPhaseStep() {
		if (this.isChangePhaseUpdateStep) {
			this.isChangePhaseUpdateStep = false;
			this.currentPhase = this.getPhaseByTag(this.currentPhaseTag);
		}
	}
	
	public Camera getCamera() {
		return camera;
	}

	public void setGameObjectFixedCamera(String tag) {
		this.camera = new Camera(tag, this);
	}

	private void updateCameraPosition(Renderer renderer) {
		renderer.setCameraPosition(this.getCamera());
	}
	
	public void addPhase(Phase phase) {
		this.phases.add(phase);
	}

	public Phase getPhaseByTag(String tag) {
		for (Phase phase : this.phases) {
			if (phase.getTag().equals(tag)) {
				return phase;
			}
		}
		return null;
	}

	public Phase getCurrentPhase() {
		return this.currentPhase;
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

	public void setCurrentPhaseTag(String currentPhaseTag) {
		this.isChangePhaseUpdateStep = true;
		this.currentPhaseTag = currentPhaseTag;
	}

	public String getCurrentPhaseTag() {
		return currentPhaseTag;
	}

}
