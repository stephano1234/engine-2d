package com.simple.engine;

import java.awt.image.DataBufferInt;
import java.util.HashSet;
import java.util.Set;

public class Renderer {

	private int pixelWidth;

	private int pixelHeight;

	private int[] pixels;

	private int[] zBuffer;

	private int zDepth;

	private int[] lightMap;

	private boolean[] lightBlockMap;

	private Set<LightRenderRequest> lightRenderRequests;

	private int backgroundColor;

	private int ambientLightness;

	private int camX;

	private int camY;

	public Renderer(Engine engine) {
		this.pixelWidth = engine.getWidth();
		this.pixelHeight = engine.getHeight();
		this.pixels = ((DataBufferInt) engine.getWindow().getImage().getRaster().getDataBuffer()).getData();
		this.zBuffer = new int[this.pixels.length];
		this.zDepth = 0;
		this.lightMap = new int[this.pixels.length];
		this.lightBlockMap = new boolean[this.pixels.length];
		this.lightRenderRequests = new HashSet<>();
		this.backgroundColor = 0;
		this.ambientLightness = 0;
		this.camX = 0;
		this.camY = 0;
	}

	// getter and setter for pixel depth

	public int getZDepth() {
		return zDepth;
	}

	public void setZDepth(int zDepth) {
		this.zDepth = zDepth;
	}

	// getter and setter for background color and ambient lightness

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getAmbientLightness() {
		return ambientLightness;
	}

	public void setAmbientLightness(int ambientLightness) {
		this.ambientLightness = ambientLightness;
	}

	// reset all pixels information after rendering the next frame
	public void clear() {
		for (int i = 0; i < this.pixels.length; i++) {
			this.pixels[i] = this.backgroundColor;
			this.zBuffer[i] = Integer.MAX_VALUE;
			this.lightMap[i] = this.ambientLightness;
			this.lightBlockMap[i] = false;
		}
	}

	// set the camera offsets
	public void setCameraPosition(Camera camera) {
		camera.updateOffsets();
		this.camX = camera.getOffsetX() - this.pixelWidth / 2;
		this.camY = camera.getOffsetY() - this.pixelHeight / 2;
	}
	
	// draw the all the lights requested and process the lightness of each pixel
	public void enlight() {
		// draw the lights
		for (LightRenderRequest lightRenderRequest : this.lightRenderRequests) {
			for (int i = 0; i <= lightRenderRequest.light.getDiameter(); i++) {
				this.drawLightLine(lightRenderRequest.light, lightRenderRequest.offsetX, lightRenderRequest.offsetY,
						lightRenderRequest.light.getRadius(), lightRenderRequest.light.getRadius(), i, 0);
				this.drawLightLine(lightRenderRequest.light, lightRenderRequest.offsetX, lightRenderRequest.offsetY,
						lightRenderRequest.light.getRadius(), lightRenderRequest.light.getRadius(), i,
						lightRenderRequest.light.getDiameter());
				this.drawLightLine(lightRenderRequest.light, lightRenderRequest.offsetX, lightRenderRequest.offsetY,
						lightRenderRequest.light.getRadius(), lightRenderRequest.light.getRadius(), 0, i);
				this.drawLightLine(lightRenderRequest.light, lightRenderRequest.offsetX, lightRenderRequest.offsetY,
						lightRenderRequest.light.getRadius(), lightRenderRequest.light.getRadius(),
						lightRenderRequest.light.getDiameter(), i);
			}
		}
		// clear the list of requested lights to render
		this.lightRenderRequests.clear();
		// change the color of each pixel based on the its lightness
		for (int i = 0; i < this.pixels.length; i++) {
			float redLight = ((this.lightMap[i] >> 16) & 0xff) / 255f;
			float greenLight = ((this.lightMap[i] >> 8) & 0xff) / 255f;
			float blueLight = (this.lightMap[i] & 0xff) / 255f;
			this.pixels[i] = (this.pixels[i] & 0xff000000)
					| (((int) (((this.pixels[i] >> 16) & 0xff) * redLight)) << 16)
					| (((int) (((this.pixels[i] >> 8) & 0xff) * greenLight)) << 8)
					| ((int) ((this.pixels[i] & 0xff) * blueLight));
		}
	}

