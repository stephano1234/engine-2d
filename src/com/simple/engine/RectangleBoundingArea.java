package com.simple.engine;

import java.util.Arrays;
import java.util.List;

public class RectangleBoundingArea extends BoundingArea implements ConvexPolygon {

	private Coordinate vertex1;
	
	private Coordinate vertex2;
	
	private Coordinate vertex3;
	
	private Coordinate vertex4;
	
	private Vector vector1;
	
	private Vector vector2;
	
	private Vector vector3;
	
	private Vector vector4;
	
	public RectangleBoundingArea(String tag, GameObject gameObject, Coordinate vertex1, Coordinate vertex2, Coordinate vertex3, Coordinate vertex4) {
		super(tag, gameObject);
		this.changeVertices(vertex1, vertex2, vertex3, vertex4);
	}

	@Override
	public void move(Vector linearOffset) {
		this.changeVertices
			(
				new Coordinate(this.getVertex1().getX() + linearOffset.getX(), this.getVertex1().getY() + linearOffset.getY()), 
				new Coordinate(this.getVertex2().getX() + linearOffset.getX(), this.getVertex2().getY() + linearOffset.getY()), 
				new Coordinate(this.getVertex3().getX() + linearOffset.getX(), this.getVertex3().getY() + linearOffset.getY()),
				new Coordinate(this.getVertex4().getX() + linearOffset.getX(), this.getVertex4().getY() + linearOffset.getY())
			)
		;
	}

	@Override
	public RectangleBoundingArea spin(double angularOffset) {
		double[][] spinMatrix = Calculator.getRotationMatrix(angularOffset, this.getCenter());
		RectangleBoundingArea rotatedBoundingArea = new RectangleBoundingArea(this.tag + "#spinCopy", this.gameObject, this.vertex1, this.vertex2, this.vertex3, this.vertex4);
		rotatedBoundingArea.changeVertices
			(
				Calculator.get3x3MatrixProduct(spinMatrix, this.vertex1), 
				Calculator.get3x3MatrixProduct(spinMatrix, this.vertex2), 
				Calculator.get3x3MatrixProduct(spinMatrix, this.vertex3),
				Calculator.get3x3MatrixProduct(spinMatrix, this.vertex4)
			)
		;
		return rotatedBoundingArea;
	}

	public void changeVertices(Coordinate vertex1, Coordinate vertex2, Coordinate vertex3, Coordinate vertex4) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.vertex3 = vertex3;
		this.vertex4 = vertex4;
		this.vector1 = new Vector(this.vertex1, this.vertex2);
		this.vector2 = new Vector(this.vertex2, this.vertex3);
		this.vector3 = new Vector(this.vertex3, this.vertex4);
		this.vector4 = new Vector(this.vertex4, this.vertex1);
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

	public Coordinate getVertex4() {
		return vertex4;
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

	public Vector getVector4() {
		return vector4;
	}

	@Override
	public List<Coordinate> getVertices() {
		return Arrays.asList(this.vertex1, this.vertex2, this.vertex3, this.vertex4);
	}

	@Override
	public List<Vector> getVectors() {
		return Arrays.asList(this.vector1, this.vector2, this.vector3, this.vector4);
	}

	@Override
	public Coordinate getCenter() {
		Coordinate halfSidePoint1 = new Coordinate
			(
				(this.vertex1.getX() + this.vertex2.getX()) / 2.0,
				this.vertex1.getY()
			)
		;
		Coordinate halfSidePoint2 = new Coordinate
			(
				(this.vertex3.getX() + this.vertex4.getX()) / 2.0,
				this.vertex3.getY()
			)
		;
		Vector halfSideMov = new Vector(halfSidePoint1, halfSidePoint2);
		halfSideMov.multiplyByScalar(0.5);
		halfSidePoint1.move(halfSideMov);
		return halfSidePoint1;
	}

}
