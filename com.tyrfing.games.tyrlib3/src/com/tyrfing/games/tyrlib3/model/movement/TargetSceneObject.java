package com.tyrfing.games.tyrlib3.model.movement;

import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneObject;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;

public class TargetSceneObject implements ITargetProvider {

	private SceneObject target;
	
	public TargetSceneObject(SceneObject target) {
		this.target = target;
	}
	
	@Override
	public Vector3F getTargetPos() {
		return target.getAbsolutePos();
	}

}
