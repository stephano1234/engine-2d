package com.simple.engine;

import java.awt.image.DataBufferInt;
import java.util.HashSet;
import java.util.Set;

public class Renderer {

	private int pixelsWidth;

	private int pixelsHeight;

	private int halfPixelsWidth;

	private int halfPixelsHeight;

	private int[] pixelsColor;

	private int[] zBuffer;

	private int zDepth = 0;

	private int[] lightMap;

	private boolean[] lightBlockMap;

	private Set<LightRenderRequest> lightRenderRequests = new HashSet<>();

	private int backgroundColor = 0;

	private int ambientLightness = 0;

	private Camera camera;
	
	private int camX = 0;

	private int camY = 0;

	public Renderer(Engine engine) {
		this.pixelsWidth = engine.getWidth();
		this.pixelsHeight = engine.getHeight();
		this.halfPixelsWidth = this.pixelsWidth / 2;
		this.halfPixelsHeight = this.pixelsHeight / 2;
		this.pixelsColor = ((DataBufferInt) engine.getWindow().getImage().getRaster().getDataBuffer()).getData();
		this.zBuffer = new int[this.pixelsColor.length];
		this.lightMap = new int[this.pixelsColor.length];
		this.lightBlockMap = new boolean[this.pixelsColor.length];
		this.camera = engine.getGameRunner().getCamera();
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
		for (int i = 0; i < this.pixelsColor.length; i++) {
			this.pixelsColor[i] = this.backgroundColor;
			this.zBuffer[i] = Integer.MAX_VALUE;
			this.lightMap[i] = this.ambientLightness;
			this.lightBlockMap[i] = false;
		}
	}

