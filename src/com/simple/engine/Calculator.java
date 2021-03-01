package com.simple.engine;

public class Calculator {

	private Calculator() {}
	
	public static double[][] getRotationMatrix(float rad, int centerX, int centerY) {
		double[][] matrix = new double[3][3];
		double cosRad = Math.cos(rad);
		double sinRad = Math.sin(rad);
		matrix[0][0] = cosRad;
		matrix[0][1] = -sinRad;
		matrix[0][2] = cosRad * (-centerX) + (-sinRad) * (-centerY) + centerX;
		matrix[1][0] = sinRad;
		matrix[1][1] = cosRad;
		matrix[1][2] = sinRad * (-centerX) + cosRad * (-centerY) + centerY;
		matrix[2][0] = 0;
		matrix[2][1] = 0;
		matrix[2][2] = 1;
		return matrix;
	}

	public static double[][] getInverse3x3Matrix(double[][] matrix) {
		double[][] inverseMatrix = new double[3][3];
		double minorMatrix0x0Det = matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1];
		double minorMatrix0x1Det = matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0];
		double minorMatrix0x2Det = matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0];
		double minorMatrix1x0Det = matrix[0][1] * matrix[2][2] - matrix[0][2] * matrix[2][1];
		double minorMatrix1x1Det = matrix[0][0] * matrix[2][2] - matrix[0][2] * matrix[2][0];
		double minorMatrix1x2Det = matrix[0][0] * matrix[2][1] - matrix[0][1] * matrix[2][0];
		double minorMatrix2x0Det = matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1];
		double minorMatrix2x1Det = matrix[0][0] * matrix[1][2] - matrix[0][2] * matrix[1][0];
		double minorMatrix2x2Det = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
		double inverseMatrixDet = 1 / (matrix[0][0] * minorMatrix0x0Det - matrix[0][1] * minorMatrix0x1Det + matrix[0][2] * minorMatrix0x2Det);
		inverseMatrix[0][0] = inverseMatrixDet * minorMatrix0x0Det;
		inverseMatrix[0][1] = inverseMatrixDet * (-minorMatrix1x0Det);
		inverseMatrix[0][2] = inverseMatrixDet * minorMatrix2x0Det;
		inverseMatrix[1][0] = inverseMatrixDet * (-minorMatrix0x1Det);
		inverseMatrix[1][1] = inverseMatrixDet * minorMatrix1x1Det;
		inverseMatrix[1][2] = inverseMatrixDet * (-minorMatrix2x1Det);
		inverseMatrix[2][0] = inverseMatrixDet * minorMatrix0x2Det;		
		inverseMatrix[2][1] = inverseMatrixDet * (-minorMatrix1x2Det);
		inverseMatrix[2][2] = inverseMatrixDet * minorMatrix2x2Det;
		return inverseMatrix;
	}
	
	public static int get3x3MatrixProductX(double[][] matrix, int x, int y) {
		return (int) Math.round(matrix[0][0] * x + matrix[0][1] * y + matrix[0][2]);
	}

	public static int get3x3MatrixProductY(double[][] matrix, int x, int y) {
		return (int) Math.round(matrix[1][0] * x + matrix[1][1] * y + matrix[1][2]);
	}

	public static int[][] getInverse2x2Matrix(int[][] matrix) {
		int[][] inverseMatrix = new int[2][2];
		int inverseMatrixDet = 1 / (matrix[0][0] * matrix[1][1] * - matrix[0][1] * matrix[1][0]);
		inverseMatrix[0][0] = inverseMatrixDet * matrix[1][1];
		inverseMatrix[0][1] = inverseMatrixDet * (-matrix[0][1]);
		inverseMatrix[1][0] = inverseMatrixDet * (-matrix[1][0]);
		inverseMatrix[1][1] = inverseMatrixDet * matrix[0][0];
		return inverseMatrix;
	}

	public static int get2x2MatrixProductX(int[][] matrix, int x, int y) {
		return matrix[0][0] * x + matrix[0][1] * y;
	}

	public static int get2x2MatrixProductY(int[][] matrix, int x, int y) {
		return matrix[1][0] * x + matrix[1][1] * y;
	}
	
	public static Vector getPerpendicularVector(Vector vector) {
		return new Vector(vector.getY(), -vector.getX());
	}
	
	public static int dotProduct(Vector vector1, Vector vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY();
	}
	
	public static Vector getUnitaryVector(Vector vector) {
		double norm = vector.getNorm();
		return new Vector((int) Math.round(vector.getX() / norm), (int) Math.round(vector.getY() / norm));
	}
	
}
