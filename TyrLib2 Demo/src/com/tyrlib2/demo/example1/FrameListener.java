package com.tyrlib2.demo.example1;


import com.tyrlib2.graphics.lighting.DirectionalLight;
import com.tyrlib2.graphics.lighting.Light.Type;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderables.Box;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

/**
 * A basic frame listener.
 * FrameListener objects react to changes to a OpenGL surface (creation, change, after a frame has been
 * rendered on it).
 * @author Sascha
 *
 */

public class FrameListener implements IFrameListener {

	private SceneNode boxNode;
	
	@Override
	public void onSurfaceCreated() {
		/* This is where you set stuff up **/
		
		/* First lets load a texture **/
		TextureManager.getInstance().createTexture("SOIL", Media.CONTEXT.getResourceID("soil", "drawable"));
		
		/* Textures alone are not enough to draw something. The library does not employ a fixed
		 * pipeline and therefore needs a shader telling OpenGL how exactly to draw your stuff.
		 * 	
		 * This is encapsulated within Materials. Simply choose the material you want to use in order
		 * to draw your stuff or roll your own. Basic effects such as lighting & texturing can be achieved
		 * by using TexturedMaterial
		 */
		
		DefaultMaterial3 mat = new DefaultMaterial3("SOIL", 1, 1, null);
		
		/* Now we need to create the actual object we want to render
		 * In this example this will be simple, unrotated cube/box.
		 */
		
		Box box = SceneManager.getInstance()
							  .createBox(	mat, 
											new Vector3(-0.3f, -0.3f, -0.3f), 
											new Vector3(0.3f, 0.3f, 0.3f));
		
		/* Positioning an object in world space is done by attaching them to SceneNode objects.
		 * If an object is not attached to a SceneNode, it will not be rendered.
		 */
		
		boxNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,0,0)); 
		boxNode.attachSceneObject(box);
		
		//box.setBoundingBoxVisible(true);
		
		/* Next we setup some basic ambient lighting (global illumination) so that we can
		 * actually see our box.
		 */
		
		SceneManager.getInstance().setAmbientLight(new Color(0.5f,0.5f,0.5f,1));
		
		/* Next we setup some directional lighting to make the scene a bit better looking*/
		DirectionalLight light = (DirectionalLight) SceneManager.getInstance().createLight(Type.DIRECTIONAL_LIGHT);
		light.setLightDirection(new Vector3(0,-1,-1));
		SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,0,0)).attachSceneObject(light);
		
		/* Finally we need to create a camera capturing the scene */
		SceneNode camNode = SceneManager.getInstance().getRootSceneNode().createChild(new Vector3(0,5,5));
		Camera camera = SceneManager.getInstance().createCamera(new Vector3(0,-1,-1), new Vector3(0,1,0), camNode);
		camera.use();
		
		

	}

	@Override
	public void onSurfaceChanged() {
		/* This is where you may react if the screen orientation changes */
	}

	@Override
	public void onFrameRendered(float time) {
		/* Lets rotate our box a bit */
		boxNode.rotate(Quaternion.fromAxisAngle(new Vector3(1,1,1), 100*time));
	}

}
