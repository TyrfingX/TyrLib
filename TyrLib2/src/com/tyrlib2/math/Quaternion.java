package com.tyrlib2.math;


/**
 * Represents a rotation in 3D space
 * @author Sascha
 *
 */

public class Quaternion {
	public float angle;
	public float rotX;
	public float rotY;
	public float rotZ;
	
	public Quaternion(float angle, float rotX, float rotY, float rotZ) {
		Vector3 tmp = new Vector3(rotX, rotY, rotZ);
		tmp.normalize();
		this.rotX = tmp.x;
		this.rotY = tmp.y;
		this.rotZ = tmp.z;
		this.angle = angle;
		this.clamp();
	}
	
	public Quaternion(Quaternion quaternion) {
		this.rotX = quaternion.rotX;
		this.rotY = quaternion.rotY;
		this.rotZ = quaternion.rotZ;
		this.angle = quaternion.angle;
		this.clamp();
	}
	
	public Quaternion() {
		this(0,0,0,0);
	}
	
	public Quaternion(float angle, Vector3 axis) {
		this(angle, axis.x, axis.y, axis.z);
	}
	
	public Quaternion add(Quaternion other) {
		Quaternion quaternion = new Quaternion(angle + other.angle, rotX + other.rotX, rotY + other.rotY, rotZ + other.rotZ);
		return quaternion;
	}
	
	public Quaternion multiply(Quaternion other) {
		Vector3 axisThis = new Vector3(rotX, rotY, rotZ);
		Vector3 axisOther = new Vector3(other.rotX, other.rotY, other.rotZ);
		float newAngle = angle * other.angle - axisThis.dot(axisOther);
		Vector3 newAxis = axisOther.multiply(angle).add(axisThis.multiply(other.angle)).add(axisThis.cross(axisOther));
		return new Quaternion(newAngle, newAxis);
	}
	
	public Vector3 multiply(Vector3 vector) {
		Vector3 axisThis = new Vector3(rotX, rotY, rotZ);
		Quaternion rotator = new Quaternion((float)Math.cos(angle/2), axisThis.multiply((float)Math.sin(angle/2)));
		Quaternion other = new Quaternion(0, vector);
		Quaternion tmp = rotator.multiply(other).multiply(rotator.conjugate());
		Vector3 rotatedVector = new Vector3(tmp.rotX, tmp.rotY, tmp.rotZ);
		return rotatedVector;
	}
	
	public Quaternion conjugate() {
		return new Quaternion(angle, -rotX, -rotY, -rotZ);
	}
	
	/**
	 * Clamps the angle to [-2pi, 2pi]
	 */
	
	public void clamp() {
		int factor = (int) (angle / 360);
		angle -= factor * 360;
	}
}
