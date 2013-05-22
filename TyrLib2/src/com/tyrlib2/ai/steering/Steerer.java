package com.tyrlib2.ai.steering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<IPattern, Float> priorities;
	private IVehicle vehicle;
	
	public Steerer(IVehicle vehicle) {
		patterns = new ArrayList<IPattern>();
		priorities = new HashMap<IPattern, Float>();
		this.vehicle = vehicle;
	}
	
	public void addPattern(IPattern pattern, float priority) {
		patterns.add(pattern);
		priorities.put(pattern, priority);
	}
	
	public void removePattern(IPattern pattern) {
		patterns.remove(pattern);
		priorities.remove(pattern);
	}

	public boolean hasPattern(IPattern pattern) {
		return patterns.contains(pattern);
	}
	
	@Override
	public void onUpdate(float time) {
		vehicle.resetSteeringForces();
		
		for (int i = 0; i < patterns.size(); ++i) {
			Vector3 steeringForce = patterns.get(i).apply(vehicle);
			float priority = priorities.get(patterns.get(i));
			steeringForce.x *= priority;
			steeringForce.y *= priority;
			steeringForce.z *= priority;
			vehicle.addSteeringForce(steeringForce);
		}
		
		vehicle.onUpdate(time);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public IVehicle getVehicle() {
		return vehicle;
	}
	
	public void clearPatterns() {
		patterns.clear();
	}
}
