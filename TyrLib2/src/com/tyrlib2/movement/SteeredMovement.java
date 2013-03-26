package com.tyrlib2.movement;

import com.tyrlib2.ai.steering.IVehicle;
import com.tyrlib2.ai.steering.Seek;
import com.tyrlib2.ai.steering.Steerer;
import com.tyrlib2.math.Vector3;

/**
 * This class uses a steerer to move an object to the desired
 * locations.
 * @author Sascha
 *
 */

public class SteeredMovement extends Movement {

	private Steerer steerer;
	private Seek seek;
	
	public SteeredMovement(Steerer steerer) {
		this.steerer = steerer;
	}
	
	@Override
	protected void newTargetProvider() {
		if (seek != null) {
			steerer.removePattern(seek);
		}
		
		seek = new Seek(currentTargetProvider);
		steerer.addPattern(seek, 1);
	}

	@Override
	protected float moveTowardsTarget(float time) {
		if (currentTargetProvider != null) {
			IVehicle vehicle = steerer.getVehicle();
			Vector3 velocity = vehicle.getVelocity();
			
			Vector3 pos = vehicle.getPosition();
			Vector3 target = currentTargetProvider.getTargetPos();
			
			float distance = pos.vectorTo(target).length();
			
			if (distance <= velocity.length() * time) {
				currentTargetProvider = null;
			} 
			
			steerer.onUpdate(time);
			
			time = 0;
		}
		
		return time;
	}

}
