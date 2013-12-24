package com.tyrlib2.graphics.renderer;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.ISceneQuery;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.FrustumG;

public class RenderSceneQuery implements ISceneQuery {
	
	private FrustumG frustum;
	private float[] transformMatrix;
	
	public RenderSceneQuery(FrustumG frustum, float[] transformMatrix) {
		this.frustum = frustum;
		this.transformMatrix = transformMatrix;
	}
	
	public RenderSceneQuery() {
		
	}
	
	public void init(FrustumG frustum, float[] transformMatrix) {
		this.frustum = frustum;
		this.transformMatrix = transformMatrix;
	}
	
	@Override
	public boolean intersects(AABB aabb) {
		if (aabb == null) return true;
		//if (!frustum.getAABB().intersectsAABB(aabb)) return false;
		return frustum.aabbInFrustum(aabb);
	}

	@Override
	public void callback(BoundedSceneObject sceneObject) {
		IRenderable renderable = (IRenderable) sceneObject;
		renderable.render(transformMatrix);
	}
}
