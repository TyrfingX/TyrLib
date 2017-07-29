package com.tyrfing.games.tyrlib3.model.movement;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.model.steering.IVehicle;
import com.tyrfing.games.tyrlib3.model.steering.Seek;
import com.tyrfing.games.tyrlib3.model.steering.Steerer;

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
			Vector3F velocity = vehicle.getVelocity();
			
			Vector3F pos = vehicle.getPosition();
			Vector3F target = currentTargetProvider.getTargetPos();
			
			float distance = pos.vectorTo(target).length();
			
			if (distance <= velocity.length()) {
				currentTargetProvider = null;
			} 
			
			time = 0;
		}
		
		return time;
	}
	
	@Override
	public void clear() {
		super.clear();
		if (seek != null) {
			steerer.removePattern(seek);
		}
	}

}
