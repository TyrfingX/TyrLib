package com.tyrfing.games.tyrlib3.graphics.renderer;

import com.tyrfing.games.tyrlib3.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.math.AABB;

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
