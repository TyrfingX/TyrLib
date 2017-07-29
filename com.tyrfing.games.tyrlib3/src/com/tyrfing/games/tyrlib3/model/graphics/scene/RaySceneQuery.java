package com.tyrfing.games.tyrlib3.model.graphics.scene;

import java.util.List;

import com.tyrfing.games.tyrlib3.model.math.AABB;
import com.tyrfing.games.tyrlib3.model.math.Ray;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;

public class RaySceneQuery implements ISceneQuery {
	private Vector3F intersect;
	private List<RaycastResult> results;
	
	private Ray ray;
	private float maxDist;
	
	public RaySceneQuery(List<RaycastResult> results, Ray ray, float maxDist) {
		this.results = results;
		this.ray = ray;
		this.maxDist = maxDist;
	}
	
	@Override
	public boolean intersects(AABB aabb) {
		if (aabb != null) {
			intersect = aabb.intersectsRay(ray, 0, maxDist);
			return (intersect != null);
		} 
		
		return false;
	}

	@Override
	public void callback(BoundedSceneObject sceneObject) {
		RaycastResult result = new RaycastResult();
		result.sceneObject = sceneObject;
		result.intersection = intersect;
		result.distance = ray.origin.sub(intersect).length();
		results.add(result);
	}
}
