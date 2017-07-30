package com.tyrlib2.collision;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.ISceneQuery;
import com.tyrlib2.math.AABB;

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
