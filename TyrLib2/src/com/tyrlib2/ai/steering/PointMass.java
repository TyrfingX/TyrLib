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
	private Vector3 velocity;
	
	public PointMass(float mass, SceneNode node) {
		this.mass = mass;
		this.node = node;
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
		return velocity;
	}

	@Override
	public Quaternion getOrientation() {
		return node.getAbsoluteRot();
	}

	@Override
	public void accelerate(Vector3 velocity) {
		this.velocity = this.velocity.add(velocity);
	}

	@Override
	public void translate(Vector3 position) {
		node.translate(position);
	}

}
