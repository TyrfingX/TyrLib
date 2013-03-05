package com.tyrlib2.graphics.scene;

import com.tyrlib2.math.AABB;

public abstract class BoundedSceneObject extends SceneObject {
	
	private boolean dirty;
	
	public abstract AABB getBoundingBox();
	public abstract void setBoundingBoxVisible(boolean visible);
	protected void calcBoundingBox() {}
	
	@Override
	public void onTransformed() {
		calcBoundingBox();
		dirty = true;
	}
	
	public void setClean() {
		dirty = false;
	}
	
	public boolean isDity() {
		return dirty;
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		dirty = true;
		super.attachTo(node);
	}

	@Override
	public SceneNode detach() {
		dirty = true;
		return super.detach();
	}
}
