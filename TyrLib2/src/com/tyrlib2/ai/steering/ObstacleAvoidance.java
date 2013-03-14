package com.tyrlib2.ai.steering;

import com.tyrlib2.collision.CollisionSphere;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.ISceneQuery;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.movement.TargetSceneObject;

public class ObstacleAvoidance implements IPattern {

	private Vector3 steering = new Vector3();
	
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
					Flee flee = new Flee(new TargetSceneObject(sceneObject), radius);
					Vector3 fleeVector = flee.apply(vehicle);
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
	
	public ObstacleAvoidance(CollisionSphere vision, BoundedSceneObject self) {
		this.vision = vision;
	}
	
	@Override
	public Vector3 apply(IVehicle vehicle) {
		
		steering.x = 0;
		steering.y = 0;
		steering.z = 0;
		
		ObstacleQuery query = new ObstacleQuery(vehicle, steering);
		SceneManager.getInstance().performSceneQuery(query);
		
		return steering;
	}

}
