package com.tyrfing.games.tyrlib3.view.graphics.renderer;

import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.model.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.model.math.AABB;
import com.tyrfing.games.tyrlib3.model.math.FrustumG;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.IRenderable;

public class RenderShadowSceneQuery implements ISceneQuery {
	
	private FrustumG frustum;
	private float[] transformMatrix;
	
	public RenderShadowSceneQuery(FrustumG frustum, float[] transformMatrix) {
		this.frustum = frustum;
		this.transformMatrix = transformMatrix;
	}
	
	public RenderShadowSceneQuery() {
		
	}
	
	public void init(FrustumG frustum, float[] transformMatrix) {
		this.frustum = frustum;
		this.transformMatrix = transformMatrix;
	}
	
	@Override
	public boolean intersects(AABB aabb) {
		if (aabb == null) return true;
		if (!frustum.getAABB().intersectsAABB(aabb)) return false;
		return true;
	}

	@Override
	public void callback(BoundedSceneObject sceneObject) {
		IRenderable renderable = (IRenderable) sceneObject;
		renderable.renderShadow(transformMatrix);
	}
}
