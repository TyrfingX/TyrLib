package com.tyrlib2.ai.steering;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.math.Vector3;

/**
 * Main class for steering. This class takes the patterns which compose the 
 * steering behaviour and inputs the resulting action into the vehicle.
 * @author Sascha
 *
 */

public class Steerer implements IUpdateable {
	private List<IPattern> patterns;
	private IVehicle vehicle;
	
	public Steerer(IVehicle vehicle) {
		patterns = new ArrayList<IPattern>();
		this.vehicle = vehicle;
	}
	
	public void addPattern(IPattern pattern) {
		patterns.add(pattern);
	}

	@Override
	public void onUpdate(float time) {
		vehicle.resetSteeringForces();
		
		for (int i = 0; i < patterns.size(); ++i) {
			Vector3 steeringForce = patterns.get(i).apply(vehicle);
			vehicle.addSteeringForce(steeringForce);
		}
		
		vehicle.onUpdate(time);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
