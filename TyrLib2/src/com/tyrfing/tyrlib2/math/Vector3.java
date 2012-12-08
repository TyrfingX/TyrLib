package com.tyrfing.tyrlib2.math;

import android.util.FloatMath;
/**
 * Basic vector object implementing 3D vector math.
 * @author Sascha
 *
 */

public class Vector3 {
	public float x;
	public float y;
	public float z;
	
	public Vector3(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
	
	/**
	 * Creates a vector pointing from this to the passed vector.
	 * @param	The reference point.
	 * @return	A vector pointing from this to the reference point.
	 */
	
	public Vector3 vectorTo(Vector3 other)
	{
		return new Vector3(other.x - this.x, other.y - this.y, other.z - this.z);
	}
	
	/**
	 * @return The length of this vector.
	 */

	public float length()
	{
		return FloatMath.sqrt(this.x * this.x + this.y * this.y + this.z *  this.z);
	}
	
	/**
	 * Sets the length of this vector to 1.
	 * @return The length from before normalizing the vector.
	 */
	
	public float normalize()
	{
		float length = this.length();
		if (length != 0)
		{
			x /= length;
			y /= length;
			z /= length;
		}
		return length;
	}
	
	/**
	 * Multiplies all components by a constant.
	 * @param m	The constant by which the components will be multiplied.
	 * @return	A new vector v with v = this * m.
	 */
	
	public Vector3 multiply(float m)
	{
		return new Vector3(x*m, y*m, z*m);
	}
	
	/**
	 * Adds this vector to another.
	 * @param other	The other vector which will be added
	 * @return		A new vector v with v = this + other.
	 */
	
	
	public Vector3 add(Vector3 other)
	{
		return new Vector3(other.x + this.x, other.y + this.y, other.z + this.z);
	}
	
	/**
	 * Let another vector subtract from this one.
	 * @param other	The other vector which will be subtracted from this.
	 * @return		A new vector v with v = this - other.
	 */
	
	public Vector3 sub(Vector3 other)
	{
		return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	/**
	 * Calculates the dot product between two vectors.
	 * @param other	The other vector.
	 * @return The value of the dot product between the two vectors.
	 */
	
	
	public float dot(Vector3 other)
	{
		return other.x * this.x + other.y * this.y + other.z * this.z;
	}
	
}
