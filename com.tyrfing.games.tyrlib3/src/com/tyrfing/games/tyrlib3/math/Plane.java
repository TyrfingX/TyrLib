package com.tyrfing.games.tyrlib3.math;

public class Plane {
	
	public Vector3F normal;
	public float x, y, z;
	
	public Plane(Vector3F normal, Vector3F point) {
		normal.normalize();
		this.normal = normal;
		x = point.x;
		y = point.y;
		z = point.z;
	}
	
	public Plane(Vector3F normal, float x, float y, float z) {
		normal.normalize();
		this.normal = normal;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float distance(Vector3F point) {
		float distance = normal.x * (point.x - this.x) +
						 normal.y * (point.y - this.y) + 
						 normal.z * (point.z - this.z);
		return distance;
	}

	public void set(Vector3F point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
	}
	
	
}
