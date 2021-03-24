package com.simple.engine;

import java.util.ArrayList;
import java.util.List;

public class BoundingAreasInteractionsResolver {

	private static final List<List<BoundingArea>> intersectionQueue = new ArrayList<>();
	
	private static final List<List<BoundingArea>> collisionQueue = new ArrayList<>();

	private BoundingAreasInteractionsResolver() {}

	public static void incrementIntersectionQueue(List<BoundingArea> boundingAreas) {
		intersectionQueue.add(boundingAreas);
	}

	public static void incrementCollisionQueue(List<BoundingArea> boundingAreas) {
		collisionQueue.add(boundingAreas);
	}

	public static void resolveIntersectionInteractions() {
		for (int i = 0; i < intersectionQueue.size(); i++) {
			List<BoundingArea> gameObjectBoundingAreas1 = intersectionQueue.get(i);
			if (gameObjectBoundingAreas1.isEmpty()) {
				continue;
			}
			for (int j = i + 1; j < intersectionQueue.size(); j++) {
				List<BoundingArea> gameObjectBoundingAreas2 = intersectionQueue.get(j);
				if 
				(
					gameObjectBoundingAreas2
						.isEmpty()
					||
					gameObjectBoundingAreas1
						.get(0)
						.getGameObject()
						.getIgnoredIntersectionInteractionGameObjectsTags()
						.contains(gameObjectBoundingAreas2.get(0).getGameObject().getTag())
					||
					gameObjectBoundingAreas2
						.get(0)
						.getGameObject()
						.getIgnoredIntersectionInteractionGameObjectsTags()
						.contains(gameObjectBoundingAreas1.get(0).getGameObject().getTag())
				)
				{
					continue;
				}
				for (BoundingArea boundingAreaFrom1 : gameObjectBoundingAreas1) {
					for (BoundingArea boundingAreaFrom2 : gameObjectBoundingAreas2) {
						resolveIntersectionInteraction(boundingAreaFrom1, boundingAreaFrom2);
					}
				}
			}
		}
		intersectionQueue.clear();
	}

	public static void resolveCollisionInteractions() {
		for (int i = 0; i < collisionQueue.size(); i++) {
			List<BoundingArea> gameObjectBoundingAreas1 = collisionQueue.get(i);
			if (gameObjectBoundingAreas1.isEmpty()) {
				continue;
			}
			for (int j = i + 1; j < collisionQueue.size(); j++) {
				List<BoundingArea> gameObjectBoundingAreas2 = collisionQueue.get(j);
				if 
				(
					gameObjectBoundingAreas2
						.isEmpty()
					||
					gameObjectBoundingAreas1
						.get(0)
						.getGameObject()
						.getIgnoredCollisionInteractionGameObjectsTags()
						.contains(gameObjectBoundingAreas2.get(0).getGameObject().getTag())
					||
					gameObjectBoundingAreas2
						.get(0)
						.getGameObject()
						.getIgnoredCollisionInteractionGameObjectsTags()
						.contains(gameObjectBoundingAreas1.get(0).getGameObject().getTag())
				)
				{
					continue;
				}
				for (BoundingArea boundingAreaFrom1 : gameObjectBoundingAreas1) {
					for (BoundingArea boundingAreaFrom2 : gameObjectBoundingAreas2) {
						resolveCollisionInteraction(boundingAreaFrom1, boundingAreaFrom2);
					}
				}
			}
		}
		collisionQueue.clear();
	}

