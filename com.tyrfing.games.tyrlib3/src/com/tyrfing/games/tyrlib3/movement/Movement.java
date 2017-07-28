package com.tyrfing.games.tyrlib3.movement;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.game.IUpdateable;
import com.tyrfing.games.tyrlib3.math.Vector3F;

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
	
	public Vector3F getCurrentTarget() {
		return currentTargetProvider.getTargetPos();
	}
	
	public ITargetProvider getCurrentTargetProvider() {
		return currentTargetProvider;
	}
	
	public void clear() {
		targetProviders.clear();
		currentTargetProvider = null;
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
	
	public ITargetProvider peekNextTargetProvider() {
		return targetProviders.get(0);
	}
	
	protected abstract void newTargetProvider();
	protected abstract float moveTowardsTarget(float time);
	
	@Override
	public boolean isFinished() {
		return currentTargetProvider == null && targetProviders.size() == 0;
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
