package com.tyrlib2.scene;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.lighting.Light;
import com.tyrlib2.lighting.PointLight;
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
	
	private List<Light> lights;
	
	public SceneManager() {
		lights = new ArrayList<Light>();
	}
	
	public static SceneManager getInstance() {
		if (instance == null) {
			instance = new SceneManager();
		}
		
		return instance;
	}
	
	public void destroy() {
		instance = null;
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
	
	public Camera createCamera(Vector3 lookDirection, Vector3 up, SceneNode node) {
		Camera camera = new Camera(up);
		node.attachSceneObject(camera);
		camera.setLookDirection(lookDirection);
		return camera;
	}
	
	public Light createLight(Light.Type type) {
		
		Light light = null;
		
		switch(type) {
		case POINT_LIGHT:
			light = new PointLight();
			break;
		default:
			break;
		}
		
		if (light != null) {
			lights.add(light);
		}
		
		return light;
	}
	
	/**
	 * Get the number of created lights
	 * @return	The number of created lights
	 */
	
	public int getLightCount() {
		return lights.size();
	}
	
	/**
	 * Get a light object
	 * @param index	Index of the light
	 * @return	The light object
	 */
	
	public Light getLight(int index) {
		return lights.get(index);
	}
	
	public SceneNode getRootSceneNode() {
		return renderer.getRootSceneNode();
	}
	
	
}
