package com.tyrlib2.math;

public class Plane {
	
	public Vector3 normal;
	public float x, y, z;
	
	public Plane(Vector3 normal, Vector3 point) {
		normal.normalize();
		this.normal = normal;
		x = point.x;
		y = point.y;
		z = point.z;
	}
	
	public Plane(Vector3 normal, float x, float y, float z) {
		normal.normalize();
		this.normal = normal;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float distance(Vector3 point) {
		float distance = normal.x * (point.x - this.x) +
						 normal.y * (point.y - this.y) + 
						 normal.z * (point.z - this.z);
		return distance;
	}
	
	
}
