package com.tyrlib2.movement;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.game.IUpdateable;
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
	
	protected List<IMovementListener> movementListeners = new ArrayList<IMovementListener>();
	
	public Movement() {
		targetProviders = new ArrayList<ITargetProvider>();
	}
	
	public void addTarget(ITargetProvider targetProvider) {
		targetProviders.add(targetProvider);
		
		if (currentTargetProvider == null) {
			nextTargetProvider();
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
			// Actually move and get the remaining time
			time = moveTowardsTarget(time);
			
			if (currentTargetProvider == null) {
				targetReached();
				if (!targetProviders.isEmpty()) {
					nextTargetProvider();
				}
			}
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
		return currentTargetProvider == null;
	}
	
	private void targetReached() {
		for (int i = 0; i < movementListeners.size(); ++i) {
			movementListeners.get(i).onTargetReached();
		}
	}
	
	public void addMovementListener(IMovementListener listener) {
		movementListeners.add(listener);
	}
	
	public void removeMovementListener(IMovementListener listener) {
		movementListeners.remove(listener);
	}
}
