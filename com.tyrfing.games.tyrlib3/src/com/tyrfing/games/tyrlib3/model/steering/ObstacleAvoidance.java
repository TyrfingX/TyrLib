package com.tyrfing.games.tyrlib3.model.steering;

import com.tyrfing.games.tyrlib3.model.collision.CollisionSphere;
import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.model.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.model.graphics.scene.Octree;
import com.tyrfing.games.tyrlib3.model.math.AABB;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.model.movement.TargetSceneObject;

public class ObstacleAvoidance implements IPattern {

	/** We assume that the world will not change drastically within 1 frame
	 *  therefore we will not perform collision queries at each frame
	 *  instead we will perform a query after a certain amount of requests
	 *  and otherwise assume that the same objects in our way
	 */
	public static int QUERY_STEP = 50;
	
	private Vector3F steering = new Vector3F();
	
	private int queryRequests;
	
	private class ObstacleQuery implements ISceneQuery {

		private IVehicle vehicle;
		private Vector3F steering;
		
		public ObstacleQuery(IVehicle vehicle, Vector3F steering) {
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
					Vector3F fleeVector = Flee.apply(new TargetSceneObject(sceneObject), radius, vehicle);
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
	public Vector3F apply(IVehicle vehicle) {
		
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
