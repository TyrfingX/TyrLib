package com.tyrfing.games.tyrlib3.collision;

import com.tyrfing.games.tyrlib3.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.math.AABB;

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
