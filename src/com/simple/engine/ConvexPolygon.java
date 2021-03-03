package com.simple.engine;

import java.util.List;

public interface ConvexPolygon {
	
	public List<Coordinate> getVertices();
	
	public List<Vector> getVectors();
	
	public Coordinate getCenter();
	
}
