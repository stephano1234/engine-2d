package com.simple.engine;

public abstract class Component {

	protected String tag;
	
	protected GameObject gameObject;

	protected Component(String tag, GameObject gameObject) {
		this.tag = tag;
		this.gameObject = gameObject;
	}
	
	public abstract void update();

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public GameObject getGameObject() {
		return gameObject;
	}

	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}

}
