package com.tyrlib2.movement;

import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.Vector3;

public class TargetSceneObject implements ITargetProvider {

	private SceneObject target;
	
	public TargetSceneObject(SceneObject target) {
		this.target = target;
	}
	
	@Override
	public Vector3 getTargetPos() {
		return target.getAbsolutePos();
	}

}
