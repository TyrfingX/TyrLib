package com.tyrfing.games.tyrlib3.graphics.renderer;

import com.tyrfing.games.tyrlib3.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.math.AABB;
import com.tyrfing.games.tyrlib3.math.FrustumG;

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
