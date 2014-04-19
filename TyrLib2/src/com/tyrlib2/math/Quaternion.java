package com.tyrlib2.math;

import android.util.FloatMath;


/**
 * Represents a rotation in 3D space
 * @author Sascha
 *
 */

public class Quaternion {
	public float x;
	public float y;
	public float z;
	public float w;
	
	private static final Quaternion IDENTITY = new Quaternion(0,0,0,1);
	
	/** When to perform slerp and when lerp (due to numerical instability) **/
	private static final float SLERP_TO_LERP_SWITCH_THRESHOLD = 0.01f;
	
	/** Defaults to the identity quaternion **/
	public Quaternion() {
		w = 1;
	}
	
	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Quaternion(Quaternion other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.w = other.w;
	}
	
	/**
	 * Get a normalized version of this quaternion
	 * @return	The normalized quaternion
	 */
	public Quaternion normalized() {
		Quaternion normalized = new Quaternion(this);
		normalized.normalize();
		return normalized;
	}
	
	/**
	 * Get the "length" of this quaternion
	 * @return
	 */
	public float length() {
		return (float) Math.sqrt(x*x + y*y + z*z + w*w);
	}
	
	/**
	 * Normalize this quaternion
	 */
	public void normalize() {
		float length = length();
		x /= length;
		y /= length;
		z /= length;
		w /= length;
	}
	
	/**
	 * Get the inverse of this quaternion
	 * @return	The inverse of this quaternion
	 */
	public Quaternion inverse() {
		Quaternion inverse = new Quaternion(-x, -y, -z, w);
		return inverse;
	}
	
	/**
	 * Invert this quaternion
	 */
	
	public void invert() {
		x = -x;
		y = -y;
		z = -z;
	}
	
	/**
	 * Multiply this quaternion with another
	 * @param other
	 * @return	The resulting quaternion
	 */
	public Quaternion multiply(Quaternion other) {

		float angle = (w * other.w) - (x * other.x + y * other.y + z * other.z);
		
		float crossX = y * other.z - z * other.y;
		float crossY = -(x * other.z - z * other.x);
		float crossZ = x * other.y - y * other.x;

		Quaternion result = new Quaternion();
		result.x = x * other.w + other.x * w + crossX;
		result.y = y * other.w + other.y * w + crossY;
		result.z = z * other.w + other.z * w + crossZ;
		result.w = angle;
		
		return result;
	}
	
	/**
	 * Multiply this quaternion with another
	 * @param other
	 * @param result
	 */
	public void multiply(Quaternion other, Quaternion result) {

		float angle = (w * other.w) - (x * other.x + y * other.y + z * other.z);
		
		float crossX = y * other.z - z * other.y;
		float crossY = -(x * other.z - z * other.x);
		float crossZ = x * other.y - y * other.x;

		result.x = x * other.w + other.x * w + crossX;
		result.y = y * other.w + other.y * w + crossY;
		result.z = z * other.w + other.z * w + crossZ;
		result.w = angle;
	}
	
	/**
	 * Multiply this quaternion with another
	 * @param other
	 */
	public void multiplyNoTmp(Quaternion other) {

		float angle = (w * other.w) - (x * other.x + y * other.y + z * other.z);
		
		float crossX = y * other.z - z * other.y;
		float crossY = -(x * other.z - z * other.x);
		float crossZ = x * other.y - y * other.x;

		x = x * other.w + other.x * w + crossX;
		y = y * other.w + other.y * w + crossY;
		z = z * other.w + other.z * w + crossZ;
		w = angle;
	}
	
	public static void multiply(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2, Quaternion result) {
		float angle = (w1 * w2) - (x1 * x2 + y1 * y2 + z1 * z2);
		
		float crossX = y1 * z2 - z1 * y2;
		float crossY = -(x1 * z2 - z1 * x1);
		float crossZ = x1 * y2 - y1 * x2;

		result.x = x1 * w2 + x2 * w1 + crossX;
		result.y = y1 * w2 + y2 * w1 + crossY;
		result.z = z1 * w2 + z2 * w1 + crossZ;
		result.w = angle;
	}
	
	public Quaternion rotate(Vector3 axis, float angle) {
		Quaternion rotation = Quaternion.fromAxisAngle(axis, angle);
		Quaternion result = this.multiply(rotation);
		return result;
	}
	
	/**
	 * Converts this quaternion to a 4x4 matrix
	 * @return
	 */
	public float[] toMatrix() {
		
		float[] matrix = new float[16];
		
		toMatrix(matrix);
		  
		return matrix;
	}
	
	public void toMatrix(float[] matrix) {
		matrix[0]  = (1.0f - (2.0f * ((y * y) + (z * z))));
		matrix[1]  =         (2.0f * ((x * y) + (z * w)));
		matrix[2]  =         (2.0f * ((x * z) - (y * w)));
		matrix[3]  = 0.0f;
		matrix[4]  =         (2.0f * ((x * y) - (z * w)));
		matrix[5]  = (1.0f - (2.0f * ((x * x) + (z * z))));
		matrix[6]  =         (2.0f * ((y * z) + (x * w)));
		matrix[7]  = 0.0f;
		matrix[8]  =         (2.0f * ((x * z) + (y * w)));
		matrix[9]  =         (2.0f * ((y * z) - (x * w)));
		matrix[10] = (1.0f - (2.0f * ((x * x) + (y * y))));
		matrix[11] = 0.0f;
		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[14] = 0.0f;
		matrix[15] = 1.0f;
	}
	
