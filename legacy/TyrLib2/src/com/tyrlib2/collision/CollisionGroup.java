package com.tyrlib2.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tyrlib2.graphics.scene.Octree;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Vector3;

/**
 * This class contains a group of objects which can collide with
 * each other and need therefore be checked for collision.
 * @author Sascha
 *
 */

public class CollisionGroup {
	private Octree octree;
	private List<CollisionSphere> collideables;
	
	public CollisionGroup() {
		octree = new Octree(1, 20, new Vector3(), 10);
		collideables = new ArrayList<CollisionSphere>();
		SceneManager.getInstance().getRootSceneNode().attachSceneObject(octree);
	}
	
	public void addCollisionSphere(CollisionSphere sphere) {
		octree.addObject(sphere);
		collideables.add(sphere);
	}
	
	public void addCollisionSpheres(Collection<CollisionSphere> spheres) {
		for (CollisionSphere sphere : spheres) {
			octree.addObject(sphere);
		}
		
		collideables.addAll(spheres);
	}
	
	public void updateCollisions() {
		octree.update();
		//octree.setBoundingBoxVisible(true);
		for (int i = 0; i < collideables.size(); ++i) {
			if (collideables.get(i).isTestingCollision()) {
				CollisionQuery query = new CollisionQuery(collideables.get(i));
				octree.query(query);
			}
		}
	}
}
