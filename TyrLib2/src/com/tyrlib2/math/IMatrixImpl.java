package com.tyrlib2.math;

public interface IMatrixImpl {
	public void setIdentityM(float[] m, int offset);
	public void scaleM(float[] m, int offset, float scaleX, float scaleY, float scaleZ);
	public void multiplyMM(float[] result, int offsetResult, float[] lhs, int offsetLeft, float[] rhs, int offsetRight);
	public void translateM(float[] m, int offset, float x, float y, float z);
	public void multiplyMV(float[] result, int offsetResult, float[] m, int offsetMatrix, float[] v, int offsetVector);
	public void invertM(float[] inv, int offsetResult, float[] m, int offsetMatrix);
	public void rotateM(float[] result, int offsetResult, float rotation, float x, float y, float z);
	public void orthoM(float[] result, int offsetResult, float left, float right, float bottom, float top, float near, float far);
	public void frustumM(float[] m, int offsetResult, float left, float right, float bottom, float top, float near, float far);
	public void setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX,float upY, float upZ);
}