	// draw a ray of light (line format)
	private void drawLightLine(Light light, int offsetX, int offsetY, int x0, int y0, int x1, int y1) {
		int deltaX = x1 - x0;
		int deltaY = y1 - y0;
		int incrementalX = (deltaX > 0) ? 1 : -1;
		int incrementalY = (deltaY > 0) ? 1 : -1;
		deltaX = Math.abs(deltaX);
		deltaY = Math.abs(deltaY);
		float deltaError;
		float error = 0f;
		if (deltaX > deltaY) {
			deltaError = ((float) deltaY) / deltaX;
			int y = y0;
			for (int x = x0; x != x1; x += incrementalX) {
				int screenX = x + offsetX;
				int screenY = y + offsetY;
				if (screenX < 0 || screenX >= this.pixelWidth || screenY < 0 || screenY >= this.pixelHeight)
					return;
				if (this.lightBlockMap[screenX + screenY * this.pixelWidth])
					return;
				this.setPixelLight(screenX, screenY, light.getLightMap()[x + y * light.getDiameter()]);
				error += deltaError;
				if (error >= 0.5f) {
					y += incrementalY;
					error -= 1.0f;
				}
			}
		} else {
			deltaError = ((float) deltaX) / deltaY;
			int x = x0;
			for (int y = y0; y != y1; y += incrementalY) {
				int screenY = y + offsetY;
				int screenX = x + offsetX;
				if (screenX < 0 || screenX >= this.pixelWidth || screenY < 0 || screenY >= this.pixelHeight)
					return;
				if (this.lightBlockMap[screenX + screenY * this.pixelWidth])
					return;
				this.setPixelLight(screenX, screenY, light.getLightMap()[x + y * light.getDiameter()]);
				error += deltaError;
				if (error >= 0.5f) {
					x += incrementalX;
					error -= 1.0f;
				}
			}
		}
	}

	// set the lightness of the pixel
	private void setPixelLight(int x, int y, int value) {
		// calculate the index of the pixel in the vector that represents all the pixels
		// of the screen
		int oneDimPixelCoord = y * this.pixelWidth + x;
		// don't render pixel out of screen
		if ((x < 0 || x >= this.pixelWidth) || (y < 0 || y >= this.pixelHeight)) {
			return;
		}
		// get the actual pixel
		int actualLightValue = this.lightMap[oneDimPixelCoord];
		// set the lightness of the pixel (the blending rule overwrites one of the
		// pixel's RGB component color only if it's higher)
		this.lightMap[oneDimPixelCoord] = (Math.max((actualLightValue >> 16) & 0xff, (value >> 16) & 0xff) << 16)
				| (Math.max((actualLightValue >> 8) & 0xff, (value >> 8) & 0xff) << 8)
				| (Math.max(actualLightValue & 0xff, value & 0xff));
	}

	// set the darkness of the pixel
	private void setPixelLightBlock(int x, int y, boolean value) {
		// calculate the index of the pixel in the vector that represents all the pixels
		// of the screen
		int oneDimPixelCoord = y * this.pixelWidth + x;
		// don't render pixel out of screen
		if ((x < 0 || x >= this.pixelWidth) || (y < 0 || y >= this.pixelHeight)) {
			return;
		}
		// set the darkness
		this.lightBlockMap[oneDimPixelCoord] = value;
	}

