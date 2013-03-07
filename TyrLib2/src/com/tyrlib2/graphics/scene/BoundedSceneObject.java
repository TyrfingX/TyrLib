package com.tyrlib2.graphics.scene;

import com.tyrlib2.math.AABB;

public abstract class BoundedSceneObject extends SceneObject {
	
	public abstract AABB getBoundingBox();
	public abstract void setBoundingBoxVisible(boolean visible);
	protected void calcBoundingBox() {}

	private boolean dirty;
	protected OctreeNode octree;
	
	@Override
	public void onTransformed() {
		calcBoundingBox();
		dirty = true;
		if (octree != null) {
			octree.setDirty();
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
	
	public void setClean() {
		dirty = false;
	}
	
	public boolean isDity() {
		return dirty;
	}
}
