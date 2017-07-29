package com.tyrfing.games.tyrlib3.model.movement;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;

public class TargetPoint implements ITargetProvider {
	
	private Vector3F point;
	
	public TargetPoint(Vector3F point) {
		this.point = point;
	}

	@Override
	public Vector3F getTargetPos() {
		return point;
	}
}
