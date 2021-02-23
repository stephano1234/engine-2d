package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public class CollisionDetector {

	private static final List<List<BoundingArea>> collisionQueue = new ArrayList<>();

	private CollisionDetector() {}

	public static void incrementCollisionQueue(List<BoundingArea> boundingAreas) {
		collisionQueue.add(boundingAreas);
	}

	public static void resolveCollisions() {
		for (int i = 0; i < collisionQueue.size(); i++) {
			List<BoundingArea> gameObjectBoundingAreas1 = collisionQueue.get(i);
			for (int j = i + 1; j < collisionQueue.size(); j++) {
				List<BoundingArea> gameObjectBoundingAreas2 = collisionQueue.get(j);
				for (BoundingArea boundingAreaFrom1 : gameObjectBoundingAreas1) {
					for (BoundingArea boundingAreaFrom2 : gameObjectBoundingAreas2) {
						resolveCollision(boundingAreaFrom1, boundingAreaFrom2);
					}
				}
			}
		}
		collisionQueue.clear();
	}
	
	private static void resolveCollision(BoundingArea boundingArea1, BoundingArea boundingArea2) {
		if (boundingArea1 instanceof ConvexPolygon && boundingArea2 instanceof ConvexPolygon) {
			ConvexPolygon convexPolygon1 = (ConvexPolygon) boundingArea1;
			ConvexPolygon convexPolygon2 = (ConvexPolygon) boundingArea2;
			Vector minimumDistanceVector = null;
			int minimumDistanceValue = Integer.MAX_VALUE;
			List<Coordinate> vertices1 = convexPolygon1.getVertices();
			List<Coordinate> vertices2 = convexPolygon2.getVertices();
			List<Vector> vectors1 = convexPolygon1.getVectors();
			List<Vector> vectors2 = convexPolygon2.getVectors();
			for (Vector vector : vectors1) {
				int min1 = Integer.MAX_VALUE;
				int max1 = Integer.MIN_VALUE;
				int min2 = Integer.MAX_VALUE;
				int max2 = Integer.MIN_VALUE;
				Vector perpendicularVector = Calculator.getPerpendicularVector(vector);
				Coordinate baseVertex1 = vertices1.get(0);
				for (int i = 0; i < vertices1.size(); i++) {
					min1 = Math.min(Calculator.dotProduct(Calculator.getVector(baseVertex1, vertices1.get(i)), perpendicularVector), min1);
					max1 = Math.max(Calculator.dotProduct(Calculator.getVector(baseVertex1, vertices1.get(i)), perpendicularVector), max1);
				}
				for (int i = 0; i < vertices2.size(); i++) {
					min2 = Math.min(Calculator.dotProduct(Calculator.getVector(baseVertex1, vertices2.get(i)), perpendicularVector), min2);
					max2 = Math.max(Calculator.dotProduct(Calculator.getVector(baseVertex1, vertices2.get(i)), perpendicularVector), max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
				int minimumPerpendicularDistance = 
						max2 - min1 < max1 - min2 ? 
						-(max2 - min1) : max1 - min2;
				if (Math.abs(minimumDistanceValue) > Math.abs(minimumPerpendicularDistance)) {
					minimumDistanceValue = minimumPerpendicularDistance;
					minimumDistanceVector = perpendicularVector;
				}
			}						
			for (Vector vector : vectors2) {
				int min1 = Integer.MAX_VALUE;
				int max1 = Integer.MIN_VALUE;
				int min2 = Integer.MAX_VALUE;
				int max2 = Integer.MIN_VALUE;
				Vector perpendicularVector = Calculator.getPerpendicularVector(vector);
				Coordinate baseVertex2 = vertices2.get(0);
				for (int i = 0; i < vertices1.size(); i++) {
					min1 = Math.min(Calculator.dotProduct(Calculator.getVector(baseVertex2, vertices1.get(i)), perpendicularVector), min1);
					max1 = Math.max(Calculator.dotProduct(Calculator.getVector(baseVertex2, vertices1.get(i)), perpendicularVector), max1);
				}
				for (int i = 0; i < vertices2.size(); i++) {
					min2 = Math.min(Calculator.dotProduct(Calculator.getVector(baseVertex2, vertices2.get(i)), perpendicularVector), min2);
					max2 = Math.max(Calculator.dotProduct(Calculator.getVector(baseVertex2, vertices2.get(i)), perpendicularVector), max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
				int minimumPerpendicularDistance = 
						max2 - min1 < max1 - min2 ? 
						-(max2 - min1) : max1 - min2;
				if (Math.abs(minimumDistanceValue) > Math.abs(minimumPerpendicularDistance)) {
					minimumDistanceValue = minimumPerpendicularDistance;
					minimumDistanceVector = perpendicularVector;
				}
			}
			if (minimumDistanceVector != null) {
				minimumDistanceVector.multiplyByScalar(minimumDistanceValue / Math.pow(Calculator.getNorm(minimumDistanceVector), 2));
				boundingArea2.move(minimumDistanceVector);
				boundingArea2.getGameObject().getOffset().plus(minimumDistanceVector);
			}
			boundingArea1.getGameObject().applyCollisionEvent(boundingArea2.getGameObject());
			boundingArea2.getGameObject().applyCollisionEvent(boundingArea1.getGameObject());
		}
	}
	
}
