package com.tyrlib2.game;

import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;

/**
 * Directly moves the object to the target disregarding any
 * constraints such as rotation, inertia, etc
 * @author Sascha
 *
 */

public class DirectMovement extends Movement {

	private SceneNode node;
	private Speed speed;
	
	public DirectMovement(SceneNode node, Speed speed) {
		this.node = node;
		this.speed = speed;
	}
	
	@Override
	protected void newTargetProvider() {
	}

	@Override
	protected float moveTowardsTarget(float time) {
		if (currentTargetProvider != null) {
			Vector3 target = currentTargetProvider.getTargetPos();
			Vector3 pos = node.getRelativePos();
			Vector3 direction = pos.vectorTo(target);
			
			float distance = direction.normalize();
			
			if (distance <= speed.speed * time) {
				time = (speed.speed * time - distance) / speed.speed;
				node.setRelativePos(target);
				currentTargetProvider = null;
			} else {
				node.translate(direction.multiply(speed.speed * time));
				time = 0;
			}
			
		}
		
		return time;
	}

}
