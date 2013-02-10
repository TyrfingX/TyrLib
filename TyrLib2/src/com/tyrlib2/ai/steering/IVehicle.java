package com.tyrlib2.ai.steering;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/**
 * An interface for vehicles which will receive steer commands from 
 * the Behaviour layer.
 * @author Sascha
 *
 */

public interface IVehicle extends IUpdateable {
	public float getMass();
	public Vector3 getPosition();
	public Vector3 getVelocity();
	public float getMaxVelocity();
	public float getMaxForce();
	public Quaternion getOrientation();
	
	public void accelerate(float velocity);
	public void translate(Vector3 position);
	
	/**
	 * Reset the accumulated steering forces
	 */
	public void resetSteeringForces();
	
	/**
	 * Add a new steering force
	 * @param steering The new steering force
	 */
	public void addSteeringForce(Vector3 steering);
}