	public static Quaternion getIdentity() {
		return new Quaternion(IDENTITY);
	}
	

	
	
	/** 
	 * Create a quaternion from an axis-angle rotation 
	 **/
	public static Quaternion fromAxisAngle(Vector3 axis, float angle) {
		return fromAxisAngle(axis.x, axis.y, axis.z, angle);
	}
	
	public static Quaternion fromAxisAngle(float x, float y, float z, float angle) {
		float length = Vector3.length(x, y, z);
		x /= length;
		y /= length;
		z /= length;
		
		Quaternion q = new Quaternion();
		
		float radiantAngle = 0.5f * angle * (float)Math.PI / 180;
		float sinAngle = (float) Math.sin(radiantAngle);
		
		q.x = x * sinAngle;
		q.y = y * sinAngle;
		q.z = z * sinAngle;
		q.w = (float) Math.cos(radiantAngle);
		
		return q;
	}

	/**
	 * Perform a spherical linear interpolation between the two quaternion q1 and q2
	 * @param start	The start rotation
	 * @param end	The end rotation
	 * @param alpha	The blending factor (0 <= alpha <= 1) with 
	 * 				0: 	start rotation and
	 * 				1:	end rotation
	 * @return		A quaternion representing the interpolated rotation
	 */
	public static Quaternion slerp(Quaternion start, Quaternion end, float alpha) {
		Quaternion result = new Quaternion();
		slerp(result, start, end, alpha);
		return result;
	}
	
	public static Quaternion slerp(Quaternion result, Quaternion start, Quaternion end, float alpha) {
		float diff = (start.x * end.x) + (start.y * end.y) + (start.z * end.z) * (start.w * end.w);
		
		float startWeight, endWeight;
		
		if (1.0f - Math.abs(diff) > SLERP_TO_LERP_SWITCH_THRESHOLD) {
			float theta = (float) Math.acos(Math.abs(diff));
			float oneOverTheta = 1.0f / (float) Math.sin(theta);
			
			startWeight = (float) Math.sin(theta * (1.0f - alpha)) * oneOverTheta;
			endWeight 	= (float) Math.sin(theta * alpha)			* oneOverTheta;
			
			if (diff < 0.0f) {
				startWeight = -startWeight;
			}
			
		} else {
			startWeight = 1.0f - alpha;
			endWeight = alpha;
		}
		
		result.x = start.x * startWeight + end.x * endWeight;
		result.y = start.y * startWeight + end.y * endWeight;
		result.z = start.z * startWeight + end.z * endWeight;
		result.w = start.w * startWeight + end.w * endWeight;
		
		result.normalize();
		
		return result;
	}
	
	public Vector3 multiply(Vector3 vector) {
		this.invert();

		float angle = - (vector.x * x + vector.y * y + vector.z * z);

		float crossX = vector.y * z - vector.z * y;
		float crossY = -(vector.x * z - vector.z * x);
		float crossZ = vector.x * y - vector.y * x;

		float resultX = vector.x * w + crossX;
		float resultY = vector.y * w + crossY;
		float resultZ = vector.z * w + crossZ;
		float resultW = angle;
		
		this.invert();
		
		angle = (w * resultW) - (x * resultX + y * resultY + z * resultZ);

		crossX = y * resultZ - z * resultY;
		crossY = -(x * resultZ - z * resultX);
		crossZ = x * resultY - y * resultX;
		
		Vector3 resultVector = new Vector3();
		resultVector.x = x * resultW + resultX * w +  crossX;
		resultVector.y = y * resultW + resultY * w + crossY;
		resultVector.z = z * resultW + resultZ * w + crossZ;

		return resultVector;
	}
	
	public void multiplyNoTmp(Vector3 vector) {
		  Quaternion vectorQuaternion = new Quaternion();
		  vectorQuaternion.x = vector.x;
		  vectorQuaternion.y = vector.y;
		  vectorQuaternion.z = vector.z;
		  vectorQuaternion.w = 0.0f;
		  
		  this.invert();
		  vectorQuaternion.multiplyNoTmp(this);
		  this.invert();
		  Quaternion resultQuaternion = this.multiply(vectorQuaternion);
		  
		  vector.x = resultQuaternion.x;
		  vector.y = resultQuaternion.y;
		  vector.z = resultQuaternion.z;
	}
	
	public static Quaternion rotateTo(Vector3 src, Vector3 dest) {
		Quaternion q= new Quaternion();
		Vector3 a = src.cross(dest);
		q.x = a.x;
		q.y = a.y;
		q.z = a.z;
		q.w = (float) Math.sqrt((src.length() * src.length()) * (dest.length() * dest.length())) + src.dot(dest);
		q.normalize();
		return q;
	}

}
