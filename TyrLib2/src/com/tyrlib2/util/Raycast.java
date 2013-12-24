package com.tyrlib2.util;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.ISceneQuery;
import com.tyrlib2.graphics.scene.Octree;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Ray;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;

/**
 * Gets the objects intersecting this raycast
 * @author Sascha
 *
 */

public class Raycast {
	
	private Ray ray;
	private float maxDist;
	
	private class RaySceneQuery implements ISceneQuery {
		
		private Vector3 intersect;
		private List<RaycastResult> results;
		
		public RaySceneQuery(List<RaycastResult> results) {
			this.results = results;
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
	
	public class RaycastResult implements Comparable<RaycastResult> {
		public SceneObject sceneObject;
		public Vector3 intersection;
		public float distance;

		@Override
		public int compareTo(RaycastResult another) {
			if (another.distance < distance) {
				return 1;
			} else if (another.distance > distance) {
				return -1;
			}
			
			return 0;
		}
	}
	
	public Raycast(Vector3 startPoint, Vector3 direction, float maxDist) {
		direction.normalize();
		this.ray = new Ray(direction, startPoint);	
		this.maxDist = maxDist;
	}
	
	public Ray getRay() {
		return ray;
	}
	
	public float maxDist() {
		return maxDist;
	}
	
	public List<RaycastResult> performRaycast() {
		return performRaycast(SceneManager.getInstance().getRenderer().getOctree(OpenGLRenderer.DEFAULT_CHANNEL));
	}
	
	public List<RaycastResult> performRaycast(Octree octree) {
		List<RaycastResult> results = new ArrayList<RaycastResult>();
		octree.query(new RaySceneQuery(results));
		return results;
	}
	
	public static Raycast fromScreen(Vector2 point, Vector3 origin) {
		Camera cam = SceneManager.getInstance().getActiveCamera();
		Viewport viewport = SceneManager.getInstance().getViewport();
		
		Vector3 camLookDirection = cam.getWorldLookDirection();
		camLookDirection.normalize();
		
		Vector3 camUpDirection = cam.getWorldUpVector();
		camUpDirection.normalize();
		camUpDirection = camUpDirection.multiply(viewport.getNearClipHeight());
		
		Vector3 camRightDirection = camLookDirection.cross(camUpDirection);
		camRightDirection.normalize();
		camRightDirection = camRightDirection.multiply(viewport.getNearClipWidth());
		
		camLookDirection.normalize();
		
		float nearClipDistance = viewport.getNearClip();
		
		Vector3 nearClipPoint = origin.add(camLookDirection.multiply(nearClipDistance));
		
		Vector3 raycastPoint = nearClipPoint.add(camUpDirection.multiply(-1*(point.y-0.5f)))
											.add(camRightDirection.multiply(point.x-0.5f));
		
		Vector3 raycastDirection = raycastPoint.sub(origin);
		raycastDirection.normalize();
		
		Raycast raycast = new Raycast(raycastPoint, raycastDirection, viewport.getFarClip());
		return raycast;
	}
	
	public static Raycast fromScreen(Vector2 point) {
		Camera cam = SceneManager.getInstance().getActiveCamera();
		return fromScreen(point, cam.getAbsolutePos());
	}
}
