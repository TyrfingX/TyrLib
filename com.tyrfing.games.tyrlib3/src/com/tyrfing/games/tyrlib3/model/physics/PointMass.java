package com.tyrfing.games.tyrlib3.model.physics;

import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.math.Quaternion;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;

public class PointMass {
	public SceneNode node;
	public Vector3F speed;
	public float mass;
	
	public PointMass(float mass) {
		this.node = new SceneNode();
		this.speed = new Vector3F();
		this.mass = mass;
	}
	
	public PointMass(SceneNode node, Vector3F speed, float mass) {
		this.node = node;
		this.speed = speed;
		this.mass = mass;
	}
	
	public Vector3F calcTrajectoryLaunch(float g, Vector3F targetPoint) {
		float v = speed.length();
		Vector3F dist = node.getRelativePos().vectorTo(targetPoint);
		float x = Vector3F.length(dist.x, dist.y, 0);
		float y = dist.z;
		
		float root = (float) Math.sqrt(v*v*v*v - g*(g*x*x+2*y*v*v));
		float angle1 = (float) Math.toDegrees(Math.atan((v*v + root) / (g*x)));
		float angle2 = (float) Math.toDegrees(Math.atan((v*v - root) / (g*x)));
		
		float angle = Math.max(angle1, angle2);
		
		dist.normalize();
		Vector3F right = dist.cross(Vector3F.UNIT_Z);
		Quaternion rot = Quaternion.fromAxisAngle(right, angle);
		
		Vector3F launch = rot.multiply(dist).multiply(v);
		return launch;
	}
	
	public Vector3F calcTrajectoryLaunch(float g, float angle, Vector3F targetPoint) {
		Vector3F dist = node.getRelativePos().vectorTo(targetPoint);
		float x = Vector3F.length(dist.x, dist.y, 0);
		float y = 0;
		double o = Math.toRadians(angle);
		
		float speed = (float) ((Math.sqrt(g) * Math.sqrt(x) * Math.sqrt((Math.tan(o)*Math.tan(o))+1)) / Math.sqrt(2 * Math.tan(o) - (2 * g * y) / x));
		
		dist.normalize();
		Vector3F right = dist.cross(Vector3F.UNIT_Z);
		Quaternion rot = Quaternion.fromAxisAngle(right, angle);
		
		Vector3F launch = rot.multiply(dist).multiply(speed);
		return launch;
	}
}
