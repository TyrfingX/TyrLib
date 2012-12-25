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
		float length = 1.0f / length();
		Quaternion inverse = new Quaternion(-length, -length, -length, length);
		return inverse;
	}
	
	/**
	 * Multiply this quaternion with another
	 * @param other
	 * @return	The resulting quaternion
	 */
	public Quaternion multiply(Quaternion other) {

		Vector3 vector1 = new Vector3(x,y,z);
		Vector3 vector2 = new Vector3(other.x,other.y,other.z);

		float angle = (w * other.w) - vector1.dot(vector2);

		Vector3 cross = vector1.cross(vector2);
		
		vector1 = vector1.multiply(other.w);
		vector2 = vector2.multiply(w);
		
		Quaternion result = new Quaternion();
		result.x = vector1.x + vector2.x + cross.x;
		result.y = vector1.y + vector2.y + cross.y;
		result.z = vector1.z + vector2.z + cross.z;
		result.w = angle;
		
		return result;
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
		
		matrix[0]  = (1.0f - (2.0f * ((y * y) + (z * z))));
		matrix[1]  =         (2.0f * ((x *y) + (z * w)));
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
		  
		return matrix;
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
}
