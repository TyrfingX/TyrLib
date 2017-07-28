package com.tyrlib2.physics;

import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

public class PointMass {
	public SceneNode node;
	public Vector3 speed;
	public float mass;
	
	public PointMass(float mass) {
		this.node = new SceneNode();
		this.speed = new Vector3();
		this.mass = mass;
	}
	
	public PointMass(SceneNode node, Vector3 speed, float mass) {
		this.node = node;
		this.speed = speed;
		this.mass = mass;
	}
	
	public Vector3 calcTrajectoryLaunch(float g, Vector3 targetPoint) {
		float v = speed.length();
		Vector3 dist = node.getRelativePos().vectorTo(targetPoint);
		float x = Vector3.length(dist.x, dist.y, 0);
		float y = dist.z;
		
		float root = (float) Math.sqrt(v*v*v*v - g*(g*x*x+2*y*v*v));
		float angle1 = (float) Math.toDegrees(Math.atan((v*v + root) / (g*x)));
		float angle2 = (float) Math.toDegrees(Math.atan((v*v - root) / (g*x)));
		
		float angle = Math.max(angle1, angle2);
		
		dist.normalize();
		Vector3 right = dist.cross(Vector3.UNIT_Z);
		Quaternion rot = Quaternion.fromAxisAngle(right, angle);
		
		Vector3 launch = rot.multiply(dist).multiply(v);
		return launch;
	}
	
	public Vector3 calcTrajectoryLaunch(float g, float angle, Vector3 targetPoint) {
		Vector3 dist = node.getRelativePos().vectorTo(targetPoint);
		float x = Vector3.length(dist.x, dist.y, 0);
		float y = 0;
		double o = Math.toRadians(angle);
		
		float speed = (float) ((Math.sqrt(g) * Math.sqrt(x) * Math.sqrt((Math.tan(o)*Math.tan(o))+1)) / Math.sqrt(2 * Math.tan(o) - (2 * g * y) / x));
		
		dist.normalize();
		Vector3 right = dist.cross(Vector3.UNIT_Z);
		Quaternion rot = Quaternion.fromAxisAngle(right, angle);
		
		Vector3 launch = rot.multiply(dist).multiply(speed);
		return launch;
	}
}
