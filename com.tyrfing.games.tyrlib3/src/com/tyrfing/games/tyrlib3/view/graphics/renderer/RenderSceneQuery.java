package com.tyrfing.games.tyrlib3.view.graphics.renderer;

import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.model.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.model.math.AABB;
import com.tyrfing.games.tyrlib3.model.math.FrustumG;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.IRenderable;

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
