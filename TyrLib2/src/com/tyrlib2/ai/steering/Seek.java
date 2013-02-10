package com.tyrlib2.ai.steering;

import com.tyrlib2.math.Vector3;

/**
 * Makes a vehicle seek a target destination
 * @author Sascha
 *
 */

public class Seek implements IPattern {

	private ITargetProvider targetProvider;
	
	public Seek(ITargetProvider targetProvider) {
		this.targetProvider = targetProvider;
	}
	
	@Override
	public void apply(IVehicle vehicle) {
		Vector3 target = targetProvider.getTargetPos();
		Vector3 desiredVelocity = vehicle.getPosition().sub(target);
		desiredVelocity.normalize();
		desiredVelocity = desiredVelocity.multiply(vehicle.getMaxVelocity());
		
		
		Vector3 velocity = vehicle.getVelocity();
		
		Vector3 steering = desiredVelocity.sub(velocity);
		steering.normalize();
		steering = steering.multiply(vehicle.getMaxForce());
		vehicle.addSteeringForce(steering);
	}
	
}
