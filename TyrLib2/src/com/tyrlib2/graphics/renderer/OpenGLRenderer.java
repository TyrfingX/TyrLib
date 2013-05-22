package com.tyrlib2.graphics.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.SparseArray;

import com.tyrlib2.graphics.lighting.Light;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.Octree;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.FrustumG;
import com.tyrlib2.math.Vector3;

/**
 * This class initiates the actual rendering.
 * @author Sascha
 *
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    
	private class RenderChannel {
		Octree octree;
		List<IRenderable> renderables;
		boolean enabled = true;
		
		public RenderChannel() {
			octree = new Octree(5, 2000, new Vector3(), 200);
			renderables = new ArrayList<IRenderable>();
			octree.attachTo(rootSceneNode);
		}
	}
	
	/** 
	 * These are the default rendering channels. They are rendered starting with the lowest number.
	 */
	
	public static final int BACKGROUND_CHANNEL = 0;
	public static final int DEFAULT_CHANNEL = 100;
	public static final int TRANSLUCENT_CHANNEL = 200;
	public static final int OVERLAY_CHANNEL = 1000;
	
	public static final int BYTES_PER_FLOAT = 4;
	
	private static List<IFrameListener> frameListeners;
	private static SparseArray<RenderChannel> renderChannels;
	private static SceneNode rootSceneNode;
	private static Viewport viewport;
	private static Camera camera;
	private Context context;
	
	/** View projection matrix of the camera **/
	private float[] vpMatrix = new float[16];
	
	private float[] proj = new float[16];
	
	private long lastTime;
	
	private static float BILLION = 1000000000;
	
	protected boolean rendering = false;
	private static boolean init = false;
	
	private FrustumG frustum;
	
	public OpenGLRenderer(Context context) {
		if (!init) {
			frameListeners = new Vector<IFrameListener>();
			renderChannels = new SparseArray<RenderChannel>();
			
			rootSceneNode = new SceneNode();
	
			renderChannels.put(BACKGROUND_CHANNEL, new RenderChannel());
			renderChannels.put(DEFAULT_CHANNEL, new RenderChannel());
			renderChannels.put(TRANSLUCENT_CHANNEL, new RenderChannel());
			renderChannels.put(OVERLAY_CHANNEL, new RenderChannel());
		}
		
		rendering = false;
		this.context = context;
	}
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
        // Enable depth testing
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );
        
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		
		// Set the blend function
		
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);

		ProgramManager.getInstance().recreateAll();
		TextureManager.getInstance().reloadAll(context);
		SceneManager.getInstance().recreateFonts(context);

		
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
		
		// Create a material for rendering terrain
		ProgramManager.getInstance().createProgram(	"TERRAIN", 
													context, 
													com.tyrlib2.R.raw.terrain_vs, 
													com.tyrlib2.R.raw.terrain_fs, 
													new String[]{"a_Position", "a_Normal", "a_TexCoordinate", "a_TexWeights"});
		
		// Create a material to render point sprites
		ProgramManager.getInstance().createProgram(	"POINT_SPRITE", 
													context, 
													com.tyrlib2.R.raw.point_sprite_vs, 
													com.tyrlib2.R.raw.point_sprite_fs, 
													new String[]{"a_Position, a_Color"});
		
		// Create a program for rendering shadow depth maps
		ProgramManager.getInstance().createProgram(	"SHADOW_DEPTH", 
													context, 
													com.tyrlib2.R.raw.depth_vs, 
													com.tyrlib2.R.raw.depth_fs, 
													new String[]{"aPosition" });

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        viewport = new Viewport();
        
        lastTime = 0;
        
        rendering = true;
        init = true;
        
        for (int i = 0; i < frameListeners.size(); ++i) {
           	frameListeners.get(i).onSurfaceCreated();
        }
	}
	
	public void destroy() {
		rendering = false;
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
		    
			GLES20.glViewport(-(int)(viewport.getWidth()*0.2f), -(int)(viewport.getHeight()*0.2f), (int)(viewport.getWidth()*1.4f), (int)(viewport.getHeight()*1.4f));
		    
		    Matrix.multiplyMM(vpMatrix, 0, viewport.projectionMatrix, 0, camera.viewMatrix, 0);
		    
		    drawScene();
		    updateListeners();

    	}
        
    }
    
    private void drawScene() {
    	RenderChannel renderChannel = renderChannels.get(BACKGROUND_CHANNEL);
    	
    	if (renderChannel.enabled) {
    		drawChannel(renderChannel, vpMatrix);
    	}
    	
    	GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    	renderChannel = renderChannels.get(DEFAULT_CHANNEL);
    	if (renderChannel.enabled) {
    		drawChannel(renderChannel, vpMatrix);
    	}
    	
    	renderChannel = renderChannels.get(TRANSLUCENT_CHANNEL);
    	if (renderChannel.enabled) {
    		drawChannel(renderChannel, vpMatrix);
    	}
    	GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    	

    	
    	renderChannel = renderChannels.get(OVERLAY_CHANNEL);
    	if (renderChannel.enabled) {
	    	Matrix.orthoM(proj, 0, -viewport.getWidth()*0.2f, viewport.getWidth()*1.2f, -viewport.getHeight()*0.2f, viewport.getHeight()*1.2f, -1, 1);
	    	
			// Draw all unbounded objects
			if (renderChannel.renderables != null) {
			    for (int i = 0; i < renderChannel.renderables.size(); ++i) {
			    	renderChannel.renderables.get(i).render(proj);
			    }
			}
    	}
    	
    }
    
    public void setRenderChannelEnabled(boolean enabled, int channel) {
    	RenderChannel renderChannel = renderChannels.get(channel);
    	renderChannel.enabled = enabled;
    }

	private void drawChannel(RenderChannel channel, final float[] transformMatrix) {
		
		// Now draw all bounded objects
		if (channel.octree != null) {
			channel.octree.update();
			channel.octree.query(new RenderSceneQuery(frustum, transformMatrix));    
		}
		
		// Draw all unbounded objects
		if (channel.renderables != null) {
		    int countRenderables = channel.renderables.size();
			for (int i = 0; i < countRenderables; ++i) {
		    	channel.renderables.get(i).render(transformMatrix);
		    }
		}
	}
    
    
    private void updateListeners() {
	    
	    if (lastTime != 0) {
	    	

	    	
	    	long time = System.nanoTime();
	    	long diff = time - lastTime;
	    	
	    	lastTime = System.nanoTime();
	    	
	        for (IFrameListener listener : frameListeners) {
	        	listener.onFrameRendered(diff / BILLION);
	        }
        
	    } else {
	    	lastTime = System.nanoTime();
	    }
        
        
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
       viewport.setFullscreen(width, height);
       
       for (int i = 0; i < frameListeners.size(); ++i) {
       	frameListeners.get(i).onSurfaceChanged();
       }
       
		if (init) {
			ProgramManager.getInstance().recreateAll();
			TextureManager.getInstance().reloadAll(context);
			SceneManager.getInstance().recreateFonts(context);
		}
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
    
    public FrustumG getFrustum() {
    	return frustum;
    }

    public void addRenderable(IRenderable renderable) {
    	this.addRenderable(renderable, DEFAULT_CHANNEL);
    }
    
    public void addRenderable(BoundedRenderable renderable) {
    	this.addRenderable(renderable, DEFAULT_CHANNEL);
    }
    
    public void addRenderable(BoundedSceneObject renderable) {
    	this.addRenderable(renderable, DEFAULT_CHANNEL);
    }
    
    public void removeRenderable(IRenderable renderable) {
    	this.removeRenderable(renderable, DEFAULT_CHANNEL);
    }
    
    public void removeRenderable(BoundedRenderable renderable) {
    	this.removeRenderable(renderable, DEFAULT_CHANNEL);
    }
    
    public void removeRenderable(BoundedSceneObject renderable) {
    	this.removeRenderable(renderable, DEFAULT_CHANNEL);
    }
    
    public void addRenderable(IRenderable renderable, int channel) {
    	RenderChannel renderChannel = renderChannels.get(channel);
    	if (renderChannel != null) {
    		renderChannel.renderables.add(renderable);
    	} else {
    		renderChannel = new RenderChannel();
    		renderChannels.put(channel, renderChannel);
    		renderChannel.renderables.add(renderable);
    	}
    }
    
    public void addRenderable(BoundedRenderable renderable, int channel) {
    	RenderChannel renderChannel = renderChannels.get(channel);
    	if (renderChannel != null) {
    		if (channel != OVERLAY_CHANNEL) {
    			renderChannel.octree.addObject(renderable);
    		} else {
    			renderChannel.renderables.add(renderable);
    		}
    	} else {
    		renderChannel = new RenderChannel();
    		renderChannels.put(channel, renderChannel);
    		renderChannel.octree.addObject(renderable);
    	}
    }
    
    public void addRenderable(BoundedSceneObject renderable, int channel) {
    	if (renderable instanceof IRenderable) {
	    	RenderChannel renderChannel = renderChannels.get(channel);
	    	if (renderChannel != null) {
	    		if (channel != OVERLAY_CHANNEL) {
	    			renderChannel.octree.addObject(renderable);
	    		} else {
	    			renderChannel.renderables.add((IRenderable) renderable);
	    		}
	    	} else {
	    		renderChannel = new RenderChannel();
	    		renderChannels.put(channel, renderChannel);
	    		renderChannel.octree.addObject(renderable);
	    	}
    	}
    }
    
    public void removeRenderable(IRenderable renderable, int channel) {
    	RenderChannel renderChannel = renderChannels.get(channel);
    	if (renderChannel != null) {
    		renderChannel.renderables.remove(renderable);
    	} 
    }

    public void removeRenderable(BoundedSceneObject renderable, int channel) {
    	if (renderable instanceof IRenderable) {
	    	RenderChannel renderChannel = renderChannels.get(channel);
	    	if (renderChannel != null) {
	    		renderChannel.octree.removeObject(renderable);
	    	} 
    	}
    }
    
    public void removeRenderable(BoundedRenderable renderable, int channel) {
    	RenderChannel renderChannel = renderChannels.get(channel);
    	if (renderChannel != null) {
    		renderChannel.octree.removeObject(renderable);
    	} 
    }
    
    public IRenderable getRenderable(int index) {
    	RenderChannel renderChannel = renderChannels.get(DEFAULT_CHANNEL);
    	if (renderChannel != null) {
    		return renderChannel.renderables.get(index);
    	}
    	
    	return null;
    }
    
    public int getCountRenderables() {
    	RenderChannel renderChannel = renderChannels.get(DEFAULT_CHANNEL);
    	if (renderChannel != null) {
    		return renderChannel.renderables.size();
    	}
    	return 0;
    }
    
    public Octree getOctree(int channel) {
    	return renderChannels.get(DEFAULT_CHANNEL).octree;
    }
}
