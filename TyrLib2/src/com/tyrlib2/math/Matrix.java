package com.tyrlib2.math;

public class Matrix {
	
	public static IMatrixImpl IMPL; 
	
	public static void setIdentityM(float[] m, int offset) {
		IMPL.setIdentityM(m, offset);
	}

	public static void scaleM(float[] m, int offset, float scaleX, float scaleY, float scaleZ) {
		IMPL.scaleM(m, offset, scaleX, scaleY, scaleZ);
	}

	public static void multiplyMM(float[] result, int offsetResult, float[] lhs, int offsetLeft, float[] rhs, int offsetRight) {
		IMPL.multiplyMM(result, offsetResult, lhs, offsetLeft, rhs, offsetRight);
	}

	public static void translateM(float[] m, int offset, float x, float y, float z) {
		IMPL.translateM(m, offset, x, y, z);
	}

	public static void multiplyMV(float[] result, int offsetResult, float[] m, int offsetMatrix, float[] v, int offsetVector) {		
		IMPL.multiplyMV(result, offsetResult, m, offsetMatrix, v, offsetVector);
	}

	public static void invertM(float[] inv, int offsetResult, float[] m, int offsetMatrix) {
		IMPL.invertM(inv, offsetResult, m, offsetMatrix);
	}

	public static void rotateM(float[] result, int offsetResult, float rotation, float x, float y, float z) {
		IMPL.rotateM(result, offsetResult, rotation, x, y, z);
	}

	public static void orthoM(float[] result, int offsetResult, float left, float right, float bottom, float top, float near, float far) {
		IMPL.orthoM(result, offsetResult, left, right, bottom, top, near, far);
	}

	public static void frustumM(float[] m, int offsetResult, float left, float right, float bottom, float top, float near, float far) {
		IMPL.frustumM(m, offsetResult, left, right, bottom, top, near, far);
	}

	public static void setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY,
			float eyeZ, float centerX, float centerY, float centerZ, float upX,
			float upY, float upZ) {
		IMPL.setLookAtM(rm, rmOffset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
	}
}
