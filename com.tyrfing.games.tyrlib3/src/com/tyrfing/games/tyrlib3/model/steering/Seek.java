package com.tyrfing.games.tyrlib3.model.steering;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.model.movement.ITargetProvider;

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
	public Vector3F apply(IVehicle vehicle) {
		Vector3F target = targetProvider.getTargetPos();
		
		Vector3F desiredVelocity = vehicle.getPosition();
		desiredVelocity.x -= target.x;
		desiredVelocity.y -= target.y;
		desiredVelocity.z -= target.z;
		
		desiredVelocity.normalize();
		desiredVelocity.x *= vehicle.getMaxVelocity();
		desiredVelocity.y *= vehicle.getMaxVelocity();
		desiredVelocity.z *= vehicle.getMaxVelocity();
		
		Vector3F velocity = vehicle.getVelocity();
		
		Vector3F steering = desiredVelocity.sub(velocity);
		steering.normalize();
		steering = steering.multiply(-vehicle.getMaxForce());
		
		return steering;
	}
	
}
