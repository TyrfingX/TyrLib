package com.tyrfing.games.tyrlib3.view.graphics.renderer;

import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.model.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.model.math.AABB;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.IRenderable;

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