	// set the color of the pixel based on the drawn elements
	private void setPixel(int x, int y, int value) {
		// extract the alpha value of the color integer (first 8 bit, without the
		// negative sign)
		int alpha = (value >> 24) & 0xff;
		// don't render pixel out of screen and if the integer color code has a maximum
		// transparency alpha (first 8 bit equals to 00000000)
		if (((x < 0 || x >= this.pixelWidth) || (y < 0 || y >= this.pixelHeight)) || alpha == 0) {
			return;
		}
		// calculate the index of the pixel in the vector that represents all the pixels
		// of the screen
		int oneDimPixelCoord = y * this.pixelWidth + x;
		// get the actual pixel alpha integer and set to zero in case of full opacity
		// (if it equals to 255)
		int actualPixelAlpha = (this.pixels[oneDimPixelCoord] >> 24) & 0xff;
		actualPixelAlpha = (actualPixelAlpha == 255) ? 0 : actualPixelAlpha;
		// don't render the pixel that has to appear under the actual pixel with full
		// opacity
		if (this.zBuffer[oneDimPixelCoord] < this.zDepth && actualPixelAlpha == 0) {
			return;
		}
		if (this.zBuffer[oneDimPixelCoord] < this.zDepth && actualPixelAlpha != 0) {
			// the opacity must be calculated by the alpha of the actual pixel
			float opacity = actualPixelAlpha / 255f;
			int mergedPixelAlpha = actualPixelAlpha + alpha;
			mergedPixelAlpha = (mergedPixelAlpha > 255) ? 255 : mergedPixelAlpha;
			int pixelColor = this.pixels[oneDimPixelCoord];
			int pixelRedColor = pixelColor >> 16 & 0xff;
			int pixelGreenColor = pixelColor >> 8 & 0xff;
			int pixelBlueColor = pixelColor & 0xff;
			this.pixels[oneDimPixelCoord] =
					// the actual pixel alpha is not being merged if the alpha of the new pixel
					// color has full opacity (alpha equals to 255)
					(((alpha == 255) ? actualPixelAlpha : mergedPixelAlpha) << 24)
							| (pixelRedColor - (int) ((pixelRedColor - ((value >> 16) & 0xff)) * (opacity))) << 16
							| (pixelGreenColor - (int) ((pixelGreenColor - ((value >> 8) & 0xff)) * (opacity))) << 8
							| (pixelBlueColor - (int) ((pixelBlueColor - (value & 0xff)) * (opacity)));
			return;
		}
		// update zBuffer pixel value
		this.zBuffer[oneDimPixelCoord] = this.zDepth;
		// render the full opacity pixel color or blender the alpha for transparency
		if (alpha == 255) {
			this.pixels[oneDimPixelCoord] = value;
			return;
		}
		int mergedPixelAlpha = actualPixelAlpha + alpha;
		mergedPixelAlpha = (mergedPixelAlpha > 255) ? 255 : mergedPixelAlpha;
		float opacity = alpha / 255f;
		int pixelColor = this.pixels[oneDimPixelCoord];
		int pixelRedColor = pixelColor >> 16 & 0xff;
		int pixelGreenColor = pixelColor >> 8 & 0xff;
		int pixelBlueColor = pixelColor & 0xff;
		this.pixels[oneDimPixelCoord] = ((mergedPixelAlpha) << 24)
				| (pixelRedColor - (int) ((pixelRedColor - ((value >> 16) & 0xff)) * (opacity))) << 16
				| (pixelGreenColor - (int) ((pixelGreenColor - ((value >> 8) & 0xff)) * (opacity))) << 8
				| (pixelBlueColor - (int) ((pixelBlueColor - (value & 0xff)) * (opacity)));
	}

	// Draw functions
	
	public void drawLight(Light light, int offsetX, int offsetY) {
		// adjusts the offsets based on the camera position
		offsetX -= this.camX;
		offsetY -= this.camY;
		// add the light and its position to the array of lights to be processed after
		// the elements drawn
		this.lightRenderRequests.add(new LightRenderRequest(light, offsetX, offsetY));
	}

	public void drawText(Font font, String text, int offsetX, int offsetY, int color) {
		// adjusts the offsets based on the camera position
		offsetX -= this.camX;
		offsetY -= this.camY;
		// change to inputed parameter text to upper case
		text = text.toUpperCase();
		// search for the letter and draw it
		int inLineOffset = 0;
		int letterIndex;
		for (int i = 0; i < text.length(); i++) {
			letterIndex = text.codePointAt(i) - 32;
			for (int x = 0; x < font.getWidths()[letterIndex]; x++) {
				for (int y = 0; y < font.getImage().getHeight(); y++) {
					if (font.getImage().getPixels()[(x + font.getOffsets()[letterIndex])
							+ y * font.getImage().getWidth()] == Font.RENDER_COLOR_CODE) {
						this.setPixel((x + inLineOffset) + offsetX, y + offsetY, color);
					}
				}
			}
			inLineOffset += font.getWidths()[letterIndex];
		}

	}

	public void drawFPS(Font font, int fps, int offsetX, int offsetY, int color) {
		// put the FPS above all the elements on the screen
		this.setZDepth(0);
		// write "FPS:[fps]"
		String text = "FPS:" + fps;
		// search for the letter and draw it
		int inLineOffset = 0;
		int letterIndex;
		for (int i = 0; i < text.length(); i++) {
			letterIndex = text.codePointAt(i) - 32;
			for (int x = 0; x < font.getWidths()[letterIndex]; x++) {
				for (int y = 0; y < font.getImage().getHeight(); y++) {
					if (font.getImage().getPixels()[(x + font.getOffsets()[letterIndex])
							+ y * font.getImage().getWidth()] == Font.RENDER_COLOR_CODE) {
						this.setPixel((x + inLineOffset) + offsetX, y + offsetY, color);
					}
				}
			}
			inLineOffset += font.getWidths()[letterIndex];
		}
	}
	
