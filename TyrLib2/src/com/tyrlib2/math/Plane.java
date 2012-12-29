package com.tyrlib2.math;

public class Plane {
	
	public Vector3 normal;
	public Vector3 point;
	
	public Plane(Vector3 normal, Vector3 point) {
		normal.normalize();
		this.normal = normal;
		this.point = point;
	}
	
	public float distance(Vector3 point) {
		float distance = normal.dot(point.sub(this.point));
		return distance;
	}
	
	
}
