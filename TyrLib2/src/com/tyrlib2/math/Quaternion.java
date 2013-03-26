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
		return FloatMath.sqrt(x*x + y*y + z*z + w*w);
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
		Vector3 newAxis = new Vector3(axis);
		newAxis.normalize();
		
		Quaternion q = new Quaternion();
		
		float radiantAngle = 0.5f * angle * (float)Math.PI / 180;
		float sinAngle = FloatMath.sin(radiantAngle);
		
		q.x = newAxis.x * sinAngle;
		q.y = newAxis.y * sinAngle;
		q.z = newAxis.z * sinAngle;
		q.w = FloatMath.cos(radiantAngle);
		
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
		float diff = (start.x * end.x) + (start.y * end.y) + (start.z * end.z) * (start.w * end.w);
		
		float startWeight, endWeight;
		
		if (1.0f - Math.abs(diff) > SLERP_TO_LERP_SWITCH_THRESHOLD) {
			float theta = (float) Math.acos(Math.abs(diff));
			float oneOverTheta = 1.0f / FloatMath.sin(theta);
			
			startWeight = FloatMath.sin(theta * (1.0f - alpha)) * oneOverTheta;
			endWeight 	= FloatMath.sin(theta * alpha)			* oneOverTheta;
			
			if (diff < 0.0f) {
				startWeight = -startWeight;
			}
			
		} else {
			startWeight = 1.0f - alpha;
			endWeight = alpha;
		}
		
		Quaternion result = new Quaternion();
		result.x = start.x * startWeight + end.x * endWeight;
		result.y = start.y * startWeight + end.y * endWeight;
		result.z = start.z * startWeight + end.z * endWeight;
		result.w = start.w * startWeight + end.w * endWeight;
		
		result.normalize();
		
		return result;
	}
	
	public Vector3 multiply(Vector3 vector) {
		  Quaternion vectorQuaternion = new Quaternion();
		  vectorQuaternion.x = vector.x;
		  vectorQuaternion.y = vector.y;
		  vectorQuaternion.z = vector.z;
		  vectorQuaternion.w = 0.0f;
		  
		  this.invert();
		  vectorQuaternion.multiplyNoTmp(this);
		  this.invert();
		  Quaternion result = this.multiply(vectorQuaternion);
		  
		  Vector3 resultVector = new Vector3();
		  resultVector.x = result.x;
		  resultVector.y = result.y;
		  resultVector.z = result.z;
		  
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

}
