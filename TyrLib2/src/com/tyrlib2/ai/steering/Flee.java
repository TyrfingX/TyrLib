package com.tyrlib2.ai.steering;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.movement.ITargetProvider;

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
			float scalar = range * range / (distance * distance);
			scalar = Math.min(vehicle.getMaxForce(), scalar);
			steering.x *= scalar;
			steering.y *= scalar;
			steering.z *= scalar;
		} 
		
		return steering;
	}
	
	public static Vector3 apply(ITargetProvider targetProvider, float range, IVehicle vehicle){
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
