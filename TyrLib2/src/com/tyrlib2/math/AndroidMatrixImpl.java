package com.tyrlib2.math;

public class AndroidMatrixImpl implements IMatrixImpl {

	@Override
	public void setIdentityM(float[] m, int offset) {
		android.opengl.Matrix.setIdentityM(m, offset);
	}

	@Override
	public void scaleM(float[] m, int offset, float scaleX, float scaleY, float scaleZ) {
		android.opengl.Matrix.scaleM(m, offset, scaleX, scaleY, scaleZ);
	}

	@Override
	public void multiplyMM(float[] result, int offsetResult, float[] lhs, int offsetLeft, float[] rhs, int offsetRight) {
		android.opengl.Matrix.multiplyMM(result, offsetResult, lhs, offsetLeft, rhs, offsetRight);
		
	}

	@Override
	public void translateM(float[] m, int offset, float x, float y, float z) {
		android.opengl.Matrix.translateM(m, offset, x, y, z);
	}

	@Override
	public void multiplyMV(float[] result, int offsetResult, float[] m, int offsetMatrix, float[] v, int offsetVector) {
		android.opengl.Matrix.multiplyMV(result, offsetResult, m, offsetMatrix, v, offsetVector);
	}
	
	@Override
	public void invertM(float[] inv, int offsetResult, float[] m, int offsetMatrix) {
		android.opengl.Matrix.invertM(inv, offsetResult, m, offsetMatrix);
	}

	@Override
	public void rotateM(float[] result, int offsetResult, float rotation, float x, float y, float z) {
		android.opengl.Matrix.rotateM(result, offsetResult, rotation, x, y, z);
	}

	@Override
	public void orthoM(float[] result, int offsetResult, float left, float right, float bottom, float top, float near, float far) {
		android.opengl.Matrix.orthoM(result, offsetResult, left, right, bottom, top, near, far);
	}

	@Override
	public void frustumM(float[] m, int offsetResult, float left, float right,
			float bottom, float top, float near, float far) {
		android.opengl.Matrix.frustumM(m, offsetResult, left, right, bottom, top, near, far);
	}

	@Override
	public void setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY,
			float eyeZ, float centerX, float centerY, float centerZ, float upX,
			float upY, float upZ) {
		android.opengl.Matrix.setLookAtM(rm, rmOffset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
	}
	
	

}
