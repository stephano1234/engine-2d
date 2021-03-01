package com.simple.engine;

import java.util.Arrays;
import java.util.List;

public class TriangularBoundingArea extends BoundingArea implements ConvexPolygon {

	private Coordinate vertex1;
	
	private Coordinate vertex2;
	
	private Coordinate vertex3;
	
	private Vector vector1;
	
	private Vector vector2;
	
	private Vector vector3;
	
	public TriangularBoundingArea(String tag, GameObject gameObject, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3) {
		super(tag, gameObject);
		this.changeVertices(vertex1, vertex2, vertex3);
	}

	@Override
	public void move(Vector offset) {
		this.changeVertices
			(
				new Coordinate(this.getVertex1().getX() + offset.getX(), this.getVertex1().getY() + offset.getY()), 
				new Coordinate(this.getVertex2().getX() + offset.getX(), this.getVertex2().getY() + offset.getY()), 
				new Coordinate(this.getVertex3().getX() + offset.getX(), this.getVertex3().getY() + offset.getY())
			)
		;
	}
	
	public void changeVertices(Coordinate vertex1, Coordinate vertex2, Coordinate vertex3) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.vertex3 = vertex3;
		this.vector1 = new Vector(this.vertex1, this.vertex2);
		this.vector2 = new Vector(this.vertex2, this.vertex3);
		this.vector3 = new Vector(this.vertex3, this.vertex1);
	}
	
	public Coordinate getVertex1() {
		return vertex1;
	}

	public Coordinate getVertex2() {
		return vertex2;
	}

	public Coordinate getVertex3() {
		return vertex3;
	}

	public Vector getVector1() {
		return vector1;
	}

	public Vector getVector2() {
		return vector2;
	}

	public Vector getVector3() {
		return vector3;
	}

	@Override
	public List<Coordinate> getVertices() {
		return Arrays.asList(this.vertex1, this.vertex2, this.vertex3);
	}

	@Override
	public List<Vector> getVectors() {
		return Arrays.asList(this.vector1, this.vector2, this.vector3);
	}

}
