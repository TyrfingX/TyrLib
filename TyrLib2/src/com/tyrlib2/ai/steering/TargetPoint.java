package com.tyrlib2.ai.steering;

import com.tyrlib2.math.Vector3;

public class TargetPoint implements ITargetProvider {
	
	private Vector3 point;
	
	public TargetPoint(Vector3 point) {
		this.point = point;
	}

	@Override
	public Vector3 getTargetPos() {
		return point;
	}
}
