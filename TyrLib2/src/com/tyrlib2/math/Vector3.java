package com.tyrlib2.math;

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
	public Vector3(Vector3 other) { this.x = other.x; this.y = other.y; this.z = other.z; }
	public Vector3() { this(0,0,0); }
	
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
	
	/**
	 * Multiplies all components by a constant.
	 * @param m	The constant by which the components will be multiplied.
	 * @return	A new vector v with v = this * m.
	 */
	
	public Vector3 multiply(float m)
	{
		return new Vector3(x*m, y*m, z*m);
	}
	
	public Vector3 multiply(Vector3 other)
	{
		return new Vector3(x*other.x, y*other.y, z*other.z);
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
	
	/**
	 * Calculates the cross product between this vector
	 * and another
	 * @param other	The other vector	
	 * @return		A new vector3 perpendicular to this and the 
	 * 				passed vector
	 */
	
	public Vector3 cross(Vector3 other) {
		Vector3 normal = new Vector3(y * other.z - z * other.y, 
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
	public static Vector3 lerp(Vector3 start, Vector3 end, float alpha) {
		float x = start.x + (end.x - start.x) * alpha;
		float y = start.y + (end.y - start.y) * alpha;
		float z = start.z + (end.z - start.z) * alpha;
		return new Vector3(x,y,z);
	}
	
	public static void lerp(Vector3 start, Vector3 end, float alpha, Vector3 result) {
		result.x = start.x + (end.x - start.x) * alpha;
		result.y = start.y + (end.y - start.y) * alpha;
		result.z = start.z + (end.z - start.z) * alpha;
	}
	
	public void projectOnNormalized(Vector3 u, Vector3 v) {
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
	
	public static Vector3 cross(float x1, float y1, float z1, float x2, float y2, float z2) {
		return  new Vector3(y1 * z2 - z1 * y2, 
				 			-(x1 * z2 - z1 * x2),
				 			x1 * y2 - y1 * x2);
	}
	
	public static void cross(Vector3 target, float x1, float y1, float z1, float x2, float y2, float z2) {
		target.x = y1 * z2 - z1 * y2; 
		target.y = -(x1 * z2 - z1 * x2);
		target.z = x1 * y2 - y1 * x2;
	}
	
	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(x*x +  y*y + z*z);
	}

	
}