	// set the camera position
	public void setCameraPosition() {
		this.camera.updatePosition();
		this.camX = this.camera.getPosition().getX() - this.halfPixelsWidth;
		this.camY = this.camera.getPosition().getY() - this.halfPixelsHeight;
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
		for (int i = 0; i < this.pixelsColor.length; i++) {
			float redLight = ((this.lightMap[i] >> 16) & 0xff) / 255f;
			float greenLight = ((this.lightMap[i] >> 8) & 0xff) / 255f;
			float blueLight = (this.lightMap[i] & 0xff) / 255f;
			this.pixelsColor[i] = (this.pixelsColor[i] & 0xff000000)
					| (((int) (((this.pixelsColor[i] >> 16) & 0xff) * redLight)) << 16)
					| (((int) (((this.pixelsColor[i] >> 8) & 0xff) * greenLight)) << 8)
					| ((int) ((this.pixelsColor[i] & 0xff) * blueLight));
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
				if (!(screenX < 0 || screenX >= this.pixelsWidth || screenY < 0 || screenY >= this.pixelsHeight)) {					
					if (this.lightBlockMap[screenX + screenY * this.pixelsWidth])
						return;
					this.setPixelLight(screenX, screenY, light.getLightMap()[x + y * light.getDiameter()]);
				}
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
				if (!(screenX < 0 || screenX >= this.pixelsWidth || screenY < 0 || screenY >= this.pixelsHeight)) {					
					if (this.lightBlockMap[screenX + screenY * this.pixelsWidth])
						return;
					this.setPixelLight(screenX, screenY, light.getLightMap()[x + y * light.getDiameter()]);
				}
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
		int oneDimPixelCoord = y * this.pixelsWidth + x;
		// don't render pixel out of screen
		if ((x < 0 || x >= this.pixelsWidth) || (y < 0 || y >= this.pixelsHeight)) {
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
		int oneDimPixelCoord = y * this.pixelsWidth + x;
		// don't render pixel out of screen
		if ((x < 0 || x >= this.pixelsWidth) || (y < 0 || y >= this.pixelsHeight)) {
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
		if (((x < 0 || x >= this.pixelsWidth) || (y < 0 || y >= this.pixelsHeight)) || alpha == 0) {
			return;
		}
		// calculate the index of the pixel in the vector that represents all the pixels
		// of the screen
		int oneDimPixelCoord = y * this.pixelsWidth + x;
		// get the actual pixel alpha integer and set to zero in case of full opacity
		// (if it equals to 255)
		int actualPixelAlpha = (this.pixelsColor[oneDimPixelCoord] >> 24) & 0xff;
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
			int pixelColor = this.pixelsColor[oneDimPixelCoord];
			int pixelRedColor = pixelColor >> 16 & 0xff;
			int pixelGreenColor = pixelColor >> 8 & 0xff;
			int pixelBlueColor = pixelColor & 0xff;
			this.pixelsColor[oneDimPixelCoord] =
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
			this.pixelsColor[oneDimPixelCoord] = value;
			return;
		}
		int mergedPixelAlpha = actualPixelAlpha + alpha;
		mergedPixelAlpha = (mergedPixelAlpha > 255) ? 255 : mergedPixelAlpha;
		float opacity = alpha / 255f;
		int pixelColor = this.pixelsColor[oneDimPixelCoord];
		int pixelRedColor = pixelColor >> 16 & 0xff;
		int pixelGreenColor = pixelColor >> 8 & 0xff;
		int pixelBlueColor = pixelColor & 0xff;
		this.pixelsColor[oneDimPixelCoord] = ((mergedPixelAlpha) << 24)
				| (pixelRedColor - (int) ((pixelRedColor - ((value >> 16) & 0xff)) * (opacity))) << 16
				| (pixelGreenColor - (int) ((pixelGreenColor - ((value >> 8) & 0xff)) * (opacity))) << 8
				| (pixelBlueColor - (int) ((pixelBlueColor - (value & 0xff)) * (opacity)));
	}

	/* light */

	public void drawLight(Light light, int pixelX, int pixelY) {
		// adjusts the offsets based on the camera position
		pixelX -= this.camX;
		pixelY -= this.camY;
		// add the light and its position to the array of lights to be processed after
		// the elements drawn
		this.lightRenderRequests.add(new LightRenderRequest(light, pixelX - light.getRadius(), pixelY - light.getRadius()));
	}

	/* images */
	
	public void drawImage(Image img, int pixelX, int pixelY, float angle, boolean blockLight) {
		// adjusts the offsets based on the camera position
		pixelX -= this.camX;
		pixelY -= this.camY;
		int width = img.getWidth();
		int height = img.getHeight();
		// get the rotational matrix and its inverse
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		double[][] rotationMatrix = Calculator.getRotationMatrix(angle, new Coordinate(halfWidth, halfHeight));
		double[][] inverseRotationMatrix = Calculator.getInverse3x3Matrix(rotationMatrix);
		// stock the target render area
		int targetWidth = width;
		int targetHeight = height;
		// calculate the enlarged render area
		int rotX1 = Calculator.get3x3MatrixProductX(rotationMatrix, 0, 0);
		int rotY1 = Calculator.get3x3MatrixProductY(rotationMatrix, 0, 0);
		int rotX2 = Calculator.get3x3MatrixProductX(rotationMatrix, width, 0);
		int rotY2 = Calculator.get3x3MatrixProductY(rotationMatrix, width, 0);
		int rotX3 = Calculator.get3x3MatrixProductX(rotationMatrix, 0, height);
		int rotY3 = Calculator.get3x3MatrixProductY(rotationMatrix, 0, height);
		int rotX4 = Calculator.get3x3MatrixProductX(rotationMatrix, width, height);
		int rotY4 = Calculator.get3x3MatrixProductY(rotationMatrix, width, height);
		int minRotatedCoordX = Math.min(Math.min(rotX1, rotX2), Math.min(rotX3, rotX4));
		int minRotatedCoordY = Math.min(Math.min(rotY1, rotY2), Math.min(rotY3, rotY4));
		int maxRotatedCoordX = Math.max(Math.max(rotX1, rotX2), Math.max(rotX3, rotX4));
		int maxRotatedCoordY = Math.max(Math.max(rotY1, rotY2), Math.max(rotY3, rotY4));
		width = Math.abs(minRotatedCoordX - maxRotatedCoordX);
		height = Math.abs(minRotatedCoordY - maxRotatedCoordY);
		// validating image screen position
		if (pixelX < -width)
			return;
		if (pixelY < -height)
			return;
		if (pixelX >= this.pixelsWidth)
			return;
		if (pixelY >= this.pixelsHeight)
			return;
		// clipping image on screen size
		int renderWidth = width + minRotatedCoordX;
		int renderHeight = height + minRotatedCoordY;
		int renderInitialX = minRotatedCoordX;
		int renderInitialY = minRotatedCoordY;
		if (pixelX < 0)
			renderInitialX -= pixelX;
		if (pixelY < 0)
			renderInitialY -= pixelY;
		if (pixelX + renderWidth > this.pixelsWidth)
			renderWidth = this.pixelsWidth - pixelX;
		if (pixelY + renderHeight > this.pixelsHeight)
			renderHeight = this.pixelsHeight - pixelY;
		// draw pixels
		for (int x = renderInitialX; x < renderWidth; x++) {
			for (int y = renderInitialY; y < renderHeight; y++) {
				int targetX = Calculator.get3x3MatrixProductX(inverseRotationMatrix, x, y);
				int targetY = Calculator.get3x3MatrixProductY(inverseRotationMatrix, x, y);
				if (targetX >= 0 && targetX < targetWidth && targetY >= 0 && targetY < targetHeight) {					
					this.setPixel(x + pixelX, y + pixelY, img.getPixels()[targetY * targetWidth + targetX]);
					this.setPixelLightBlock(x + pixelX, y + pixelY, blockLight);
				}
			}
		}
	}

	public void drawImageTile(ImageTile img, int pixelX, int pixelY, int tileX, int tileY, float angle, boolean blockLight) {
		// adjusts the offsets based on the camera position
		pixelX -= this.camX;
		pixelY -= this.camY;
		// validating image screen position
		if (pixelX < -img.getTileWidth())
			return;
		if (pixelY < -img.getTileHeight())
			return;
		if (pixelX >= this.pixelsWidth)
			return;
		if (pixelY >= this.pixelsHeight)
			return;
		// clipping image on screen size
		int renderWidth = img.getTileWidth();
		int renderHeight = img.getTileHeight();
		int renderInitialX = 0;
		int renderInitialY = 0;
		if (pixelX < 0)
			renderInitialX -= pixelX;
		if (pixelY < 0)
			renderInitialY -= pixelY;
		if (pixelX + renderWidth > this.pixelsWidth)
			renderWidth = this.pixelsWidth - pixelX;
		if (pixelY + renderHeight > this.pixelsHeight)
			renderHeight = this.pixelsHeight - pixelY;
		// draw pixels
		for (int x = renderInitialX; x < renderWidth; x++) {
			for (int y = renderInitialY; y < renderHeight; y++) {
				this.setPixel(x + pixelX, y + pixelY,
						img.getPixels()[(y + tileY * img.getTileHeight()) * img.getWidth()
								+ (x + tileX * img.getTileWidth())]);
				this.setPixelLightBlock(x + pixelX, y + pixelY, blockLight);
			}
		}
	}

	/* areas */
	
	public void drawRectArea(int width, int height, int color, int pixelX, int pixelY, float angle, boolean blockLight) {
		// adjusts the offsets based on the camera position
		pixelX -= this.camX;
		pixelY -= this.camY;
		// get the rotational matrix and its inverse
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		double[][] rotationMatrix = Calculator.getRotationMatrix(angle, new Coordinate(halfWidth, halfHeight));
		double[][] inverseRotationMatrix = Calculator.getInverse3x3Matrix(rotationMatrix);
		// stock the target render area
		int targetWidth = width;
		int targetHeight = height;
		// calculate the enlarged render area
		int rotX1 = Calculator.get3x3MatrixProductX(rotationMatrix, 0, 0);
		int rotY1 = Calculator.get3x3MatrixProductY(rotationMatrix, 0, 0);
		int rotX2 = Calculator.get3x3MatrixProductX(rotationMatrix, width, 0);
		int rotY2 = Calculator.get3x3MatrixProductY(rotationMatrix, width, 0);
		int rotX3 = Calculator.get3x3MatrixProductX(rotationMatrix, 0, height);
		int rotY3 = Calculator.get3x3MatrixProductY(rotationMatrix, 0, height);
		int rotX4 = Calculator.get3x3MatrixProductX(rotationMatrix, width, height);
		int rotY4 = Calculator.get3x3MatrixProductY(rotationMatrix, width, height);
		int minRotatedCoordX = Math.min(Math.min(rotX1, rotX2), Math.min(rotX3, rotX4));
		int minRotatedCoordY = Math.min(Math.min(rotY1, rotY2), Math.min(rotY3, rotY4));
		int maxRotatedCoordX = Math.max(Math.max(rotX1, rotX2), Math.max(rotX3, rotX4));
		int maxRotatedCoordY = Math.max(Math.max(rotY1, rotY2), Math.max(rotY3, rotY4));
		width = Math.abs(minRotatedCoordX - maxRotatedCoordX);
		height = Math.abs(minRotatedCoordY - maxRotatedCoordY);
		// validating image screen position
		if (pixelX < -width)
			return;
		if (pixelY < -height)
			return;
		if (pixelX >= this.pixelsWidth)
			return;
		if (pixelY >= this.pixelsHeight)
			return;
		// clipping image on screen size
		int renderWidth = width + minRotatedCoordX;
		int renderHeight = height + minRotatedCoordY;
		int renderInitialX = minRotatedCoordX;
		int renderInitialY = minRotatedCoordY;
		if (pixelX < 0)
			renderInitialX -= pixelX;
		if (pixelY < 0)
			renderInitialY -= pixelY;
		if (pixelX + renderWidth > this.pixelsWidth)
			renderWidth = this.pixelsWidth - pixelX;
		if (pixelY + renderHeight > this.pixelsHeight)
			renderHeight = this.pixelsHeight - pixelY;
		// draw pixels
		for (int x = renderInitialX; x <= renderWidth; x++) {
			for (int y = renderInitialY; y <= renderHeight; y++) {
				int targetX = Calculator.get3x3MatrixProductX(inverseRotationMatrix, x, y);
				int targetY = Calculator.get3x3MatrixProductY(inverseRotationMatrix, x, y);
				if (targetX >= 0 && targetX < targetWidth && targetY >= 0 && targetY < targetHeight) {					
					this.setPixel(x + pixelX, y + pixelY, color);
					this.setPixelLightBlock(x + pixelX, y + pixelY, blockLight);
				}
			}
		}
	}

	public void drawTriangleRectangleArea(int width, int height, int color, int pixelX, int pixelY, int mode, float angle, boolean blockLight) {
		// adjusts the offsets based on the camera position
		pixelX -= this.camX;
		pixelY -= this.camY;
		// get the rotational matrix and its inverse
		double[][] rotationMatrix = null;
		switch (mode) {
		case 0:
			rotationMatrix = Calculator.getRotationMatrix(angle, new Coordinate(
						(int) Math.round((width * 2) / 3.0), 
						(int) Math.round(height / 3.0)
					)
				)
			;
			break;
		case 1:
			rotationMatrix = Calculator.getRotationMatrix(-angle, new Coordinate(
						(int) Math.round(width / 3.0), 
						(int) Math.round((height * 2) / 3.0)
					)
				)
			;
			break;
		case 2:
			rotationMatrix = Calculator.getRotationMatrix(angle, new Coordinate(
						(int) Math.round(width / 3.0), 
						(int) Math.round((height * 2) / 3.0)
					)
				)
			;
			break;
		case 3:
			rotationMatrix = Calculator.getRotationMatrix(-angle, new Coordinate(
						(int) Math.round((width * 2) / 3.0), 
						(int) Math.round(height / 3.0)
					)
				)
			;
			break;
		default:
			// put a log of error, because no available mode was chosen
			break;
		}
		double[][] inverseRotationMatrix = Calculator.getInverse3x3Matrix(rotationMatrix);
		// stock the target render area
		int targetWidth = width;
		int targetHeight = height;
		// calculate the enlarged render area
		int rotX1 = Calculator.get3x3MatrixProductX(rotationMatrix, 0, 0);
		int rotY1 = Calculator.get3x3MatrixProductY(rotationMatrix, 0, 0);
		int rotX2 = Calculator.get3x3MatrixProductX(rotationMatrix, width, 0);
		int rotY2 = Calculator.get3x3MatrixProductY(rotationMatrix, width, 0);
		int rotX3 = Calculator.get3x3MatrixProductX(rotationMatrix, 0, height);
		int rotY3 = Calculator.get3x3MatrixProductY(rotationMatrix, 0, height);
		int rotX4 = Calculator.get3x3MatrixProductX(rotationMatrix, width, height);
		int rotY4 = Calculator.get3x3MatrixProductY(rotationMatrix, width, height);
		int minRotatedCoordX = Math.min(Math.min(rotX1, rotX2), Math.min(rotX3, rotX4));
		int minRotatedCoordY = Math.min(Math.min(rotY1, rotY2), Math.min(rotY3, rotY4));
		int maxRotatedCoordX = Math.max(Math.max(rotX1, rotX2), Math.max(rotX3, rotX4));
		int maxRotatedCoordY = Math.max(Math.max(rotY1, rotY2), Math.max(rotY3, rotY4));
		width = Math.abs(minRotatedCoordX - maxRotatedCoordX);
		height = Math.abs(minRotatedCoordY - maxRotatedCoordY);
		// validating image screen position
		if (pixelX < -width)
			return;
		if (pixelY < -height)
			return;
		if (pixelX >= this.pixelsWidth)
			return;
		if (pixelY >= this.pixelsHeight)
			return;
		// clipping image on screen size
		int renderWidth = width + minRotatedCoordX;
		int renderHeight = height + minRotatedCoordY;
		int renderInitialX = minRotatedCoordX;
		int renderInitialY = minRotatedCoordY;
		if (pixelX < 0)
			renderInitialX -= pixelX;
		if (pixelY < 0)
			renderInitialY -= pixelY;
		if (pixelX + renderWidth > this.pixelsWidth)
			renderWidth = this.pixelsWidth - pixelX;
		if (pixelY + renderHeight > this.pixelsHeight)
			renderHeight = this.pixelsHeight - pixelY;
		// draw pixels
		float c = (float) height / width;
		switch (mode) {
		case 0:
			for (int x = renderInitialX; x < renderWidth; x++) {
				for (int y = renderInitialY; y < renderHeight; y++) {
					int targetX = Calculator.get3x3MatrixProductX(inverseRotationMatrix, x, y);
					int targetY = Calculator.get3x3MatrixProductY(inverseRotationMatrix, x, y);
					if (targetX >= 0 && targetX < targetWidth && targetY >= 0 && targetY < targetHeight && targetY <= targetX * c) {										
						this.setPixel(x + pixelX, y + pixelY, color);
						this.setPixelLightBlock(x + pixelX, y + pixelY, blockLight);
					}
				}
			}			
			break;
		case 1:
			for (int x = renderInitialX; x < renderWidth; x++) {
				for (int y = renderInitialY; y < renderHeight; y++) {
					int targetX = Calculator.get3x3MatrixProductX(inverseRotationMatrix, x, y);
					int targetY = Calculator.get3x3MatrixProductY(inverseRotationMatrix, x, y);
					if (targetX >= 0 && targetX < targetWidth && targetY >= 0 && targetY < targetHeight && targetY >= targetX * c) {																
						this.setPixel((targetWidth - 1) - x + pixelX, y + pixelY, color);
						this.setPixelLightBlock((targetWidth - 1) - x + pixelX, y + pixelY, blockLight);
					}
				}
			}			
			break;
		case 2:
			for (int x = renderInitialX; x < renderWidth; x++) {
				for (int y = renderInitialY; y < renderHeight; y++) {
					int targetX = Calculator.get3x3MatrixProductX(inverseRotationMatrix, x, y);
					int targetY = Calculator.get3x3MatrixProductY(inverseRotationMatrix, x, y);
					if (targetX >= 0 && targetX < targetWidth && targetY >= 0 && targetY < targetHeight && targetY >= targetX * c) {																
						this.setPixel(x + pixelX, y + pixelY, color);
						this.setPixelLightBlock(x + pixelX, y + pixelY, blockLight);
					}
				}
			}			
			break;
		case 3:
			for (int x = renderWidth - 1; x > renderInitialX - 1; x--) {
				for (int y = renderInitialY; y < renderHeight; y++) {
					int targetX = Calculator.get3x3MatrixProductX(inverseRotationMatrix, x, y);
					int targetY = Calculator.get3x3MatrixProductY(inverseRotationMatrix, x, y);
					if (targetX >= 0 && targetX < targetWidth && targetY >= 0 && targetY < targetHeight && targetY <= targetX * c) {																
						this.setPixel((targetWidth - 1) - x + pixelX, y + pixelY, color);
						this.setPixelLightBlock((targetWidth - 1) - x + pixelX, y + pixelY, blockLight);
					}
				}
			}			
			break;
		default:
			// put a log of error, because no available mode was chosen
			break;
		}
	}

	public void drawCircleArea(int radius, int color, int pixelX, int pixelY, boolean blockLight) {
		// adjusts the offsets based on the camera position
		pixelX -= this.camX;
		pixelY -= this.camY;
		// calculate diameter
		int width = radius * 2;
		int height = width;
		// validating image screen position
		if (pixelX < -width)
			return;
		if (pixelY < -height)
			return;
		if (pixelX >= this.pixelsWidth)
			return;
		if (pixelY >= this.pixelsHeight)
			return;
		// clipping image on screen size
		int renderWidth = width;
		int renderHeight = height;
		int renderInitialX = 0;
		int renderInitialY = 0;
		if (pixelX < 0)
			renderInitialX -= pixelX;
		if (pixelY < 0)
			renderInitialY -= pixelY;
		if (pixelX + renderWidth > this.pixelsWidth)
			renderWidth = this.pixelsWidth - pixelX;
		if (pixelY + renderHeight > this.pixelsHeight)
			renderHeight = this.pixelsHeight - pixelY;
		// draw pixels
		for (int x = renderInitialX; x < renderWidth; x++) {
			for (int y = renderInitialY; y < renderHeight; y++) {
				int referenceToCenterX = x - radius;
				int referenceToCenterY = y - radius;
				int distanceToCenter = referenceToCenterX * referenceToCenterX + referenceToCenterY * referenceToCenterY;
				int radiusPow2 = radius * radius;
				if (distanceToCenter <= radiusPow2) {
					this.setPixel(x + pixelX, y + pixelY, color);
					this.setPixelLightBlock(x + pixelX, y + pixelY, blockLight);
				}
			}
		}
	}

	/* lines and curves */
	
	public void drawRect(int width, int height, int strokeWidth, int color, int pixelX, int pixelY) {
		// adjusts the offsets based on the camera position
		pixelX -= this.camX;
		pixelY -= this.camY;
		// validating image screen position
		if (pixelX < -width)
			return;
		if (pixelY < -height)
			return;
		if (pixelX >= this.pixelsWidth)
			return;
		if (pixelY >= this.pixelsHeight)
			return;
		// clipping image on screen size
		int renderWidth = width;
		int renderHeight = height;
		int renderInitialX = 0;
		int renderInitialY = 0;
		if (pixelX < 0)
			renderInitialX -= pixelX;
		if (pixelY < 0)
			renderInitialY -= pixelY;
		if (pixelX + renderWidth > this.pixelsWidth)
			renderWidth = this.pixelsWidth - pixelX;
		if (pixelY + renderHeight > this.pixelsHeight)
			renderHeight = this.pixelsHeight - pixelY;
		// draw horizontal pixels
		for (int x = renderInitialX; x < renderWidth; x++) {
			for (int xWidth = 0; xWidth < strokeWidth; xWidth++) {
				this.setPixel(x + pixelX, xWidth + pixelY, color);
				this.setPixel(x + pixelX, xWidth + (height - strokeWidth) + pixelY, color);
			}
		}
		// draw vertical pixels
		for (int y = renderInitialY; y < renderHeight; y++) {
			for (int yWidth = 0; yWidth < strokeWidth; yWidth++) {
				this.setPixel(yWidth + pixelX, y + pixelY, color);
				this.setPixel(yWidth + (width - strokeWidth) + pixelX, y + pixelY, color);
			}
		}
	}

	public void drawMinThicknessLine(int pixelX0, int pixelY0, int pixelX1, int pixelY1, int color) {
		// adjusts the offsets based on the camera position
		pixelX0 -= this.camX;
		pixelY0 -= this.camY;
		pixelX1 -= this.camX;
		pixelY1 -= this.camY;
		// apply line raster algorithm
		int deltaX = pixelX1 - pixelX0;
		int deltaY = pixelY1 - pixelY0;
		int incrementalX = (deltaX > 0) ? 1 : -1;
		int incrementalY = (deltaY > 0) ? 1 : -1;
		deltaX = Math.abs(deltaX);
		deltaY = Math.abs(deltaY);
		float deltaError;
		float error = 0f;
		if (deltaX > deltaY) {
			deltaError = ((float) deltaY) / deltaX;
			int y = pixelY0;
			for (int x = pixelX0; x != pixelX1; x += incrementalX) {
				this.setPixel(x, y, color);
				error += deltaError;
				if (error >= 0.5f) {
					y += incrementalY;
					error -= 1.0f;
				}
			}
		} else {
			deltaError = ((float) deltaX) / deltaY;
			int x = pixelX0;
			for (int y = pixelY0; y != pixelY1; y += incrementalY) {
				this.setPixel(x, y, color);
				error += deltaError;
				if (error >= 0.5f) {
					x += incrementalX;
					error -= 1.0f;
				}
			}
		}
	}

	/* texts */
	
	public void drawFPS(Font font, int fps, int pixelX, int pixelY, int color) {
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
						this.setPixel((x + inLineOffset) + pixelX, y + pixelY, color);
					}
				}
			}
			inLineOffset += font.getWidths()[letterIndex];
		}
	}

	public void drawText(Font font, String text, int pixelX, int pixelY, int color) {
		// adjusts the offsets based on the camera position
		pixelX -= this.camX;
		pixelY -= this.camY;
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
						this.setPixel((x + inLineOffset) + pixelX, y + pixelY, color);
					}
				}
			}
			inLineOffset += font.getWidths()[letterIndex];
		}

	}

}
