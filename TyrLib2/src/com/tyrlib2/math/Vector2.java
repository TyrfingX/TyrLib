package com.tyrlib2.math;

import android.util.FloatMath;

/**
 * Basic vector object implementing 2D vector math.
 * @author Sascha
 *
 */

public class Vector2 {
	public float x;
	public float y;
	
	public Vector2(float x, float y) { this.x = x; this.y = y; }
	public Vector2(Vector2 other) { this.x = other.x; this.y = other.y; }
	public Vector2() { this(0,0); }
	
	/**
	 * Creates a vector pointing from this to the passed vector.
	 * @param	The reference point.
	 * @return	A vector pointing from this to the reference point.
	 */
	
	public Vector2 vectorTo(Vector2 other)
	{
		return new Vector2(other.x - this.x, other.y - this.y);
	}

	/**
	 * @return The length of this vector.
	 */
	
	public float length()
	{
		return FloatMath.sqrt(this.x * this.x + this.y * this.y);
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
		}
		return length;
	}
	
	/**
	 * Multiplies all components by a constant.
	 * @param m	The constant by which the components will be multiplied.
	 * @return	A new vector v with v = this * m.
	 */
	
	public Vector2 multiply(float m)
	{
		return new Vector2(x*m, y*m);
	}
	
	/**
	 * @return The magnitude of the vector.
	 */
	
	public float magnitude()
	{
		return this.y / this.x;
	}
	
	/**
	 * Adds this vector to another.
	 * @param other	The other vector which will be added
	 * @return		A new vector v with v = this + other.
	 */
	
	public Vector2 add(Vector2 other)
	{
		return new Vector2(other.x + this.x, other.y + this.y);
	}
	
	/**
	 * Let another vector subtract from this one.
	 * @param other	The other vector which will be subtracted from this.
	 * @return		A new vector v with v = this - other.
	 */
	
	public Vector2 sub(Vector2 other)
	{
		return new Vector2(this.x - other.x, this.y - other.y);
	}
	/**
	 * Calculates the dot product between two vectors.
	 * @param other	The other vector.
	 * @return The value of the dot product between the two vectors.
	 */
	
	public float dot(Vector2 other)
	{
		return other.x * this.x + other.y * this.y;
	}
	
}
