package com.simple.engine;

public class Light {
	
	private int radius;
	
	private int diameter;
	
	private int[] lightMap;
	
	public Light(int radius, int color) {
				
		this.radius = radius;
		
		this.diameter = radius * 2;
		
		this.lightMap = new int[this.diameter * this.diameter];
		
		double distance;
		int translatedX;
		int translatedY;
		double intensity;
		
		for (int y = 0; y < this.diameter; y++) {
			for (int x = 0; x < this.diameter; x++) {
				
				translatedX = x - radius;
				translatedY = y - radius;
				distance = Math.sqrt((double) ((translatedX * translatedX) + (translatedY * translatedY)));
				
				if (distance < radius) {
					intensity = 1 - (distance / radius);
					this.lightMap[x + y * this.diameter] = 
						(((int) (((color >> 16) & 0xff) * intensity)) << 16)
						|
						(((int) (((color >> 8) & 0xff) * intensity)) << 8)
						|
						((int) ((color & 0xff) * intensity))
					;

				} else {
					// set no light if the pixel is out of the circle of light
					this.lightMap[x + y * this.diameter] = 0;
				}
			}
		}
		
	}
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getDiameter() {
		return diameter;
	}

	public void setDiameter(int diameter) {
		this.diameter = diameter;
	}

	public int[] getLightMap() {
		return lightMap;
	}

	public void setLightMap(int[] lightMap) {
		this.lightMap = lightMap;
	}
	
}
