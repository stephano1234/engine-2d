package com.simple.engine;

public class Scenery {

	private String tag;
	
	private Image backgroundImage;
		
	public Scenery(String tag) {
		this.tag = tag;
	}
	
	public void render(Renderer renderer) {
		if (this.backgroundImage != null) {			
			renderer.setZDepth(Integer.MAX_VALUE);
			renderer.drawImage(this.backgroundImage, 0, 0, 0f, false);		
		}
	}
	
	public void setBackgroundImage(String path) {
		this.backgroundImage = new Image(path);
	}
	
	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
