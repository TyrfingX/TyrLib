package com.tyrlib2.collision;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.Vector3;

/**
 * This class represents a the collision component of an object
 * @author Sascha
 *
 */

public class CollisionSphere extends SceneObject {
	private List<CollisionSphere> collisions;
	private float radius;
	private int tag;
	
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
	
	
	
	
}
