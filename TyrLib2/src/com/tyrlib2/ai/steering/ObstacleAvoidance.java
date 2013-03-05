package com.tyrlib2.ai.steering;

import java.util.List;

import com.tyrlib2.collision.CollisionSphere;
import com.tyrlib2.game.TargetSceneObject;
import com.tyrlib2.math.Vector3;

public class ObstacleAvoidance implements IPattern {

	/** Every obstacle within the vision will be avoided **/
	private CollisionSphere vision;
	
	public ObstacleAvoidance(CollisionSphere vision) {
		this.vision = vision;
	}
	
	@Override
	public Vector3 apply(IVehicle vehicle) {
		
		Vector3 steering = new Vector3();
		
		List<CollisionSphere> collisions = vision.getCollisions();
		for (int i = 0; i < collisions.size(); ++i) {
			CollisionSphere sphere = collisions.get(i);
			Flee flee = new Flee(new TargetSceneObject(sphere), sphere.getRadius());
			steering = steering.add(flee.apply(vehicle));
		}
		
		vision.resetCollisions();
		
		return steering;
	}

}
