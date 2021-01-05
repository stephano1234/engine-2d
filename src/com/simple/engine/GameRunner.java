package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class GameRunner {
	
	public static final int TILE_SIZE = 16;
	
	public static final int HALF_TILE_SIZE = TILE_SIZE / 2;
	
	private Engine engine;
	
	private Camera camera;

	private boolean isChangePhaseLogicalStep = true;
	
	private String currentPhaseTag;
	
	private Phase currentPhase;

	private List<Phase> phases = new ArrayList<>();
	
	private List<GameObject> gameObjects = new ArrayList<>();	
	
	protected GameRunner() {
		this.engine = new Engine();
		this.engine.setGameRunner(this);
	}

	protected abstract void setWindowConfig(Engine engine);	
	
	protected abstract void defineLogicalStep(Engine engine);
	
	protected abstract void defineFrame(Engine engine);
	
	public void run() {
		this.engine.start();
	}
	
	public void loopLogicalStep(Engine engine) {
				
		this.changePhaseStep();
		
		this.defineLogicalStep(engine);
		
		this.updateGameObjectsStates(engine.getInput());
		
		GamePhysic.updateAxisAlignedBoundingBox();
		
	}
	
	public void renderFrame(Engine engine) {

		this.defineFrame(engine);

		this.renderPhase(engine);
		
		this.renderGameObjects(engine.getRenderer());
	
	}

	private void renderGameObjects(Renderer renderer) {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.renderObject(renderer);
		}
	}
	
	private void updateGameObjectsStates(Input input) {
		int i = 0;
		while (i < this.gameObjects.size()) {			
			if (this.gameObjects.get(i).isDisabled()) {
				this.gameObjects.remove(i);
			} else {
				this.gameObjects.get(i).updateObjectState(input);
				i++;
			}
		}
	}

	private void renderPhase(Engine engine) {
		this.currentPhase.render(engine);
	}
	
	private void changePhaseStep() {
		if (this.isChangePhaseLogicalStep) {
			this.isChangePhaseLogicalStep = false;
			this.loadPhase();
		}
	}
	
	private void loadPhase() {
		this.currentPhase = this.getPhaseByTag(this.currentPhaseTag);
		TileEventMap collisionMap = this.currentPhase.getCollisionMap();
		for (GameObject gameObject : this.gameObjects) {
			gameObject.setCollisionMap(collisionMap);
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
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
		this.isChangePhaseLogicalStep = true;
		this.currentPhaseTag = currentPhaseTag;
	}

	public String getCurrentPhaseTag() {
		return currentPhaseTag;
	}

}
