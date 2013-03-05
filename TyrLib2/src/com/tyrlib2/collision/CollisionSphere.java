package com.tyrlib2.collision;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

/**
 * This class represents a the collision component of an object
 * @author Sascha
 *
 */

public class CollisionSphere extends BoundedSceneObject {
	private List<CollisionSphere> collisions;
	private float radius;
	private int tag;
	protected AABB boundingBox;
	
	private boolean testCollision;

	public CollisionSphere(float radius) {
		collisions = new ArrayList<CollisionSphere>();
		this.radius = radius;
	}
	
	public List<CollisionSphere> getCollisions() {
		return collisions;
	}
	
	public void resetCollisions() {
		collisions.clear();
	}
	
	public void addCollision(CollisionSphere object) {
		collisions.add(object);
	}

	public boolean collidesWith(CollisionSphere object) {
		Vector3 distance = object.getAbsolutePos().sub(this.getAbsolutePos());
		if (distance.squaredLength() <= (radius-object.getRadius()) * (radius-object.getRadius()) ) {
			return true;
		}
		
		return false;
	}
	
	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;

	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	@Override
	public AABB getBoundingBox() {
		if (boundingBox == null) {
			Vector3 pos = parent.getCachedAbsolutePos();
			boundingBox = new AABB(	new Vector3(pos.x + -radius, pos.y + -radius, pos.z + -radius),
									new Vector3(pos.x + radius,  pos.y + radius, pos.z + radius));
		}
		return boundingBox;
	}

	@Override
	public void setBoundingBoxVisible(boolean visible) {
	}
	
	@Override
	public void onTransformed() {
		super.onTransformed();
		Vector3 pos = parent.getCachedAbsolutePos();
		boundingBox = new AABB(	new Vector3(pos.x + -radius, pos.y + -radius, pos.z + -radius),
								new Vector3(pos.x + radius,  pos.y + radius, pos.z + radius));
	}
	
	
	public boolean isTestingCollision() {
		return testCollision;
	}

	public void setTestCollision(boolean testCollision) {
		this.testCollision = testCollision;
	}
	
	
}
