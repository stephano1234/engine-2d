package com.simple.engine;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {

	private int width;
	
	private int height;
	
	private int[] pixels;
	
	public Image(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(Image.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (img != null) {			
			this.width = img.getWidth();
			this.height = img.getHeight();
			this.pixels = img.getRGB(0, 0, width, height, null, 0, width);
			img.flush();
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

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}
	
}
