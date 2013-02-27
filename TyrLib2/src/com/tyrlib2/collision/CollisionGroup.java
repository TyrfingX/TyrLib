package com.tyrlib2.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class contains a group of objects which can collide with
 * each other and need therefore be checked for collision.
 * @author Sascha
 *
 */

public class CollisionGroup {
	private List<CollisionSphere> collideables;
	
	public CollisionGroup() {
		collideables = new ArrayList<CollisionSphere>();
	}
	
	public void addCollisionSphere(CollisionSphere sphere) {
		collideables.add(sphere);
	}
	
	public void addCollisionSpheres(Collection<CollisionSphere> spheres) {
		collideables.addAll(spheres);
	}
	
	public void updateCollisions() {
		for (int i = 0; i < collideables.size(); ++i) {
			CollisionSphere sphere = collideables.get(i);
			for (int j = i + 1; j < collideables.size(); ++j) {
				CollisionSphere other = collideables.get(j);
				if (sphere.collidesWith(other)) {
					sphere.addCollision(other);
					other.addCollision(sphere);
				}
			}
		}
	}
}
