package com.tyrlib2.util;

import java.util.List;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.ISceneQuery;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Ray;
import com.tyrlib2.math.Vector3;

public class RaySceneQuery implements ISceneQuery {
	private Vector3 intersect;
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