	private static void resolveIntersectionInteraction(BoundingArea boundingArea1, BoundingArea boundingArea2) {
		// convex polygon X convex polygon
		if (boundingArea1 instanceof ConvexPolygon && boundingArea2 instanceof ConvexPolygon) {
			ConvexPolygon convexPolygon1 = (ConvexPolygon) boundingArea1.spin(boundingArea1.getGameObject().getAngle());
			ConvexPolygon convexPolygon2 = (ConvexPolygon) boundingArea2.spin(boundingArea2.getGameObject().getAngle());
			List<Coordinate> vertices1 = convexPolygon1.getVertices();
			List<Coordinate> vertices2 = convexPolygon2.getVertices();
			List<Vector> vectors1 = convexPolygon1.getVectors();
			List<Vector> vectors2 = convexPolygon2.getVectors();
			for (Vector vector : vectors1) {
				double min1 = Integer.MAX_VALUE;
				double max1 = Integer.MIN_VALUE;
				double min2 = Integer.MAX_VALUE;
				double max2 = Integer.MIN_VALUE;
				Vector perpendicularVector = Calculator.getLeftPerpendicularVector(vector);
				Coordinate baseVertex1 = vertices1.get(0);
				for (int i = 0; i < vertices1.size(); i++) {
					min1 = Math.min(Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), perpendicularVector), min1);
					max1 = Math.max(Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), perpendicularVector), max1);
				}
				for (int i = 0; i < vertices2.size(); i++) {
					min2 = Math.min(Calculator.dotProduct(new Vector(baseVertex1, vertices2.get(i)), perpendicularVector), min2);
					max2 = Math.max(Calculator.dotProduct(new Vector(baseVertex1, vertices2.get(i)), perpendicularVector), max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
			}
			for (Vector vector : vectors2) {
				double min1 = Integer.MAX_VALUE;
				double max1 = Integer.MIN_VALUE;
				double min2 = Integer.MAX_VALUE;
				double max2 = Integer.MIN_VALUE;
				Vector perpendicularVector = Calculator.getRightPerpendicularVector(vector);
				Coordinate baseVertex2 = vertices2.get(0);
				for (int i = 0; i < vertices1.size(); i++) {
					min1 = Math.min(Calculator.dotProduct(new Vector(baseVertex2, vertices1.get(i)), perpendicularVector), min1);
					max1 = Math.max(Calculator.dotProduct(new Vector(baseVertex2, vertices1.get(i)), perpendicularVector), max1);
				}
				for (int i = 0; i < vertices2.size(); i++) {
					min2 = Math.min(Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), perpendicularVector), min2);
					max2 = Math.max(Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), perpendicularVector), max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
			}
			boundingArea1.getGameObject().applyIntersectionInteractionEvent(boundingArea2);
			boundingArea2.getGameObject().applyIntersectionInteractionEvent(boundingArea1);
		}
		//convex polygon X circle
		if (!(boundingArea1 instanceof ConvexPolygon) && boundingArea2 instanceof ConvexPolygon) {
			CircleBoundingArea circleBoundingArea1 = (CircleBoundingArea) boundingArea1.spin(boundingArea1.getGameObject().getAngle());
			ConvexPolygon convexPolygon2 = (ConvexPolygon) boundingArea2.spin(boundingArea2.getGameObject().getAngle());
			Coordinate circleCenter1 = circleBoundingArea1.getCenter();
			int circleRadius1 = circleBoundingArea1.getRadius();
			List<Coordinate> vertices2 = convexPolygon2.getVertices();
			List<Vector> vectors2 = convexPolygon2.getVectors();
			for (Coordinate baseVertex2 : vertices2) {
				Vector projectionVector = new Vector(baseVertex2, circleCenter1);
				double projectionVectorNorm = projectionVector.getNorm();
				double min1 = (projectionVectorNorm - circleRadius1) * projectionVectorNorm;
				double max1 = (projectionVectorNorm + circleRadius1) * projectionVectorNorm;
				double min2 = Integer.MAX_VALUE;
				double max2 = Integer.MIN_VALUE;
				for (int i = 0; i < vertices2.size(); i++) {
					min2 = Math.min(Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), projectionVector), min2);
					max2 = Math.max(Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), projectionVector), max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
			}
			for (Vector vector : vectors2) {
				Vector perpendicularVector2 = Calculator.getRightPerpendicularVector(vector);
				double perpendicularVector2Norm = perpendicularVector2.getNorm();
				Coordinate baseVertex2 = vertices2.get(0);
				double min1 = Calculator.dotProduct(new Vector(baseVertex2, circleCenter1), perpendicularVector2) - circleRadius1 * perpendicularVector2Norm;
				double max1 = Calculator.dotProduct(new Vector(baseVertex2, circleCenter1), perpendicularVector2) + circleRadius1 * perpendicularVector2Norm;
				double min2 = Integer.MAX_VALUE;
				double max2 = Integer.MIN_VALUE;
				for (int i = 0; i < vertices2.size(); i++) {
					min2 = Math.min(Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), perpendicularVector2), min2);
					max2 = Math.max(Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), perpendicularVector2), max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
			}
			boundingArea1.getGameObject().applyIntersectionInteractionEvent(boundingArea2);
			boundingArea2.getGameObject().applyIntersectionInteractionEvent(boundingArea1);
		}
		if (!(boundingArea2 instanceof ConvexPolygon) && boundingArea1 instanceof ConvexPolygon) {
			ConvexPolygon convexPolygon1 = (ConvexPolygon) boundingArea1.spin(boundingArea1.getGameObject().getAngle());
			CircleBoundingArea circleBoundingArea2 = (CircleBoundingArea) boundingArea2.spin(boundingArea2.getGameObject().getAngle());
			List<Coordinate> vertices1 = convexPolygon1.getVertices();
			List<Vector> vectors1 = convexPolygon1.getVectors();
			Coordinate circleCenter2 = circleBoundingArea2.getCenter();
			int circleRadius2 = circleBoundingArea2.getRadius();
			for (Coordinate baseVertex1 : vertices1) {
				Vector projectionVector = new Vector(baseVertex1, circleCenter2);
				double projectionVectorNorm = projectionVector.getNorm();
				double min1 = Integer.MAX_VALUE;
				double max1 = Integer.MIN_VALUE;
				for (int i = 0; i < vertices1.size(); i++) {
					min1 = Math.min(Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), projectionVector), min1);
					max1 = Math.max(Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), projectionVector), max1);
				}
				double min2 = (projectionVectorNorm - circleRadius2) * projectionVectorNorm;
				double max2 = (projectionVectorNorm + circleRadius2) * projectionVectorNorm;
				if (min2 > max1 || min1 > max2) {
					return;
				}
			}
			for (Vector vector : vectors1) {
				Vector perpendicularVector1 = Calculator.getRightPerpendicularVector(vector);
				double perpendicularVector1Norm = perpendicularVector1.getNorm();
				Coordinate baseVertex1 = vertices1.get(0);
				double min1 = Integer.MAX_VALUE;
				double max1 = Integer.MIN_VALUE;
				for (int i = 0; i < vertices1.size(); i++) {
					min1 = Math.min(Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), perpendicularVector1), min1);
					max1 = Math.max(Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), perpendicularVector1), max1);
				}
				double min2 = Calculator.dotProduct(new Vector(baseVertex1, circleCenter2), perpendicularVector1) - circleRadius2 * perpendicularVector1Norm;
				double max2 = Calculator.dotProduct(new Vector(baseVertex1, circleCenter2), perpendicularVector1) + circleRadius2 * perpendicularVector1Norm;
				if (min2 > max1 || min1 > max2) {
					return;
				}
			}
			boundingArea1.getGameObject().applyIntersectionInteractionEvent(boundingArea2);
			boundingArea2.getGameObject().applyIntersectionInteractionEvent(boundingArea1);
		}
		// circle X circle
		if (!(boundingArea1 instanceof ConvexPolygon) && !(boundingArea2 instanceof ConvexPolygon)) {
			Vector minimumDistanceVector = null;
			double minimumDistanceValue = 0;
			CircleBoundingArea circleBoundingArea1 = (CircleBoundingArea) boundingArea1.spin(boundingArea1.getGameObject().getAngle());
			Coordinate circleCenter1 = circleBoundingArea1.getCenter();
			int circleRadius1 = circleBoundingArea1.getRadius();
			CircleBoundingArea circleBoundingArea2 = (CircleBoundingArea) boundingArea2.spin(boundingArea2.getGameObject().getAngle());
			Coordinate circleCenter2 = circleBoundingArea2.getCenter();
			int circleRadius2 = circleBoundingArea2.getRadius();
			minimumDistanceVector = new Vector(circleCenter1, circleCenter2); 
			double distanceBetweenCenters = minimumDistanceVector.getNorm();
			minimumDistanceValue = circleRadius1 + circleRadius2 - distanceBetweenCenters;
			if (minimumDistanceValue < 0) {
				return;
			}
			boundingArea1.getGameObject().applyIntersectionInteractionEvent(boundingArea2);
			boundingArea2.getGameObject().applyIntersectionInteractionEvent(boundingArea1);
		}
	}

	private static void resolveCollisionInteraction(BoundingArea boundingArea1, BoundingArea boundingArea2) {
		// convex polygon X convex polygon
		if (boundingArea1 instanceof ConvexPolygon && boundingArea2 instanceof ConvexPolygon) {
			ConvexPolygon convexPolygon1 = (ConvexPolygon) boundingArea1.spin(boundingArea1.getGameObject().getAngle());
			ConvexPolygon convexPolygon2 = (ConvexPolygon) boundingArea2.spin(boundingArea2.getGameObject().getAngle());
			Vector minimumDistanceVector = null;
			double minimumDistanceValue = Integer.MAX_VALUE;
			List<Coordinate> vertices1 = convexPolygon1.getVertices();
			List<Coordinate> vertices2 = convexPolygon2.getVertices();
			List<Vector> vectors1 = convexPolygon1.getVectors();
			List<Vector> vectors2 = convexPolygon2.getVectors();
			for (Vector vector : vectors1) {
				double min1 = Integer.MAX_VALUE;
				double max1 = Integer.MIN_VALUE;
				double min2 = Integer.MAX_VALUE;
				double max2 = Integer.MIN_VALUE;
				Vector perpendicularVector = Calculator.getLeftPerpendicularVector(vector);
				double perpendicularVectorNorm = perpendicularVector.getNorm();
				Coordinate baseVertex1 = vertices1.get(0);
				for (int i = 0; i < vertices1.size(); i++) {
					min1 = Math.min(Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), perpendicularVector) / perpendicularVectorNorm, min1);
					max1 = Math.max(Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), perpendicularVector) / perpendicularVectorNorm, max1);
				}
				for (int i = 0; i < vertices2.size(); i++) {
					min2 = Math.min(Calculator.dotProduct(new Vector(baseVertex1, vertices2.get(i)), perpendicularVector) / perpendicularVectorNorm, min2);
					max2 = Math.max(Calculator.dotProduct(new Vector(baseVertex1, vertices2.get(i)), perpendicularVector) / perpendicularVectorNorm, max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
				double minimumPerpendicularDistance = 
						(max2 - min1) < (max1 - min2) ? 
						-(max2 - min1) : (max1 - min2);
				if (Math.abs(minimumDistanceValue) > Math.abs(minimumPerpendicularDistance)) {
					minimumDistanceValue = minimumPerpendicularDistance;
					minimumDistanceVector = perpendicularVector;
				}
			}
			for (Vector vector : vectors2) {
				double min1 = Integer.MAX_VALUE;
				double max1 = Integer.MIN_VALUE;
				double min2 = Integer.MAX_VALUE;
				double max2 = Integer.MIN_VALUE;
				Vector perpendicularVector = Calculator.getRightPerpendicularVector(vector);
				double perpendicularVectorNorm = perpendicularVector.getNorm();
				Coordinate baseVertex2 = vertices2.get(0);
				for (int i = 0; i < vertices1.size(); i++) {
					min1 = Math.min(Calculator.dotProduct(new Vector(baseVertex2, vertices1.get(i)), perpendicularVector) / perpendicularVectorNorm, min1);
					max1 = Math.max(Calculator.dotProduct(new Vector(baseVertex2, vertices1.get(i)), perpendicularVector) / perpendicularVectorNorm, max1);
				}
				for (int i = 0; i < vertices2.size(); i++) {
					min2 = Math.min(Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), perpendicularVector) / perpendicularVectorNorm, min2);
					max2 = Math.max(Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), perpendicularVector) / perpendicularVectorNorm, max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
				double minimumPerpendicularDistance = 
						(max2 - min1) < (max1 - min2) ? 
						-(max2 - min1) : (max1 - min2);
				if (Math.abs(minimumDistanceValue) > Math.abs(minimumPerpendicularDistance)) {
					minimumDistanceValue = minimumPerpendicularDistance;
					minimumDistanceVector = perpendicularVector;
				}
			}
			if (minimumDistanceVector != null) {
				double minimumDistanceVectorNorm = minimumDistanceVector.getNorm();
				minimumDistanceVector.multiplyByScalar(minimumDistanceValue / minimumDistanceVectorNorm);
				boundingArea2.move(minimumDistanceVector);
				boundingArea2.getGameObject().getLinearOffset().plus(minimumDistanceVector);
			}
			boundingArea1.getGameObject().applyCollisionInteractionEvent(boundingArea2);
			boundingArea2.getGameObject().applyCollisionInteractionEvent(boundingArea1);
		}
		// circle X convex polygon
		if (!(boundingArea1 instanceof ConvexPolygon) && boundingArea2 instanceof ConvexPolygon) {
			CircleBoundingArea circleBoundingArea1 = (CircleBoundingArea) boundingArea1.spin(boundingArea1.getGameObject().getAngle());
			ConvexPolygon convexPolygon2 = (ConvexPolygon) boundingArea2.spin(boundingArea2.getGameObject().getAngle());
			Vector minimumDistanceVector = null;
			double minimumDistanceValue = Integer.MAX_VALUE;
			Coordinate circleCenter1 = circleBoundingArea1.getCenter();
			int circleRadius1 = circleBoundingArea1.getRadius();
			List<Coordinate> vertices2 = convexPolygon2.getVertices();
			List<Vector> vectors2 = convexPolygon2.getVectors();
			for (Coordinate baseVertex2 : vertices2) {
				Vector projectionVector = new Vector(baseVertex2, circleCenter1);
				double projectionVectorNorm = projectionVector.getNorm();
				double min1 = projectionVectorNorm - circleRadius1;
				double max1 = projectionVectorNorm + circleRadius1;
				double min2 = Integer.MAX_VALUE;
				double max2 = Integer.MIN_VALUE;
				for (int i = 0; i < vertices2.size(); i++) {
					double vertexProjection = Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), projectionVector) / projectionVectorNorm;
					min2 = Math.min(vertexProjection, min2);
					max2 = Math.max(vertexProjection, max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
				double minimumDistance = 
						max2 - min1 < max1 - min2 ? 
						-(max2 - min1) : max1 - min2;
				if (Math.abs(minimumDistanceValue) > Math.abs(minimumDistance)) {
					minimumDistanceValue = minimumDistance;
					minimumDistanceVector = projectionVector;
				}
			}
			for (Vector vector : vectors2) {
				Vector perpendicularVector2 = Calculator.getRightPerpendicularVector(vector);
				double perpendicularVector2Norm = perpendicularVector2.getNorm();
				Coordinate baseVertex2 = vertices2.get(0);
				double circleCenter1Projection = Calculator.dotProduct(new Vector(baseVertex2, circleCenter1), perpendicularVector2) / perpendicularVector2Norm;
				double min1 = circleCenter1Projection - circleRadius1;
				double max1 = circleCenter1Projection + circleRadius1;
				double min2 = Integer.MAX_VALUE;
				double max2 = Integer.MIN_VALUE;
				for (int i = 0; i < vertices2.size(); i++) {
					double vertexProjection = Calculator.dotProduct(new Vector(baseVertex2, vertices2.get(i)), perpendicularVector2) / perpendicularVector2Norm;
					min2 = Math.min(vertexProjection, min2);
					max2 = Math.max(vertexProjection, max2);
				}
				if (min1 > max2 || min2 > max1) {
					return;
				}
				double minimumPerpendicularDistance = 
						max2 - min1 < max1 - min2 ? 
						-(max2 - min1) : max1 - min2;
				if (Math.abs(minimumDistanceValue) > Math.abs(minimumPerpendicularDistance)) {
					minimumDistanceValue = minimumPerpendicularDistance;
					minimumDistanceVector = perpendicularVector2;
				}
			}
			if (minimumDistanceVector != null) {
				minimumDistanceVector.multiplyByScalar(minimumDistanceValue / minimumDistanceVector.getNorm());
				boundingArea2.move(minimumDistanceVector);
				boundingArea2.getGameObject().getLinearOffset().plus(minimumDistanceVector);
			}
			boundingArea1.getGameObject().applyCollisionInteractionEvent(boundingArea2);
			boundingArea2.getGameObject().applyCollisionInteractionEvent(boundingArea1);
		}
		// convex polygon X circle
		if (!(boundingArea2 instanceof ConvexPolygon) && boundingArea1 instanceof ConvexPolygon) {
			ConvexPolygon convexPolygon1 = (ConvexPolygon) boundingArea1.spin(boundingArea1.getGameObject().getAngle());
			CircleBoundingArea circleBoundingArea2 = (CircleBoundingArea) boundingArea2.spin(boundingArea2.getGameObject().getAngle());
			Vector minimumDistanceVector = null;
			double minimumDistanceValue = Integer.MAX_VALUE;
			List<Coordinate> vertices1 = convexPolygon1.getVertices();
			List<Vector> vectors1 = convexPolygon1.getVectors();
			Coordinate circleCenter2 = circleBoundingArea2.getCenter();
			int circleRadius2 = circleBoundingArea2.getRadius();
//			for (Coordinate baseVertex1 : vertices1) {
//				Vector projectionVector = new Vector(baseVertex1, circleCenter2);
//				double projectionVectorNorm = projectionVector.getNorm();
//				double min1 = Integer.MAX_VALUE;
//				double max1 = Integer.MIN_VALUE;
//				for (int i = 0; i < vertices1.size(); i++) {
//					double vertexProjection = Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), projectionVector) / projectionVectorNorm;
//					min1 = Math.min(vertexProjection, min1);
//					max1 = Math.max(vertexProjection, max1);
//				}
//				double min2 = projectionVectorNorm - circleRadius2;
//				double max2 = projectionVectorNorm + circleRadius2;
//				if (min2 > max1 || min1 > max2) {
//					return;
//				}
//				double minimumDistance = 
//						max2 - min1 < max1 - min2 ? 
//						-(max2 - min1) : max1 - min2;
//				if (Math.abs(minimumDistanceValue) > Math.abs(minimumDistance)) {
//					minimumDistanceValue = minimumDistance;
//					minimumDistanceVector = projectionVector;
//				}
//			}			

		// implementar algoritmo de distancia minima entre um ponto e uma reta	
			
//			for (Vector vector : vectors1) {
//				Vector perpendicularVector1 = Calculator.getRightPerpendicularVector(vector);
//				double perpendicularVector1Norm = perpendicularVector1.getNorm();
//				Coordinate baseVertex1 = vertices1.get(0);
//				double min1 = Integer.MAX_VALUE;
//				double max1 = Integer.MIN_VALUE;
//				for (int i = 0; i < vertices1.size(); i++) {
//					double vertexProjection = Calculator.dotProduct(new Vector(baseVertex1, vertices1.get(i)), perpendicularVector1) / perpendicularVector1Norm;
//					min1 = Math.min(vertexProjection, min1);
//					max1 = Math.max(vertexProjection, max1);
//				}
//				double circleCenter2Projection = Calculator.dotProduct(new Vector(baseVertex1, circleCenter2), perpendicularVector1) / perpendicularVector1Norm;
//				double min2 = circleCenter2Projection - circleRadius2;
//				double max2 = circleCenter2Projection + circleRadius2;
//				if (min2 > max1 || min1 > max2) {
//					return;
//				}
//				double minimumPerpendicularDistance = 
//						max2 - min1 < max1 - min2 ? 
//						-(max2 - min1) : max1 - min2;
//				if (Math.abs(minimumDistanceValue) > Math.abs(minimumPerpendicularDistance)) {
//					minimumDistanceValue = minimumPerpendicularDistance;
//					minimumDistanceVector = perpendicularVector1;
//				}
//			}
			if (minimumDistanceVector != null) {
				minimumDistanceVector.multiplyByScalar(minimumDistanceValue / minimumDistanceVector.getNorm());
//				System.out.println("x: " + minimumDistanceVector.getX() + ", y: " + minimumDistanceVector.getY());
				boundingArea2.move(minimumDistanceVector);
				boundingArea2.getGameObject().getLinearOffset().plus(minimumDistanceVector);
			}
			boundingArea1.getGameObject().applyCollisionInteractionEvent(boundingArea2);
			boundingArea2.getGameObject().applyCollisionInteractionEvent(boundingArea1);
		}
		// circle X circle
		if (!(boundingArea1 instanceof ConvexPolygon) && !(boundingArea2 instanceof ConvexPolygon)) {
			Vector minimumDistanceVector = null;
			double minimumDistanceValue = 0;
			CircleBoundingArea circleBoundingArea1 = (CircleBoundingArea) boundingArea1.spin(boundingArea1.getGameObject().getAngle());
			Coordinate circleCenter1 = circleBoundingArea1.getCenter();
			int circleRadius1 = circleBoundingArea1.getRadius();
			CircleBoundingArea circleBoundingArea2 = (CircleBoundingArea) boundingArea2.spin(boundingArea2.getGameObject().getAngle());
			Coordinate circleCenter2 = circleBoundingArea2.getCenter();
			int circleRadius2 = circleBoundingArea2.getRadius();
			minimumDistanceVector = new Vector(circleCenter1, circleCenter2); 
			double distanceBetweenCenters = minimumDistanceVector.getNorm();
			minimumDistanceValue = circleRadius1 + circleRadius2 - distanceBetweenCenters;
			if (minimumDistanceValue < 0) {
				return;
			}
			minimumDistanceVector.multiplyByScalar(minimumDistanceValue / distanceBetweenCenters);
			boundingArea2.move(minimumDistanceVector);
			boundingArea2.getGameObject().getLinearOffset().plus(minimumDistanceVector);
			boundingArea1.getGameObject().applyCollisionInteractionEvent(boundingArea2);
			boundingArea2.getGameObject().applyCollisionInteractionEvent(boundingArea1);
		}
	}

}
