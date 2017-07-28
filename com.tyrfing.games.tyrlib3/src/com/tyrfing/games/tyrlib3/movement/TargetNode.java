package com.tyrfing.games.tyrlib3.movement;

import com.tyrfing.games.tyrlib3.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.math.Vector3F;

public class TargetNode implements ITargetProvider {
	private SceneNode node;
	
	public TargetNode(SceneNode node) {
		this.node = node;
	}

	@Override
	public Vector3F getTargetPos() {
		return node.getAbsolutePos();
	}
	
	
	
}
