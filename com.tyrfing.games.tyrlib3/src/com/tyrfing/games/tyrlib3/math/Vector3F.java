package com.tyrfing.games.tyrlib3.math;

import java.io.Serializable;

/**
 * Basic vector object implementing 3D vector math.
 * @author Sascha
 *
 */

public class Vector3F implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5645860908411944792L;
	
	public static final Vector3F UNIT_X = new Vector3F(1,0,0);
	public static final Vector3F UNIT_Y = new Vector3F(0,1,0);
	public static final Vector3F UNIT_Z = new Vector3F(0,0,1);
	public static final Vector3F ORIGIN = new Vector3F(0,0,0);
	
	public float x;
	public float y;
	public float z;
	
	public Vector3F(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
	public Vector3F(Vector3F other) { this.x = other.x; this.y = other.y; this.z = other.z; }
	public Vector3F() { this(0,0,0); }
	
	/**
	 * Creates a vector pointing from this to the passed vector.
	 * @param	The reference point.
	 * @return	A vector pointing from this to the reference point.
	 */
	
	public Vector3F vectorTo(Vector3F other)
	{
		return new Vector3F(other.x - this.x, other.y - this.y, other.z - this.z);
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3F v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public void setScaled(Vector3F v, float scale) {
		this.x = v.x * scale;
		this.y = v.y * scale;
		this.z = v.z * scale;
	}
	
	/**
	 * @return The length of this vector.
	 */

	public float length()
	{
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z *  this.z);
	}
	
	/**
	 * @return Squared length of this vector.
	 */
	
	public float squaredLength() {
		return this.x * this.x + this.y * this.y + this.z *  this.z;
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
	
	public Vector3F unitVector() {
		float length = this.length();
		if (length != 0)
		{
			return new Vector3F(x/length, y/length, z/length);
		}
		return new Vector3F(0,0,0);
	}
	
	/**
	 * Multiplies all components by a constant.
	 * @param m	The constant by which the components will be multiplied.
	 * @return	A new vector v with v = this * m.
	 */
	
	public Vector3F multiply(float m)
	{
		return new Vector3F(x*m, y*m, z*m);
	}
	
	public Vector3F multiply(Vector3F other)
	{
		return new Vector3F(x*other.x, y*other.y, z*other.z);
	}
	
	/**
	 * Adds this vector to another.
	 * @param other	The other vector which will be added
	 * @return		A new vector v with v = this + other.
	 */
	
	
	public Vector3F add(Vector3F other)
	{
		return new Vector3F(other.x + this.x, other.y + this.y, other.z + this.z);
	}
	
	/**
	 * Let another vector subtract from this one.
	 * @param other	The other vector which will be subtracted from this.
	 * @return		A new vector v with v = this - other.
	 */
	
	public Vector3F sub(Vector3F other)
	{
		return new Vector3F(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	/**
	 * Calculates the dot product between two vectors.
	 * @param other	The other vector.
	 * @return The value of the dot product between the two vectors.
	 */
	
	
	public float dot(Vector3F other)
	{
		return other.x * this.x + other.y * this.y + other.z * this.z;
	}
	
	/**
	 * Calculates the cross product between this vector
	 * and another
	 * @param other	The other vector	
	 * @return		A new vector3 perpendicular to this and the 
	 * 				passed vector
	 */
	
	public Vector3F cross(Vector3F other) {
		Vector3F normal = new Vector3F(y * other.z - z * other.y, 
									 -(x * other.z - z * other.x),
									 x * other.y - y * other.x);
		return normal;
	}
	
	/**
	 * Perform a linear interpolation between two vectors.
	 * @param start	The start vector
	 * @param end	The end vector
	 * @param alpha	The interpolation point (0 <= alpha <= 1)
	 * @return		The interpolated vector
	 */
	public static Vector3F lerp(Vector3F start, Vector3F end, float alpha) {
		float x = start.x + (end.x - start.x) * alpha;
		float y = start.y + (end.y - start.y) * alpha;
		float z = start.z + (end.z - start.z) * alpha;
		return new Vector3F(x,y,z);
	}
	
	public static void lerp(Vector3F start, Vector3F end, float alpha, Vector3F result) {
		result.x = start.x + (end.x - start.x) * alpha;
		result.y = start.y + (end.y - start.y) * alpha;
		result.z = start.z + (end.z - start.z) * alpha;
	}
	
	public void projectOnNormalized(Vector3F u, Vector3F v) {
		float dotThisU = this.dot(u);
		float dotThisV = this.dot(v);
		
		x = dotThisU * u.x + dotThisV * v.x;
		y = dotThisU * u.y + dotThisV * v.y;
		z = dotThisU * u.z + dotThisV * v.z;
	}
	
	@Override
	public String toString() {
		return x + ", " +  y + ", " + z;
	}
	
	public static Vector3F cross(float x1, float y1, float z1, float x2, float y2, float z2) {
		return  new Vector3F(y1 * z2 - z1 * y2, 
				 			-(x1 * z2 - z1 * x2),
				 			x1 * y2 - y1 * x2);
	}
	
	public static void cross(Vector3F target, float x1, float y1, float z1, float x2, float y2, float z2) {
		target.x = y1 * z2 - z1 * y2; 
		target.y = -(x1 * z2 - z1 * x2);
		target.z = x1 * y2 - y1 * x2;
	}
	
	public static void cross(Vector3F v1, Vector3F v2, Vector3F target) {
		target.x = v1.y * v2.z - v1.z * v2.y;
		target.y = -(v1.x * v2.z - v1.z * v2.x);
		target.z = v1.x * v2.y - v1.y * v2.x;
	}
	
	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(x*x +  y*y + z*z);
	}
	
	public static void add(Vector3F v1, Vector3F v2, Vector3F target) {
		target.x = v1.x + v2.x;
		target.y = v1.y + v2.y;
		target.z = v1.z + v2.z;
	}
	
	public static void add(Vector3F v1, float x, float y, float z, Vector3F target) {
		target.x = v1.x + x;
		target.y = v1.y + y;
		target.z = v1.z + z;
	}
	
	public static void sub(Vector3F v1, Vector3F v2, Vector3F target) {
		target.x = v1.x - v2.x;
		target.y = v1.y - v2.y;
		target.z = v1.z - v2.z;
	}
	
	public static void vectorTo(Vector3F from, Vector3F to, Vector3F target) {
		target.x = to.x - from.x;
		target.y = to.y - from.y;
		target.z = to.z - from.z;
	}
	public static void multiply(float f, Vector3F v) {
		v.x = v.x * f;
		v.y = v.y * f;
		v.z = v.z * f;
	}
	public static void addScaled(Vector3F v1, Vector3F v2, float scale, Vector3F target) {
		target.x = v1.x + v2.x * scale;
		target.y = v1.y + v2.y * scale;
		target.z = v1.z + v2.z * scale;
	}
	public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1*x2+y1*y2+z1*z2;
	}

	
}
