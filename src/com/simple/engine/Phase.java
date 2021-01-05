package com.simple.engine;

import static com.simple.engine.GameRunner.TILE_SIZE;

import java.util.ArrayList;
import java.util.List;

public class Phase {

	private String tag;
	
	private Image landscape;
	
	private List<TileEventMap> tileEventMaps = new ArrayList<>();
	
	private List<TileImageMap> tileImageMaps = new ArrayList<>();
	
	public Phase(String tag) {
		this.tag = tag;
	}
	
	public void render(Engine engine) {
		if (this.landscape != null) {			
			engine.getRenderer().setZDepth(Integer.MAX_VALUE);
			engine.getRenderer().drawImage(this.landscape, 0, 0);		
		}
		for (int i = 0; i < this.tileImageMaps.size(); i++) {
			TileImageMap tileImageMap = this.tileImageMaps.get(i);
			Image image = tileImageMap.getImage();
			if (image != null) {
				engine.getRenderer().setZDepth(Integer.MAX_VALUE - i);
				for (int x = 0; x < tileImageMap.getWidth(); x++) {
					for (int y = 0; y < tileImageMap.getHeight(); y++) {
						if (tileImageMap.isImageTile(x, y)) {
							engine.getRenderer().drawImage(image, x * TILE_SIZE, y * TILE_SIZE);
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

	public void addTileEventMap(TileEventMap tileEventMap) {
		this.tileEventMaps.add(tileEventMap);
	}

	public void addCollisionMap(String eventMapPath) {
		TileEventMap tileEventMap = new TileEventMap(eventMapPath, TileEventMap.COLLISION_TAG);
		TileEventMap collisionMap = this.getCollisionMap();
		if (collisionMap != null) {
			collisionMap.mergeEventMap(tileEventMap);
			this.tileEventMaps.remove(collisionMap);
			this.tileEventMaps.add(collisionMap);	
		} else {			
			this.addTileEventMap(tileEventMap);
		}
	}

	public TileEventMap getTileEventMapByTag(String tag) {
		for (TileEventMap tileEventMap : this.tileEventMaps) {
			if (tileEventMap.getTag().equals(tag)) {
				return tileEventMap;
			}
		}
		return null;
	}

	public TileEventMap getCollisionMap() {
		return this.getTileEventMapByTag(TileEventMap.COLLISION_TAG);
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
