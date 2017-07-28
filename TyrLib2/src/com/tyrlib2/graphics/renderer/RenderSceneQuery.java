package com.tyrlib2.graphics.renderer;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.ISceneQuery;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.FrustumG;

public class RenderSceneQuery implements ISceneQuery {
	
	private FrustumG frustum;
	
	public RenderSceneQuery(FrustumG frustum) {
		this.frustum = frustum;
	}
	
	public RenderSceneQuery() {
		
	}
	
	public void init(FrustumG frustum) {
		this.frustum = frustum;
	}
	
	@Override
	public boolean intersects(AABB aabb) {
		if (aabb == null) return true;
		//if (!frustum.getAABB().intersectsAABB(aabb)) return false;
		return frustum.aabbInFrustum(aabb);
	}

	@Override
	public void callback(BoundedSceneObject sceneObject) {
		SceneManager.getInstance().getRenderer().toRender.add((IRenderable)sceneObject);
	}
}
