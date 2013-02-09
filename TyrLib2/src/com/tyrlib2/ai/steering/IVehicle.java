package com.tyrlib2.ai.steering;

import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/**
 * An interface for vehicles which will receive steer commands from 
 * the Behaviour layer.
 * @author Sascha
 *
 */

public interface IVehicle {
	public float getMass();
	public Vector3 getPosition();
	public Vector3 getVelocity();
	public Quaternion getOrientation();
	
	public void accelerate(Vector3 velocity);
	public void translate(Vector3 position);
}
