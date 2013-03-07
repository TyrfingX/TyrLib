package com.tyrlib2.ai.steering;

import com.tyrlib2.game.ITargetProvider;
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
	public Vector3 apply(IVehicle vehicle) {
		Vector3 target = targetProvider.getTargetPos();
		
		Vector3 desiredVelocity = vehicle.getPosition();
		desiredVelocity.x -= target.x;
		desiredVelocity.y -= target.y;
		desiredVelocity.z -= target.z;
		
		desiredVelocity.normalize();
		desiredVelocity.x *= vehicle.getMaxVelocity();
		desiredVelocity.y *= vehicle.getMaxVelocity();
		desiredVelocity.z *= vehicle.getMaxVelocity();
		
		Vector3 velocity = vehicle.getVelocity();
		
		Vector3 steering = desiredVelocity.sub(velocity);
		steering.normalize();
		steering = steering.multiply(vehicle.getMaxForce());
		
		return steering;
	}
	
}
