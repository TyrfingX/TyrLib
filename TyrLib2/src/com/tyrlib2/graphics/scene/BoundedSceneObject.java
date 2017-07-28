package com.tyrlib2.graphics.scene;

import com.tyrlib2.graphics.renderables.BoundingBox;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.math.AABB;

public abstract class BoundedSceneObject extends SceneObject {
	
	private boolean boundingBoxVisible;
	private BoundingBox boundingBox;
	
	public abstract AABB getBoundingBox();
	public void setBoundingBoxVisible(boolean visible) {
		
		if (!boundingBoxVisible && visible) {
			AABB aabb = getBoundingBox();
			boundingBox = new BoundingBox(aabb);
			SceneManager.getInstance().getRootSceneNode().attachSceneObject(boundingBox);
			SceneManager.getInstance().getRenderer().addRenderable(boundingBox, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
		} else if (boundingBoxVisible && !visible) {
			SceneManager.getInstance().destroyRenderable(boundingBox, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
			boundingBox = null;
		}
		
		boundingBoxVisible = visible;
	}
	
	public boolean isBoundingBoxVisible() {
		return boundingBoxVisible;
	}
	
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
		
		if (boundingBoxVisible) {
			updateBoundingBox();
		}
	}
	
	public void updateBoundingBox() {
		boundingBox.setBoundingBox(getBoundingBox());
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
