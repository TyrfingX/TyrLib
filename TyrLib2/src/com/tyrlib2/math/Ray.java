package com.tyrlib2.math;

public class Ray {
	public Vector3 direction;
	public Vector3 origin;
	
	public Ray(Vector3 direction, Vector3 origin) {
		this.direction = direction;
		this.origin = origin;
	}
	
	public Vector3 getPointAtDistance(float distance) {
		return origin.add(direction.multiply(distance));
	}
}
