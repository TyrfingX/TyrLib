package com.tyrfing.games.tyrlib3.model.graphics.scene;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.model.math.Ray;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.Camera;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.Viewport;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;

/**
 * Gets the objects intersecting this raycast
 * @author Sascha
 *
 */

public class Raycast {
	
	private Ray ray;
	public float maxDist;
	
	public Raycast(Vector3F startPoint, Vector3F direction, float maxDist) {
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
	
	public static Raycast fromScreen(Vector2F point, Vector3F origin) {
		Camera cam = SceneManager.getInstance().getActiveCamera();
		Viewport viewport = SceneManager.getInstance().getViewport();
		
		Vector3F camLookDirection = cam.getWorldLookDirection();
		camLookDirection.normalize();
		
		Vector3F camUpDirection = cam.getWorldUpVector();
		camUpDirection.normalize();
		camUpDirection = camUpDirection.multiply(viewport.getNearClipHeight());
		
		Vector3F camRightDirection = camLookDirection.cross(camUpDirection);
		camRightDirection.normalize();
		camRightDirection = camRightDirection.multiply(viewport.getNearClipWidth());
		
		camLookDirection.normalize();
		
		Vector3F nearClipPoint = origin.add(camLookDirection.multiply(viewport.getNearClip()));
		
		Vector3F raycastPoint = nearClipPoint.add(camUpDirection.multiply(-1*(point.y-0.5f)))
										    .add(camRightDirection.multiply(point.x-0.5f));
		
		Vector3F raycastDirection = raycastPoint.sub(origin);
		raycastDirection.normalize();
		
		Raycast raycast = new Raycast(origin, raycastDirection, viewport.getFarClip());
		return raycast;
	}
	
	public static Raycast fromScreen(Vector2F point) {
		Camera cam = SceneManager.getInstance().getActiveCamera();
		return fromScreen(point, cam.getAbsolutePos());
	}
}
