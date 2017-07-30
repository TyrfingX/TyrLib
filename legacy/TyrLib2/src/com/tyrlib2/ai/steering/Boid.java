package com.tyrlib2.ai.steering;

import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/**
 * A very simple vehicle model made up simply by a point mass.
 * @author Sascha
 *
 */

public class Boid implements IVehicle {

	private SceneNode node;
	private float mass;
	private float inertia;
	private float velocity;
	private float maxVelocity;
	private float maxForce;
	private Vector3 steeringForces = new Vector3();
	private Vector3 steering;
	private Quaternion orientation;
	private Vector3 initForward;
	private Vector3 up;
	private Vector3 initUp;
	
	private static final float EPSILON = 0.001f;
	
	public Boid(float mass, float inertia, float maxVelocity, float maxForce, Vector3 steering, Vector3 up, SceneNode node) {
		this.mass = mass;
		this.inertia = inertia;
		this.node = node;
		this.maxVelocity = maxVelocity;
		this.maxForce = maxForce;
		this.steering = steering;
		this.initForward = steering;
		this.up = up;
		this.initUp = up;
		orientation = new Quaternion();
	}
	
	public Boid(Boid mass, SceneNode node) {
		this.mass = mass.mass;
		this.inertia = mass.inertia;
		this.node = node;
		this.maxVelocity = mass.maxVelocity;
		this.maxForce = mass.maxForce;
		this.steering = mass.steering;
		this.initForward = mass.steering;
		this.up = mass.up;
		this.initUp = mass.up;
		orientation = mass.orientation;
	}
	
	@Override
	public float getMass() {
		return mass;
	}

	@Override
	public Vector3 getPosition() {
		return node.getCachedAbsolutePos();
	}

	@Override
	public Vector3 getVelocity() {
		return steering.multiply(velocity);
	}

	@Override
	public float getMaxVelocity() {
		return maxVelocity;
	}
	
	@Override
	public float getMaxForce() {
		return maxForce;
	}
	
	public void setMaxForce(float maxForce) {
		this.maxForce = maxForce;
	}

	@Override
	public Quaternion getOrientation() {
		return orientation;
	}

	@Override
	public void accelerate(float velocity) {
		this.velocity += velocity;
	}

	@Override
	public void translate(Vector3 position) {
		node.translate(position);
	}
	
	@Override
	public void resetSteeringForces() {
		steeringForces.x = 0;
		steeringForces.y = 0;
		steeringForces.z = 0;
	}
	
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	@Override
	public void addSteeringForce(Vector3 steering) {
		steeringForces.x += steering.x;
		steeringForces.y += steering.y;
		steeringForces.z += steering.z;
	}

	@Override
	public void onUpdate(float time) {
		float totalForce = steeringForces.normalize();
		totalForce = Math.min(totalForce, maxForce);
		
		if (totalForce > 0) {
		
			float align = steeringForces.dot(steering) / (steeringForces.length() * steering.length());
			if (Math.abs(align) < EPSILON) {
				align = EPSILON;
			}
			
			Vector3 left = steering.cross(up);
			steeringForces.projectOnNormalized(left, up);
			
			steeringForces.x *= totalForce;
			steeringForces.y *= totalForce;
			steeringForces.z *= totalForce;
			
			float rotVelocity =  Math.signum(-align)*time*totalForce/inertia;
			
			if (align < -1 + EPSILON) {
				Vector3 axis = new Vector3((float)Math.random(), (float)Math.random(), (float)Math.random());
				axis.normalize();
				Quaternion rotation = Quaternion.fromAxisAngle(axis,rotVelocity);
				orientation = rotation.multiply(orientation);
				node.setRelativeRot(orientation);
				steering = orientation.multiply(initForward);
				up = orientation.multiply(initUp);
			} else if (align < 1 - EPSILON) {
				Vector3 rotAxis = steeringForces.cross(steering);
				rotAxis.normalize();
				Quaternion rotation = Quaternion.fromAxisAngle(rotAxis, rotVelocity);
				orientation = rotation.multiply(orientation);
				node.setRelativeRot(orientation);
				steering = orientation.multiply(initForward);
				up = orientation.multiply(initUp);
			}
			
			velocity += time * totalForce / mass;
			velocity = Math.min(velocity, maxVelocity);
			
		}
		
		steering.normalize();
		up.normalize();
		
		node.translate(steering.multiply(velocity*time));
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public Vector3 getForward() {
		Vector3 forward = new Vector3(steering);
		return forward;
	}

	@Override
	public Vector3 getInitForward() {
		return new Vector3(initForward);
	}

	@Override
	public void setOrientation(Quaternion orientation) {
		this.orientation = orientation;
		node.setRelativeRot(orientation);
		steering = orientation.multiply(initForward);
		up = orientation.multiply(initUp);
	}

	@Override
	public Vector3 getUp() {
		return up;
	}

}
