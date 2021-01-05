package com.simple.engine;

public class TileEventMap {

	public static final String COLLISION_TAG = "collision";
	
	private boolean[] eventMap;
	
	private int width;
	
	private int height;
	
	private String tag;
	
	public TileEventMap(String path, String tag) {
		this.tag = tag;
		Image eventMapImage = new Image(path);
		this.width = eventMapImage.getWidth();
		this.height = eventMapImage.getHeight();
		this.eventMap = new boolean[eventMapImage.getWidth() * eventMapImage.getHeight()];
		for (int x = 0; x < eventMapImage.getWidth(); x++) {
			for (int y = 0; y < eventMapImage.getHeight(); y++) {
				this.eventMap[x + y * eventMapImage.getWidth()] = eventMapImage.getPixels()[x + y * eventMapImage.getWidth()] == 0xff000000;
			}
		}
	}

	public boolean isEventTile(int tileX, int tileY) {
		if (tileX < 0 || tileX >= this.width || tileY < 0 || tileY >= this.height) {
			return false;
		}
		return eventMap[tileX + tileY * this.width];
	}
	
	public void mergeEventMap(TileEventMap tileEventMap) {
		for (int x = 0; x < tileEventMap.getWidth(); x++) {
			for (int y = 0; y < tileEventMap.getHeight(); y++) {
				this.eventMap[x + y * tileEventMap.getWidth()] = this.eventMap[x + y * tileEventMap.getWidth()] || tileEventMap.eventMap[x + y * tileEventMap.getWidth()];
			}
		}
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
