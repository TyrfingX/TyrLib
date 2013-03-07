package com.tyrlib2.ai.steering;

import com.tyrlib2.game.ITargetProvider;
import com.tyrlib2.math.Vector3;

public class Flee implements IPattern {

	private ITargetProvider targetProvider;
	private float range;
	
	public Flee(ITargetProvider targetProvider, float range) {
		this.targetProvider = targetProvider;
		this.range = range;
	}
	
	@Override
	public Vector3 apply(IVehicle vehicle) {
		Vector3 target = targetProvider.getTargetPos();
		Vector3 desiredVelocity = target.sub(vehicle.getPosition());
		float distance = desiredVelocity.normalize();
		desiredVelocity = desiredVelocity.multiply(vehicle.getMaxVelocity());
		
		
		Vector3 velocity = vehicle.getVelocity();
		
		Vector3 steering = velocity.sub(desiredVelocity);
		steering.normalize();
		
		if (distance != 0) {
			steering = steering.multiply(range * vehicle.getMaxForce() / (distance*distance));
		} 
		
		return steering;
	}
	
}
