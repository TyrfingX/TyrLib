package com.tyrlib2.scene;

import com.tyrlib2.math.Vector3;
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
	
	public Camera createCamera(Vector3 pos, Vector3 lookAt, Vector3 up) {
		Camera camera = new Camera(pos, lookAt, up);
		return camera;
	}
	
	
}
