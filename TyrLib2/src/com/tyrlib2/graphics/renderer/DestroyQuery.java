package com.tyrlib2.graphics.renderer;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.ISceneQuery;
import com.tyrlib2.math.AABB;

public class DestroyQuery implements ISceneQuery {

	@Override
	public boolean intersects(AABB aabb) {
		return true;
	}

	@Override
	public void callback(BoundedSceneObject sceneObject) {
		IRenderable r = (IRenderable) sceneObject;
		r.destroy();
	}

}
