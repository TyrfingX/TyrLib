package com.tyrlib2.game;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.math.Vector3;

/**
 * This class takes care of guiding an object to a series
 * of targets
 * @author Sascha
 *
 */

public abstract class Movement implements IUpdateable {
	
	protected List<ITargetProvider> targetProviders;
	protected ITargetProvider currentTargetProvider;
	
	public Movement() {
		targetProviders = new ArrayList<ITargetProvider>();
	}
	
	public void addTarget(ITargetProvider targetProvider) {
		if (currentTargetProvider != null) {
			targetProviders.add(targetProvider);
		} else {
			currentTargetProvider = targetProvider;
		}
	}
	
	public Vector3 getCurrentTarget() {
		return currentTargetProvider.getTargetPos();
	}
	
	public ITargetProvider getCurrentTargetProvider() {
		return currentTargetProvider;
	}
	
	public void clear() {
		targetProviders.clear();
	}

	@Override
	public void onUpdate(float time) {
		
		while (currentTargetProvider != null && time > 0) {
			if (!targetProviders.isEmpty()) {
				nextTargetProvider();
			}
			
			// Actually move and get the remaining time
			time = moveTowardsTarget(time);
		}
	}
	
	public void nextTargetProvider() {
		currentTargetProvider = targetProviders.get(0);
		targetProviders.remove(0);
		newTargetProvider();
	}
	
	protected abstract void newTargetProvider();
	protected abstract float moveTowardsTarget(float time);
	
	@Override
	public boolean isFinished() {
		return targetProviders.isEmpty();
	}
}
