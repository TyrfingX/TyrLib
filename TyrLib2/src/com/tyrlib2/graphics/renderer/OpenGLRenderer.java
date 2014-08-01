package com.tyrlib2.graphics.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.tyrlib2.graphics.lighting.Light;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.Octree;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.FrustumG;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector3;

public abstract class OpenGLRenderer extends GameLoop {

	/** 
	 * These are the default rendering channels. They are rendered starting with the lowest number.
	 */
	public static final int BACKGROUND_CHANNEL = 0;
	public static final int DEFAULT_CHANNEL = 1;
	public static final int TRANSLUCENT_CHANNEL = 2;
	public static final int OVERLAY_CHANNEL = 3;
	public static final int BYTES_PER_FLOAT = 4;
	protected List<RenderChannel> renderChannels;
	protected Viewport viewport;
	
	protected Camera camera;
	
	/** View projection matrix of the camera **/
	protected float[] vpMatrix = new float[16];
	
	private float[] proj = new float[16];
	

	protected FrustumG frustum;
	private static int textureFails = 0;
	private RenderSceneQuery query = new RenderSceneQuery();
	
	class RenderChannel {
		Octree octree;
		List<IRenderable> renderables;
		boolean enabled = true;
		int priority;
		
		public RenderChannel(int priority) {
			octree = new Octree(5, 2000, new Vector3(), 200);
			renderables = new ArrayList<IRenderable>();
			octree.attachTo(rootSceneNode);
		}
	}
	
	public OpenGLRenderer() {
		super(false);
		if (!init) {
			renderChannels = new ArrayList<RenderChannel>();
	
			renderChannels.add(new RenderChannel(BACKGROUND_CHANNEL));
			renderChannels.add(new RenderChannel(DEFAULT_CHANNEL));
			renderChannels.add(new RenderChannel(TRANSLUCENT_CHANNEL));
			renderChannels.add(new RenderChannel(OVERLAY_CHANNEL));
		}
		
		rendering = false;
	}
	
	public void destroyRenderables() {
		frameListeners = new Vector<IFrameListener>();
		renderChannels = new ArrayList<RenderChannel>();
		rootSceneNode = new SceneNode();
		
		renderChannels.clear();
		renderChannels.add(new RenderChannel(BACKGROUND_CHANNEL));
		renderChannels.add(new RenderChannel(DEFAULT_CHANNEL));
		renderChannels.add(new RenderChannel(TRANSLUCENT_CHANNEL));
		renderChannels.add(new RenderChannel(OVERLAY_CHANNEL));
	}
	
	@Override
	public void destroyRenderables(int channel) {
		renderChannels.remove(channel);
		renderChannels.add(channel, new RenderChannel(channel));
	}

	protected void drawScene() {
		if (!skipRendering) {
			RenderChannel renderChannel = renderChannels.get(BACKGROUND_CHANNEL);
			
			if (renderChannel.enabled) {
				drawChannel(renderChannel, vpMatrix);
			}
			
			TyrGL.glEnable(TyrGL.GL_DEPTH_TEST);
			renderChannel = renderChannels.get(DEFAULT_CHANNEL);
			if (renderChannel.enabled) {
				drawChannel(renderChannel, vpMatrix);
			}
			
			renderChannel = renderChannels.get(TRANSLUCENT_CHANNEL);
			if (renderChannel.enabled) {
				drawChannel(renderChannel, vpMatrix);
			}
			TyrGL.glDisable(TyrGL.GL_DEPTH_TEST);
			
		
			
			renderChannel = renderChannels.get(OVERLAY_CHANNEL);
			if (renderChannel.enabled) {
		    	Matrix.orthoM(proj, 0, 0, viewport.getWidth(), 0, viewport.getHeight(), -1, 1);
		    	
				// Draw all unbounded objects
				if (renderChannel.renderables != null) {
				    for (int i = 0; i < renderChannel.renderables.size(); ++i) {
				    	renderChannel.renderables.get(i).render(proj);
				    }
				}
			}
		}
		
	}

	@Override
	public void setRenderChannelEnabled(boolean enabled, int channel) {
		RenderChannel renderChannel = renderChannels.get(channel);
		renderChannel.enabled = enabled;
	}

	private void drawChannel(RenderChannel channel, final float[] transformMatrix) {
		
		// Now draw all bounded objects
		if (channel.octree != null) {
			channel.octree.update();
			query.init(frustum, transformMatrix);
			channel.octree.query(query);   
		}
		
		// Draw all unbounded objects
		if (channel.renderables != null) {
		    int countRenderables = channel.renderables.size();
			for (int i = 0; i < countRenderables; ++i) {
		    	channel.renderables.get(i).render(transformMatrix);
		    }
		}
	}

	@Override
	public Viewport getViewport() {
		return viewport;
	}

	@Override
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public FrustumG getFrustum() {
		return frustum;
	}

	@Override
	public void addRenderable(IRenderable renderable) {
		this.addRenderable(renderable, DEFAULT_CHANNEL);
	}

	@Override
	public void addRenderable(BoundedRenderable renderable) {
		this.addRenderable(renderable, DEFAULT_CHANNEL);
	}

	@Override
	public void addRenderable(BoundedSceneObject renderable) {
		this.addRenderable(renderable, DEFAULT_CHANNEL);
	}

	@Override
	public void removeRenderable(IRenderable renderable) {
		this.removeRenderable(renderable, DEFAULT_CHANNEL);
	}

