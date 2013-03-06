package com.tyrlib2.graphics.scene;

import com.tyrlib2.math.AABB;

public abstract class BoundedSceneObject extends SceneObject {
	
	public abstract AABB getBoundingBox();
	public abstract void setBoundingBoxVisible(boolean visible);
	protected void calcBoundingBox() {}
	
	protected OctreeNode octreeNode;
	
	@Override
	public void onTransformed() {
		calcBoundingBox();
		if (octreeNode != null) {
			octreeNode.update(this);
		}
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		onTransformed();
	}

	@Override
	public SceneNode detach() {
		onTransformed();
		return super.detach();
	}
}
