package com.simple.engine;

class LightRenderRequest {

	public Light light;
	
	public int offsetX;
	
	public int offsetY;
	
	public LightRenderRequest(Light light, int offsetX, int offsetY) {
		this.light = light;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
}
