package com.simple.engine;

public class Phase {

	private String tag;
	
	private Image landscape;
		
	public Phase(String tag) {
		this.tag = tag;
	}
	
	public void render(Renderer renderer) {
		if (this.landscape != null) {			
			renderer.setZDepth(Integer.MAX_VALUE);
			renderer.drawImage(this.landscape, 0, 0);		
		}
	}
	
	public void setLandscape(String path) {
		this.landscape = new Image(path);
	}
	
	public Image getLandscape() {
		return landscape;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