	public void drawImage(Image img, int offsetX, int offsetY) {
		// adjusts the offsets based on the camera position
		offsetX -= this.camX;
		offsetY -= this.camY;
		// validating image screen position
		if (offsetX < -img.getWidth())
			return;
		if (offsetY < -img.getHeight())
			return;
		if (offsetX >= this.pixelWidth)
			return;
		if (offsetY >= this.pixelHeight)
			return;
		// clipping image on screen size
		int renderWidth = img.getWidth();
		int renderHeight = img.getHeight();
		int renderInitialX = 0;
		int renderInitialY = 0;
		if (offsetX < 0)
			renderInitialX -= offsetX;
		if (offsetY < 0)
			renderInitialY -= offsetY;
		if (offsetX + renderWidth > this.pixelWidth)
			renderWidth = this.pixelWidth - offsetX;
		if (offsetY + renderHeight > this.pixelHeight)
			renderHeight = this.pixelHeight - offsetY;
		// draw pixels
		for (int x = renderInitialX; x < renderWidth; x++) {
			for (int y = renderInitialY; y < renderHeight; y++) {
				this.setPixel(x + offsetX, y + offsetY, img.getPixels()[y * img.getWidth() + x]);
				this.setPixelLightBlock(x + offsetX, y + offsetY, true);
			}
		}
	}

	public void drawImageTile(ImageTile img, int offsetX, int offsetY, int tileX, int tileY) {
		// adjusts the offsets based on the camera position
		offsetX -= this.camX;
		offsetY -= this.camY;
		// validating image screen position
		if (offsetX < -img.getTileWidth())
			return;
		if (offsetY < -img.getTileHeight())
			return;
		if (offsetX >= this.pixelWidth)
			return;
		if (offsetY >= this.pixelHeight)
			return;
		// clipping image on screen size
		int renderWidth = img.getTileWidth();
		int renderHeight = img.getTileHeight();
		int renderInitialX = 0;
		int renderInitialY = 0;
		if (offsetX < 0)
			renderInitialX -= offsetX;
		if (offsetY < 0)
			renderInitialY -= offsetY;
		if (offsetX + renderWidth > this.pixelWidth)
			renderWidth = this.pixelWidth - offsetX;
		if (offsetY + renderHeight > this.pixelHeight)
			renderHeight = this.pixelHeight - offsetY;
		// draw pixels
		for (int x = renderInitialX; x < renderWidth; x++) {
			for (int y = renderInitialY; y < renderHeight; y++) {
				this.setPixel(x + offsetX, y + offsetY,
						img.getPixels()[(y + tileY * img.getTileHeight()) * img.getWidth()
								+ (x + tileX * img.getTileWidth())]);
			}
		}
	}

	public void drawFilledRect(int width, int height, int color, int offsetX, int offsetY) {
		// adjusts the offsets based on the camera position
		offsetX -= this.camX;
		offsetY -= this.camY;
		// validating image screen position
		if (offsetX < -width)
			return;
		if (offsetY < -height)
			return;
		if (offsetX >= this.pixelWidth)
			return;
		if (offsetY >= this.pixelHeight)
			return;
		// clipping image on screen size
		int renderWidth = width;
		int renderHeight = height;
		int renderInitialX = 0;
		int renderInitialY = 0;
		if (offsetX < 0)
			renderInitialX -= offsetX;
		if (offsetY < 0)
			renderInitialY -= offsetY;
		if (offsetX + renderWidth > this.pixelWidth)
			renderWidth = this.pixelWidth - offsetX;
		if (offsetY + renderHeight > this.pixelHeight)
			renderHeight = this.pixelHeight - offsetY;
		// draw pixels
		for (int x = renderInitialX; x < renderWidth; x++) {
			for (int y = renderInitialY; y < renderHeight; y++) {
				this.setPixel(x + offsetX, y + offsetY, color);
			}
		}
	}

