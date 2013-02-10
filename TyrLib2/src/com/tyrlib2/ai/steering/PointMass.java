package com.tyrlib2.ai.steering;

import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/**
 * A very simple vehicle model made up simply by a point mass.
 * @author Sascha
 *
 */

public class PointMass implements IVehicle {

	private SceneNode node;
	private float mass;
	private float inertia;
	private float velocity;
	private float maxVelocity;
	private float maxForce;
	private Vector3 steeringForces = new Vector3();
	private Vector3 steering;
	private Quaternion orientation;
	
	private static final float EPSILON = 0.001f;
	
	public PointMass(float mass, float inertia, float maxVelocity, float maxForce, Vector3 steering, SceneNode node) {
		this.mass = mass;
		this.inertia = inertia;
		this.node = node;
		this.maxVelocity = maxVelocity;
		this.maxForce = maxForce;
		this.steering = steering;
		orientation = new Quaternion();
	}
	
	@Override
	public float getMass() {
		return mass;
	}

	@Override
	public Vector3 getPosition() {
		return node.getAbsolutePos();
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
		steeringForces = new Vector3();
	}

	@Override
	public void addSteeringForce(Vector3 steering) {
		steeringForces = steeringForces.add(steering);
	}

	@Override
	public void onUpdate(float time) {
		float totalForce = steeringForces.normalize();
		totalForce = Math.min(totalForce, maxForce);
		
		steeringForces = steeringForces.multiply(totalForce);
		
		float align = steeringForces.dot(steering) / (steeringForces.length() * steering.length());
		
		if (align < -1 + EPSILON) {
			Vector3 axis = new Vector3((float)Math.random(), (float)Math.random(), (float)Math.random());
			axis.normalize();
			Quaternion rotation = Quaternion.fromAxisAngle(axis, Math.signum(-align)*time*totalForce/inertia);
			node.rotate(rotation);
			steering = rotation.multiply(steering);
			orientation = orientation.multiply(rotation);
		} else if (align < 1 - EPSILON) {
			Vector3 rotAxis = steeringForces.cross(steering);
			rotAxis.normalize();
			Quaternion rotation = Quaternion.fromAxisAngle(rotAxis, Math.signum(-align)*time*totalForce/inertia);
			node.rotate(rotation);
			steering = rotation.multiply(steering);
			orientation = orientation.multiply(rotation);
		}
		
		velocity += time * totalForce / mass;
		velocity = Math.min(velocity, maxVelocity);
		
		steering.normalize();
		
		node.translate(steering.multiply(velocity*time));
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
