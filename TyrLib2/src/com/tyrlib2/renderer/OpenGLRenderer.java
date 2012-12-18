package com.tyrlib2.renderer;

import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.tyrlib2.lighting.Light;
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
	
	/** View projection matrix of the camera **/
	private float[] vpMatrix = new float[16];
	
	private float timeLastFrame;
	
	private static long BILLION = 1000000000;
	
	protected boolean rendering = false;
	private boolean init = false;
	
	public OpenGLRenderer(Context context) {
		frameListeners = new Vector<IFrameListener>();
		renderables = new Vector<IRenderable>();
		rootSceneNode = new SceneNode();
		this.context = context;
		rendering = false;
	}
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
        
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// Set the blend function
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		
		if (init) {
			ProgramManager.getInstance().recreateAll();
			TextureManager.getInstance().reloadAll(context);
			return;
		}
		
        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(this);
		
		// Create a default program to use
		ProgramManager.getInstance().createProgram(	"BASIC", 
													context, 
													com.tyrlib2.R.raw.basic_color_vs, 
													com.tyrlib2.R.raw.basic_color_fs, 
													new String[]{"a_Position", "a_Color"});
		
		// Create a material for point lights
		ProgramManager.getInstance().createProgram(	"POINT_LIGHT", 
													context, 
													com.tyrlib2.R.raw.point_light_vs, 
													com.tyrlib2.R.raw.point_light_fs, 
													new String[]{"a_Position" });
		
		// Create a program for rendering shadow depth maps
		ProgramManager.getInstance().createProgram(	"SHADOW_DEPTH", 
													context, 
													com.tyrlib2.R.raw.depth_vs, 
													com.tyrlib2.R.raw.depth_fs, 
													new String[]{"aPosition" });
		
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        viewport = new Viewport();
		
        for (int i = 0; i < frameListeners.size(); ++i) {
        	frameListeners.get(i).onSurfaceCreated();
        }
        
        timeLastFrame = 0;
        
        rendering = true;
        init = true;
	}
	
	public void destroy() {
		rendering = false;
		renderables.clear();
	}
	
    public void onDrawFrame(GL10 unused) {
    	
    	if (rendering) {
    	
	        // Redraw background color
	    	GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		    
		    rootSceneNode.update();
		    
		    // Update the view matrix of the camera
		    camera.update();
		    
		    // Update the eye space matrices of all lights
		    SceneManager sceneManager = SceneManager.getInstance();
		    for (int i = 0; i < sceneManager.getLightCount(); ++i) {
		    	Light light = sceneManager.getLight(i);
		    	light.update(camera.viewMatrix);
		    }

		    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		    
			GLES20.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());
		    
		    Matrix.multiplyMM(vpMatrix, 0, viewport.projectionMatrix, 0, camera.viewMatrix, 0);
			
		    drawScene();
		    updateListeners();
		   
    	}
        
    }
    
    private void drawScene() {

    	
	    for (int i = 0; i < renderables.size(); ++i) {
	    	renderables.get(i).render(vpMatrix);
	    }
    }
    
    
    private void updateListeners() {
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
