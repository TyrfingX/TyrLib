package com.tyrfing.games.tyrlib3.steering;

import com.tyrfing.games.tyrlib3.game.IUpdateable;
import com.tyrfing.games.tyrlib3.math.Quaternion;
import com.tyrfing.games.tyrlib3.math.Vector3F;

/**
 * An interface for vehicles which will receive steer commands from 
 * the Behaviour layer.
 * @author Sascha
 *
 */

public interface IVehicle extends IUpdateable {
	public float getMass();
	public Vector3F getPosition();
	public Vector3F getVelocity();
	public float getMaxVelocity();
	public float getMaxForce();
	public Vector3F getForward();
	public Vector3F getInitForward();
	public Vector3F getUp();
	public Quaternion getOrientation();
	public void setOrientation(Quaternion orientation);
	
	public void accelerate(float velocity);
	public void translate(Vector3F position);
	
	/**
	 * Reset the accumulated steering forces
	 */
	public void resetSteeringForces();
	
	/**
	 * Add a new steering force
	 * @param steering The new steering force
	 */
	public void addSteeringForce(Vector3F steering);
}
