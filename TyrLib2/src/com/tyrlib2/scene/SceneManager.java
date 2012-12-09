package com.tyrlib2.scene;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Camera;
import com.tyrlib2.renderer.OpenGLRenderer;

/**
 * This singleton class manages the creation and destruction of Scene objects.
 * These objects should always be created via the SceneManager.
 * @author Sascha
 *
 */

public class SceneManager {
	
	private static SceneManager instance;
	private OpenGLRenderer renderer;
	
	public SceneManager() {
		
	}
	
	public static SceneManager getInstance() {
		if (instance == null) {
			instance = new SceneManager();
		}
		
		return instance;
	}
	
	public void setRenderer(OpenGLRenderer renderer) {
		this.renderer = renderer;
	}
	
	/**
	 * Get the renderer
	 * @return	The renderer
	 */
	
	public OpenGLRenderer getRenderer() {
		return renderer;
	}
	
	/**
	 * Creates a new camera object
	 * @param lookAt	The look direction of the camera
	 * @param up		???
	 * @param node		The parent node
	 * @return			A new camera
	 */
	
	public Camera createCamera(Vector3 lookAt, Vector3 up, SceneNode node) {
		Camera camera = new Camera(up);
		node.attachSceneObject(camera);
		camera.lookAt(lookAt);
		return camera;
	}
	
	public SceneNode getRootSceneNode() {
		return renderer.getRootSceneNode();
	}
	
	
}
