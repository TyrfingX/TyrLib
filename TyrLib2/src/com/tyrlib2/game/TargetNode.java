package com.tyrlib2.game;

import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;

public class TargetNode implements ITargetProvider {
	private SceneNode node;
	
	public TargetNode(SceneNode node) {
		this.node = node;
	}

	@Override
	public Vector3 getTargetPos() {
		return node.getAbsolutePos();
	}
	
	
	
}
