package com.simple.engine;

import static com.simple.engine.GameRunner.TILE_SIZE;

import java.util.ArrayList;
import java.util.List;

public class Phase {

	private String tag;
	
	private Image landscape;
	
	private List<TileImageMap> tileImageMaps = new ArrayList<>();
	
	public Phase(String tag) {
		this.tag = tag;
	}
	
	public void render(Renderer renderer) {
		if (this.landscape != null) {			
			renderer.setZDepth(Integer.MAX_VALUE);
			renderer.drawImage(this.landscape, 0, 0);		
		}
		for (int i = 0; i < this.tileImageMaps.size(); i++) {
			TileImageMap tileImageMap = this.tileImageMaps.get(i);
			Image image = tileImageMap.getImage();
			if (image != null) {
				renderer.setZDepth(Integer.MAX_VALUE - i);
				for (int x = 0; x < tileImageMap.getWidth(); x++) {
					for (int y = 0; y < tileImageMap.getHeight(); y++) {
						if (tileImageMap.isImageTile(x, y)) {
							renderer.drawImage(image, x * TILE_SIZE, y * TILE_SIZE);
						}
					}
				}
			}
		}
	}
	
	public void setLandscape(String path) {
		this.landscape = new Image(path);
	}
	
	public Image getLandscape() {
		return landscape;
	}

	public void addTileImageMap(TileImageMap tileImageMap) {
		this.tileImageMaps.add(tileImageMap);
	}

	public TileImageMap getTileImageMapByTag(String tag) {
		for (TileImageMap tileImageMap : this.tileImageMaps) {
			if (tileImageMap.getTag().equals(tag)) {
				return tileImageMap;
			}
		}
		return null;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
