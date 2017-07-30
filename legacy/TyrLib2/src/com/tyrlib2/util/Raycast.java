package com.tyrlib2.util;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.Octree;
import com.tyrlib2.graphics.scene.SceneManager;
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
	public float maxDist;
	
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
		octree.query(new RaySceneQuery(results, ray, maxDist));
		return results;
	}
	
	public List<RaycastResult> performRaycast(Octree octree, RaySceneQuery query) {
		List<RaycastResult> results = new ArrayList<RaycastResult>();
		octree.query(query);
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
		
		Vector3 nearClipPoint = origin.add(camLookDirection.multiply(viewport.getNearClip()));
		
		Vector3 raycastPoint = nearClipPoint.add(camUpDirection.multiply(-1*(point.y-0.5f)))
										    .add(camRightDirection.multiply(point.x-0.5f));
		
		Vector3 raycastDirection = raycastPoint.sub(origin);
		raycastDirection.normalize();
		
		Raycast raycast = new Raycast(origin, raycastDirection, viewport.getFarClip());
		return raycast;
	}
	
	public static Raycast fromScreen(Vector2 point) {
		Camera cam = SceneManager.getInstance().getActiveCamera();
		return fromScreen(point, cam.getAbsolutePos());
	}
}
