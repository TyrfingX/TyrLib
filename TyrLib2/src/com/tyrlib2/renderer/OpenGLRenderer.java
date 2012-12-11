package com.tyrlib2.renderer;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.tyrlib2.files.FileReader;
import com.tyrlib2.lighting.Light;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.scene.SceneManager;
import com.tyrlib2.scene.SceneNode;

/**
 * This class initiates the actual rendering.
 * @author Sascha
 *
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    
	public static final int BYTES_PER_FLOAT = 4;
	
	private List<IFrameListener> frameListeners;
	private List<IRenderable> renderables;
	private SceneNode rootSceneNode;
	private Viewport viewport;
	private Camera camera;
	private Context context;
	
	private float[] vpMatrix = new float[16];
	
	private float[] identityMatrix = new float[16];
	private Vector3 origin = new Vector3();
	private Quaternion rotFree = new Quaternion();
	
	private float timeLastFrame;
	
	private static long BILLION = 1000000000;
	
	public OpenGLRenderer(Context context) {
		frameListeners = new ArrayList<IFrameListener>();
		renderables = new ArrayList<IRenderable>();
		rootSceneNode = new SceneNode();
		this.context = context;
	}
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(this);
		
		Matrix.setIdentityM(identityMatrix, 0);
		
		// Create a default program to use
		String basicVertexShader = FileReader.readRawFile(context, com.tyrlib2.R.raw.basic_color_vs);
		String basicFragmentShader = FileReader.readRawFile(context, com.tyrlib2.R.raw.basic_color_fs);
		ShaderManager.getInstance().loadShader("BASIC_VS", GLES20.GL_VERTEX_SHADER, basicVertexShader);
		ShaderManager.getInstance().loadShader("BASIC_FS", GLES20.GL_FRAGMENT_SHADER, basicFragmentShader);
		ProgramManager.getInstance().createProgram("BASIC", "BASIC_VS", "BASIC_FS", new String[]{"a_Position", "a_Color"});
		
		// Create a material for point lights
		String lightVertexShader = FileReader.readRawFile(context, com.tyrlib2.R.raw.point_light_vs);
		String lightFragmentShader = FileReader.readRawFile(context, com.tyrlib2.R.raw.point_light_fs);
		ShaderManager.getInstance().loadShader("POINT_LIGHT_VS", GLES20.GL_VERTEX_SHADER, lightVertexShader);
		ShaderManager.getInstance().loadShader("POINT_LIGHT_FS", GLES20.GL_FRAGMENT_SHADER, lightFragmentShader);
		ProgramManager.getInstance().createProgram("POINT_LIGHT", "POINT_LIGHT_VS", "POINT_LIGHT_FS", new String[]{"a_Position" });
		
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        viewport = new Viewport();
        
     // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
        
        for (IFrameListener listener : frameListeners) {
        	listener.onSurfaceCreated();
        }
        
        timeLastFrame = 0;

	}
	
    public void onDrawFrame(GL10 unused) {
        // Redraw background color
    	GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
	    
	    rootSceneNode.update(origin, rotFree, identityMatrix);
	    
	    // Update the view matrix of the camera
	    camera.update();
	    
	    // Update the eye space matrices of all lights
	    SceneManager sceneManager = SceneManager.getInstance();
	    for (int i = 0; i < sceneManager.getLightCount(); ++i) {
	    	Light light = sceneManager.getLight(i);
	    	light.updateEyeSpaceVector(camera.viewMatrix);
	    }
	    
	    Matrix.multiplyMM(vpMatrix, 0, viewport.projectionMatrix, 0, camera.viewMatrix, 0);
	    
	    for (int i = 0; i < renderables.size(); ++i) {
	    	renderables.get(i).render(vpMatrix);
	    }
	    
	    float time = (float) System.nanoTime() / BILLION - timeLastFrame;
	    
	    if (timeLastFrame != 0) {
	    
	        for (IFrameListener listener : frameListeners) {
	        	listener.onFrameRendered(time);
	        }
        
	    }
        
        timeLastFrame = (float) System.nanoTime() / BILLION;

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
       viewport.setFullscreen(width, height);
    }
    
    /**
     * Add a new frame listener
     * @param listener	The IFrameListener to be added
     */
    
    public void addFrameListener(IFrameListener listener) {
    	frameListeners.add(listener);
    }
    
    /**
     * Remove a frame listener
     * @param listener	The IFrameListener to be removed
     */
    
    public void removeFrameListener(IFrameListener listener) {
    	frameListeners.remove(listener);
    }
    
    public Viewport getViewport() {
    	return viewport;
    }
    
    public void setCamera(Camera camera) {
    	this.camera = camera;
    }
    
    public Camera getCamera() {
    	return camera;
    }
    
    public SceneNode getRootSceneNode() {
    	return rootSceneNode;
    }

    public void addRenderable(IRenderable renderable) {
    	renderables.add(renderable);
    }
    
    public void removeRenderable(IRenderable renderable) {
    	renderables.remove(renderable);
    }
    
}
