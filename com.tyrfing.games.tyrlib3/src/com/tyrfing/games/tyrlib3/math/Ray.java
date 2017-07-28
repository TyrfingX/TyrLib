package com.tyrfing.games.tyrlib3.math;

public class Ray {
	public Vector3F direction;
	public Vector3F origin;
	
	public Ray(Vector3F direction, Vector3F origin) {
		this.direction = direction;
		this.origin = origin;
	}
	
	public Vector3F getPointAtDistance(float distance) {
		return origin.add(direction.multiply(distance));
	}
}
