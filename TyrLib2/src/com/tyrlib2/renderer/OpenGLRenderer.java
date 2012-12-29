package com.tyrlib2.renderer;

import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.SparseArray;

import com.tyrlib2.lighting.Light;
import com.tyrlib2.materials.DefaultMaterial3;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.FrustumG;
import com.tyrlib2.scene.SceneManager;
import com.tyrlib2.scene.SceneNode;

/**
 * This class initiates the actual rendering.
 * @author Sascha
 *
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    
	/** 
	 * These are the default rendering channels. They are rendered starting with the lowest number.
	 */
	
	public static final int BACKGROUND_CHANNEL = 0;
	public static final int DEFAULT_CHANNEL = 100;
	public static final int OVERLAY_CHANNEL = 1000;
	
	public static final int BYTES_PER_FLOAT = 4;
	
	private List<IFrameListener> frameListeners;
	private SparseArray<List<IRenderable>> renderChannels;
	private SceneNode rootSceneNode;
	private Viewport viewport;
	private Camera camera;
	private Context context;
	
	/** View projection matrix of the camera **/
	private float[] vpMatrix = new float[16];
	
	private long lastTime;
	
	private static float BILLION = 1000000000;
	
	protected boolean rendering = false;
	private boolean init = false;
	
	private FrustumG frustum;
	
	public OpenGLRenderer(Context context) {
		frameListeners = new Vector<IFrameListener>();
		renderChannels = new SparseArray<List<IRenderable>>();
		
		renderChannels.put(BACKGROUND_CHANNEL, new Vector<IRenderable>());
		renderChannels.put(DEFAULT_CHANNEL, new Vector<IRenderable>());
		renderChannels.put(OVERLAY_CHANNEL, new Vector<IRenderable>());
		
		rootSceneNode = new SceneNode();
		this.context = context;
		rendering = false;
	}
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
        // Enable depth testing
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
		
		// Create a default program for 2D primitives
		ProgramManager.getInstance().createProgram(	"BASIC", 
													context, 
													com.tyrlib2.R.raw.basic_color_vs, 
													com.tyrlib2.R.raw.basic_color_fs, 
													new String[]{"a_Position", "a_Color"});
		
		// Create a default program for textured 2D primitives
		ProgramManager.getInstance().createProgram(	"TEXTURED", 
													context, 
													com.tyrlib2.R.raw.textured_vs, 
													com.tyrlib2.R.raw.textured_fs, 
													new String[]{"a_Position", "a_TexCoordinate"});
		
		// Default program for 3D objects
		ProgramManager.getInstance()
					  .createProgram(DefaultMaterial3.PER_PIXEL_PROGRAM_NAME, 
									 context, 
									 com.tyrlib2.R.raw.textured_ppl_vs, 
									 com.tyrlib2.R.raw.textured_ppl_fs, 
									 new String[]{"a_Position", "a_Normal", "a_Color", "a_TexCoordinate", "a_BoneIndex", "a_BoneWeight"});
		
		// Create a material for point lights
		ProgramManager.getInstance().createProgram(	"POINT_LIGHT", 
													context, 
													com.tyrlib2.R.raw.point_light_vs, 
													com.tyrlib2.R.raw.point_light_fs, 
													new String[]{"a_Position"});
		
		// Create a material to render point sprites
		ProgramManager.getInstance().createProgram(	"POINT_SPRITE", 
													context, 
													com.tyrlib2.R.raw.point_sprite_vs, 
													com.tyrlib2.R.raw.point_sprite_fs, 
													new String[]{"a_Position"});
		
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
        
        lastTime = 0;
        
        rendering = true;
        init = true;
	}
	
	public void destroy() {
		rendering = false;
		renderChannels.clear();
	}
	
    public void onDrawFrame(GL10 unused) {
    	
    	if (rendering) {
    	
	        // Redraw background color
	    	GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		    
		    rootSceneNode.update();
		    
		    // Update the view matrix of the camera
		    camera.update();
		    
		    frustum = new FrustumG(	camera.getAbsolutePos(), 
		    						camera.getWorldLookDirection(), 
		    						camera.getWorldUpVector(), 
		    						viewport.getNearClip(), 
		    						viewport.getFarClip(),
		    						viewport.getNearClipWidth(),
		    						viewport.getNearClipHeight());
		    
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
    	
    	drawChannel(renderChannels.get(BACKGROUND_CHANNEL), vpMatrix);
    	
    	
    	GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    	drawChannel(renderChannels.get(DEFAULT_CHANNEL), vpMatrix);
    	GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    	
    	float[] proj = new float[16];
    	Matrix.orthoM(proj, 0, 0, 1, 0, 1, -1, 1);
    	
    	drawChannel(renderChannels.get(OVERLAY_CHANNEL), proj);
    	
    }

	private void drawChannel(List<IRenderable> renderables, float[] transformMatrix) {
	    for (int i = 0; i < renderables.size(); ++i) {
	    	IRenderable r = renderables.get(i);
	    	AABB boundingBox = r.getBoundingBox();
	    	if (boundingBox == null) {
	    		r.render(transformMatrix);
	    	} else if (frustum.aabbInFrustum(boundingBox)) {
	    		r.render(transformMatrix);
	    	}
	    }
	}
    
    
    private void updateListeners() {
	    
	    if (lastTime != 0) {
	    	
	    	try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	long time = System.nanoTime();
	    	long diff = time - lastTime;
	    	
	        for (IFrameListener listener : frameListeners) {
	        	listener.onFrameRendered(diff / BILLION);
	        }
        
	    }
        
        lastTime = System.nanoTime();
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
    	renderChannels.get(DEFAULT_CHANNEL).add(renderable);
    }
    
    public void removeRenderable(IRenderable renderable) {
    	renderChannels.get(DEFAULT_CHANNEL).remove(renderable);
    }
    
    public void addRenderable(IRenderable renderable, int channel) {
    	List<IRenderable> renderables = renderChannels.get(channel);
    	if (renderables != null) {
    		renderables.add(renderable);
    	} else {
    		renderables = new Vector<IRenderable>();
    		renderChannels.put(channel, renderables);
    		renderables.add(renderable);
    	}
    }
    
    public void removeRenderable(IRenderable renderable, int channel) {
    	List<IRenderable> renderables = renderChannels.get(channel);
    	if (renderables != null) {
    		renderables.remove(renderable);
    	} 
    }
    
}