	@Override
	public void removeRenderable(BoundedRenderable renderable) {
		this.removeRenderable(renderable, DEFAULT_CHANNEL);
	}

	@Override
	public void removeRenderable(BoundedSceneObject renderable) {
		this.removeRenderable(renderable, DEFAULT_CHANNEL);
	}

	@Override
	public void addRenderable(IRenderable renderable, int channel) {
		RenderChannel renderChannel = renderChannels.get(channel);
		if (renderChannel != null) {
			renderChannel.renderables.add(renderable);
		} else {
			renderChannel = new RenderChannel(channel);
			renderChannels.add(renderChannel);
			renderChannel.renderables.add(renderable);
		}
	}

	@Override
	public void addRenderable(BoundedRenderable renderable, int channel) {
		RenderChannel renderChannel = renderChannels.get(channel);
		if (renderChannel != null) {
			if (channel != OVERLAY_CHANNEL) {
				renderChannel.octree.addObject(renderable);
			} else {
				renderChannel.renderables.add(renderable);
			}
		} else {
			renderChannel = new RenderChannel(channel);
			renderChannels.add(renderChannel);
			renderChannel.octree.addObject(renderable);
		}
	}

	@Override
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
	    		renderChannel = new RenderChannel(channel);
	    		renderChannels.add(renderChannel);
	    		renderChannel.octree.addObject(renderable);
	    	}
		}
	}

	@Override
	public void removeRenderable(IRenderable renderable, int channel) {
		RenderChannel renderChannel = renderChannels.get(channel);
		if (renderChannel != null) {
			renderChannel.renderables.remove(renderable);
		} 
	}

	@Override
	public void removeRenderable(BoundedSceneObject renderable, int channel) {
		if (renderable instanceof IRenderable) {
	    	RenderChannel renderChannel = renderChannels.get(channel);
	    	if (renderChannel != null) {
	    		renderChannel.octree.removeObject(renderable);
	    	} 
		}
	}
	
	@Override
	public void removeRenderable(BoundedRenderable renderable, int channel) {
		RenderChannel renderChannel = renderChannels.get(channel);
		if (renderChannel != null) {
			renderChannel.octree.removeObject(renderable);
		} 
	}

	@Override
	public IRenderable getRenderable(int index) {
		RenderChannel renderChannel = renderChannels.get(DEFAULT_CHANNEL);
		if (renderChannel != null) {
			return renderChannel.renderables.get(index);
		}
		
		return null;
	}

	@Override
	public int getCountRenderables() {
		RenderChannel renderChannel = renderChannels.get(DEFAULT_CHANNEL);
		if (renderChannel != null) {
			return renderChannel.renderables.size();
		}
		return 0;
	}

	@Override
	public Octree getOctree(int channel) {
		return renderChannels.get(DEFAULT_CHANNEL).octree;
	}
	
	public abstract void loadShaders();
	
	public void defaultSetup() {
        // Enable depth testing
		TyrGL.glDepthFunc( TyrGL.GL_LEQUAL );
		TyrGL.glDepthMask( true );
        
		// Use culling to remove back faces.
		TyrGL.glEnable(TyrGL.GL_CULL_FACE);
		TyrGL.glCullFace(TyrGL.GL_BACK);
		
		// Set the blend function
		
		TyrGL.glBlendFunc(TyrGL.GL_ONE, TyrGL.GL_ONE);

		ProgramManager.getInstance().recreateAll();
		TextureManager.getInstance().reloadAll();
		SceneManager.getInstance().recreateFonts();
		
        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(this);
	}
	
	@Override
	public void startRendering() {
        // Set the background frame color
		TyrGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        viewport = new Viewport();
        
        init = true;
        
        super.startRendering();
	}
	
	public void surfaceChanged(int width, int height) {
		System.out.println("Surface Changed: Reconstructing Context");
		if (init) {
	       viewport.setFullscreen(width, height);
	       
	       for (int i = 0; i < frameListeners.size(); ++i) {
	    	   frameListeners.get(i).onSurfaceChanged();
	       }
       
		
			ProgramManager.getInstance().recreateAll();
			TextureManager.getInstance().reloadAll();
			//SceneManager.getInstance().recreateFonts();
		}
	}
	
	@Override
	public void render() {
    	if (rendering) {
    		
    		try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
	        // Redraw background color
    		TyrGL.glClear(TyrGL.GL_DEPTH_BUFFER_BIT | TyrGL.GL_COLOR_BUFFER_BIT);
		    
		    rootSceneNode.update();
		    
		    // Update the view matrix of the camera
		    camera.update();
		    
		    if (frustum == null) {
			    frustum = new FrustumG();
		    }
		    
		    frustum.update(	camera.getAbsolutePos(), 
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

		    TyrGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		    TyrGL.glClear( TyrGL.GL_DEPTH_BUFFER_BIT | TyrGL.GL_COLOR_BUFFER_BIT);
		    
		    TyrGL.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());
		    
		    Matrix.multiplyMM(vpMatrix, 0, viewport.projectionMatrix, 0, camera.viewMatrix, 0);
		    
		    drawScene();
		    super.render();
		    
		    setTextureFails(0);

    	}
        
	}

	public static int getTextureFails() {
		return textureFails;
	}

	public static void setTextureFails(int textureFails) {
		OpenGLRenderer.textureFails = textureFails;
	}

}