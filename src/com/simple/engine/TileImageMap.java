package com.simple.engine;

public class TileImageMap {

	private boolean[] imageMap;
	
	private int width;
	
	private int height;
	
	private String tag;
	
	private Image image;
	
	public TileImageMap(String imageMapPath, String imagePath, String tag) {
		this.tag = tag;
		this.image = new Image(imagePath);
		Image imageMapImage = new Image(imageMapPath);
		this.width = imageMapImage.getWidth();
		this.height = imageMapImage.getHeight();
		this.imageMap = new boolean[imageMapImage.getWidth() * imageMapImage.getHeight()];
		for (int x = 0; x < imageMapImage.getWidth(); x++) {
			for (int y = 0; y < imageMapImage.getHeight(); y++) {
				this.imageMap[x + y * imageMapImage.getWidth()] = imageMapImage.getPixels()[x + y * imageMapImage.getWidth()] == 0xff000000;
			}
		}
	}

	public boolean isImageTile(int tileX, int tileY) {
		if (tileX < 0 || tileX >= this.width || tileY < 0 || tileY >= this.height) {
			return false;
		}
		return imageMap[tileX + tileY * this.width];
	}

	public void setImage(String path) {
		this.image = new Image(path);
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
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