	public void drawRect(int width, int height, int strokeWidth, int color, int offsetX, int offsetY) {
		// adjusts the offsets based on the camera position
		offsetX -= this.camX;
		offsetY -= this.camY;
		// validating image screen position
		if (offsetX < -width)
			return;
		if (offsetY < -height)
			return;
		if (offsetX >= this.pixelWidth)
			return;
		if (offsetY >= this.pixelHeight)
			return;
		// clipping image on screen size
		int renderWidth = width;
		int renderHeight = height;
		int renderInitialX = 0;
		int renderInitialY = 0;
		if (offsetX < 0)
			renderInitialX -= offsetX;
		if (offsetY < 0)
			renderInitialY -= offsetY;
		if (offsetX + renderWidth > this.pixelWidth)
			renderWidth = this.pixelWidth - offsetX;
		if (offsetY + renderHeight > this.pixelHeight)
			renderHeight = this.pixelHeight - offsetY;
		// draw horizontal pixels
		for (int x = renderInitialX; x < renderWidth; x++) {
			for (int xWidth = 0; xWidth < strokeWidth; xWidth++) {
				this.setPixel(x + offsetX, xWidth + offsetY, color);
				this.setPixel(x + offsetX, xWidth + (height - strokeWidth) + offsetY, color);
			}
		}
		// draw vertical pixels
		for (int y = renderInitialY; y < renderHeight; y++) {
			for (int yWidth = 0; yWidth < strokeWidth; yWidth++) {
				this.setPixel(yWidth + offsetX, y + offsetY, color);
				this.setPixel(yWidth + (width - strokeWidth) + offsetX, y + offsetY, color);
			}
		}
	}

	public void drawFilledCircle(int radius, int color, int offsetX, int offsetY) {
		// adjusts the offsets based on the camera position
		offsetX -= this.camX;
		offsetY -= this.camY;
		// calculate diameter
		int width = radius * 2;
		int height = width;
		// validating image screen position
		if (offsetX < -width)
			return;
		if (offsetY < -height)
			return;
		if (offsetX >= this.pixelWidth)
			return;
		if (offsetY >= this.pixelHeight)
			return;
		// clipping image on screen size
		int renderWidth = width;
		int maxRenderHeight = height;
		int renderInitialX = 0;
		int renderInitialY = 0;
		if (offsetX < 0)
			renderInitialX -= offsetX;
		if (offsetY < 0)
			renderInitialY -= offsetY;
		if (offsetX + renderWidth > this.pixelWidth)
			renderWidth = this.pixelWidth - offsetX;
		if (offsetY + maxRenderHeight > this.pixelHeight)
			maxRenderHeight = this.pixelHeight - offsetY;
		// draw pixels
		int minY;
		int maxY;
		int renderHeight;
		int translatedX;
		for (int x = renderInitialX; x < renderWidth; x++) {
			translatedX = x - radius;
			minY = ((int) -Math.ceil(Math.sqrt((double) ((radius * radius) - (translatedX * translatedX))))) + radius;
			maxY = ((int) Math.ceil(Math.sqrt((double) ((radius * radius) - (translatedX * translatedX))))) + radius;
			renderHeight = (maxRenderHeight > maxY) ? maxY : maxRenderHeight;
			for (int y = (renderInitialY < minY) ? minY : renderInitialY; y < renderHeight; y++) {
				this.setPixel(x + offsetX, y + offsetY, color);
			}
		}
	}

	public void drawMinThicknessLine(int x0, int y0, int x1, int y1, int color) {
		// adjusts the offsets based on the camera position
		x0 -= this.camX;
		y0 -= this.camY;
		x1 -= this.camX;
		y1 -= this.camY;
		// apply line raster algorithm
		int deltaX = x1 - x0;
		int deltaY = y1 - y0;
		int incrementalX = (deltaX > 0) ? 1 : -1;
		int incrementalY = (deltaY > 0) ? 1 : -1;
		deltaX = Math.abs(deltaX);
		deltaY = Math.abs(deltaY);
		float deltaError;
		float error = 0f;
		if (deltaX > deltaY) {
			deltaError = ((float) deltaY) / deltaX;
			int y = y0;
			for (int x = x0; x != x1; x += incrementalX) {
				this.setPixel(x, y, color);
				error += deltaError;
				if (error >= 0.5f) {
					y += incrementalY;
					error -= 1.0f;
				}
			}
		} else {
			deltaError = ((float) deltaX) / deltaY;
			int x = x0;
			for (int y = y0; y != y1; y += incrementalY) {
				this.setPixel(x, y, color);
				error += deltaError;
				if (error >= 0.5f) {
					x += incrementalX;
					error -= 1.0f;
				}
			}
		}
	}

}
