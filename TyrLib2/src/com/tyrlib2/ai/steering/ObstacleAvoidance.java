package com.tyrlib2.ai.steering;

import com.tyrlib2.collision.CollisionSphere;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.ISceneQuery;
import com.tyrlib2.graphics.scene.Octree;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.movement.TargetSceneObject;

public class ObstacleAvoidance implements IPattern {

	/** We assume that the world will not change drastically within 1 frame
	 *  therefore we will not perform collision queries at each frame
	 *  instead we will perform a query after a certain amount of requests
	 *  and otherwise assume that the same objects in our way
	 */
	public static int QUERY_STEP = 50;
	
	private Vector3 steering = new Vector3();
	
	private int queryRequests;
	
	private class ObstacleQuery implements ISceneQuery {

		private IVehicle vehicle;
		private Vector3 steering;
		
		public ObstacleQuery(IVehicle vehicle, Vector3 steering) {
			this.vehicle = vehicle;
			this.steering = steering;
		}
		
		@Override
		public boolean intersects(AABB aabb) {
			if (aabb != null) {
				return aabb.intersectsAABB(vision.getBoundingBox());
			} else {
				return false;
			}
		}

		@Override
		public void callback(BoundedSceneObject sceneObject) {
			if (sceneObject != null) {
				if (sceneObject != self) {
					AABB aabb = sceneObject.getBoundingBox();
					float radius = aabb.getExtends()/2;
					Vector3 fleeVector = Flee.apply(new TargetSceneObject(sceneObject), radius, vehicle);
					steering.x += fleeVector.x;
					steering.y += fleeVector.y;
					steering.z += fleeVector.z;
				}
			}
		}
		
	}
	
	/** Every obstacle within the vision will be avoided **/
	private CollisionSphere vision;
	
	/** Collision of the vision with the actual object is ignored **/
	private BoundedSceneObject self;
	
	/** The octree where the collision will be tested **/
	private Octree octree;
	
	public ObstacleAvoidance(CollisionSphere vision, BoundedSceneObject self, Octree octree) {
		this.vision = vision;
		this.octree = octree;
		this.self = self;
	}
	
	@Override
	public Vector3 apply(IVehicle vehicle) {
		
		steering.x = 0;
		steering.y = 0;
		steering.z = 0;
		
		if (queryRequests == 0) {
			ObstacleQuery query = new ObstacleQuery(vehicle, steering);
			octree.query(query);
		} 
		
		queryRequests = (queryRequests + 1) % QUERY_STEP;
		
		return steering;
	}

}
