package com.tyrfing.games.tyrlib3.model.math;

import java.io.Serializable;


/**
 * Basic vector object implementing 2D vector math.
 * @author Sascha
 *
 */

public class Vector2F implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 220971957145805318L;
	public float x;
	public float y;
	
	public Vector2F(float x, float y) { this.x = x; this.y = y; }
	public Vector2F(Vector2F other) { this.x = other.x; this.y = other.y; }
	public Vector2F() { this(0,0); }
	public Vector2F(String x, String y) { this(Float.valueOf(x), Float.valueOf(y)); }
	
	/**
	 * Creates a vector pointing from this to the passed vector.
	 * @param	The reference point.
	 * @return	A vector pointing from this to the reference point.
	 */
	
	public Vector2F vectorTo(Vector2F other)
	{
		return new Vector2F(other.x - this.x, other.y - this.y);
	}

	/**
	 * @return The length of this vector.
	 */
	
	public float length()
	{
		return (float) Math.sqrt(this.x * this.x + this.y * this.y);
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
	
	public Vector2F multiply(float m)
	{
		return new Vector2F(x*m, y*m);
	}
	
	public Vector2F multiply(float m1, float m2)
	{
		return new Vector2F(x*m1, y*m2);
	}
	
	public Vector2F scale(float x, float y)
	{
		this.x *= x;
		this.y *= y;
		
		return this;
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
	
	public Vector2F add(Vector2F other)
	{
		return new Vector2F(other.x + this.x, other.y + this.y);
	}

	public Vector2F add(float x, float y)
	{
		return new Vector2F(x + this.x, y + this.y);
	}
	
	/**
	 * Let another vector subtract from this one.
	 * @param other	The other vector which will be subtracted from this.
	 * @return		A new vector v with v = this - other.
	 */
	
	public Vector2F sub(Vector2F other)
	{
		return new Vector2F(this.x - other.x, this.y - other.y);
	}
	/**
	 * Calculates the dot product between two vectors.
	 * @param other	The other vector.
	 * @return The value of the dot product between the two vectors.
	 */
	
	public float dot(Vector2F other)
	{
		return other.x * this.x + other.y * this.y;
	}
	
	public Vector2F copy() {
		return new Vector2F(this);
	}
	
}
