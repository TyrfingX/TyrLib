package com.tyrfing.games.tyrlib3.model.steering;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.model.movement.ITargetProvider;

public class Flee implements IPattern {

	private ITargetProvider targetProvider;
	private float range;
	
	public Flee(ITargetProvider targetProvider, float range) {
		this.targetProvider = targetProvider;
		this.range = range;
	}
	
	@Override
	public Vector3F apply(IVehicle vehicle) {
		Vector3F target = targetProvider.getTargetPos();
		Vector3F desiredVelocity = target.sub(vehicle.getPosition());
		float distance = desiredVelocity.normalize();
		desiredVelocity = desiredVelocity.multiply(vehicle.getMaxVelocity());
		
		
		Vector3F velocity = vehicle.getVelocity();
		
		Vector3F steering = velocity.sub(desiredVelocity);
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
	
	public static Vector3F apply(ITargetProvider targetProvider, float range, IVehicle vehicle){
		Vector3F target = targetProvider.getTargetPos();
		Vector3F desiredVelocity = target.sub(vehicle.getPosition());
		float distance = desiredVelocity.normalize();
		desiredVelocity = desiredVelocity.multiply(vehicle.getMaxVelocity());
		
		
		Vector3F velocity = vehicle.getVelocity();
		
		Vector3F steering = velocity.sub(desiredVelocity);
		steering.normalize();
		
		if (distance != 0) {
			steering = steering.multiply(range * vehicle.getMaxForce() / (distance*distance));
		} 
		
		return steering;
	}
	
}
