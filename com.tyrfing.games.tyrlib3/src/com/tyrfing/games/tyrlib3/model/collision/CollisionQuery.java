package com.tyrfing.games.tyrlib3.model.collision;

import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.model.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.model.math.AABB;

public class CollisionQuery implements ISceneQuery {

	private CollisionSphere sphere;
	
	public CollisionQuery(CollisionSphere sphere) {
		this.sphere = sphere;
	}
	
	@Override
	public boolean intersects(AABB aabb) {
		return aabb.intersectsAABB(sphere.boundingBox);
	}

	@Override
	public void callback(BoundedSceneObject sceneObject) {
		CollisionSphere other = (CollisionSphere) sceneObject;
		if (sphere != other && sphere.collidesWith(other)) {
			sphere.addCollision(other);
		}
	}

}
