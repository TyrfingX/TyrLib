package com.tyrlib2.graphics.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.tyrlib2.graphics.lighting.Light;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.Octree;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.main.Media;
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
	protected float[] shadowVPMatrix = new float[16];
	protected float[] shadowViewMatrix = new float[16];
	
	private float[] proj = new float[16];

	protected FrustumG frustum;
	private static int textureFails = 0;
	private RenderSceneQuery query = new RenderSceneQuery();
	private RenderShadowSceneQuery queryShadow = new RenderShadowSceneQuery();
	
	protected boolean shadowsEnabled;
	protected Light shadowCastingLight;
	
	private Program shadowProgram;
	private Program shadowProgramAnim;
	
	private int shadowMVPHandle;
	private int shadowMVPHandleAnim;

	private int shadowDistanceIndex;
	private int[] depthTextures;
	private int[] shadowBuffers;
	private int[] shadowTextureSizes;
	private float[] shadowDistances;
	
	private Comparator<IRenderable> comparator = new Comparator<IRenderable>() {
		@Override
		public int compare(IRenderable lhs, IRenderable rhs) {
			if (lhs.getInsertionID() < rhs.getInsertionID()) {
				return -1;
			} else if (rhs.getInsertionID() < lhs.getInsertionID()) {
				return 1;
			}
			return 0;
		}
	};
	
	class RenderChannel {
		Octree octree;
		List<IRenderable> renderables;
		boolean enabled = true;
		int priority;
		int countTotalRenderables;
		
		public RenderChannel(int priority) {
			octree = new Octree(5, 20, new Vector3(), 2000);
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
	
	@Override
	public void setShadowsEnabled(boolean state, Light caster, int textureSizes[], float distances[]) {
		this.shadowsEnabled = state;
		
		if (state) {
			
			this.shadowTextureSizes = textureSizes;
			this.shadowDistances = distances;
			
			shadowBuffers = new int[shadowTextureSizes.length];
			depthTextures = new int[shadowTextureSizes.length];
			
			shadowCastingLight = caster;
			
			for (int i = 0; i < textureSizes.length; ++i) {
				int[] buffer = new int[1];
				TyrGL.glGenFramebuffers(1, buffer, 0);
				shadowBuffers[i] = buffer[0];
				TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, shadowBuffers[i]);
				
				TyrGL.glGenTextures(1, buffer, 0);
				depthTextures[i] = buffer[0];
				TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, depthTextures[i]);
				
				TyrGL.glTexImage2D(	TyrGL.GL_TEXTURE_2D, 0, TyrGL.GL_DEPTH_COMPONENT16, 
									shadowTextureSizes[i],shadowTextureSizes[i], 0, TyrGL.GL_DEPTH_COMPONENT, TyrGL.GL_UNSIGNED_INT, null);
				
				TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_NEAREST);
				TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_NEAREST);
				TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_WRAP_S, TyrGL.GL_CLAMP_TO_EDGE);
				TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_WRAP_T, TyrGL.GL_CLAMP_TO_EDGE);
				
				TyrGL.glFramebufferTexture2D(TyrGL.GL_FRAMEBUFFER, TyrGL.GL_DEPTH_ATTACHMENT, TyrGL.GL_TEXTURE_2D, depthTextures[i], 0);
			
				TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
				
			}
			
			ProgramManager.getInstance().createProgram(	"SHADOW_DEPTH", 
														Media.CONTEXT.getResourceID("depth_vs", "raw"), 
														Media.CONTEXT.getResourceID("depth_fs", "raw"), 
														new String[]{"a_Position"});
			
			ProgramManager.getInstance().createProgram(	"SHADOW_DEPTH_ANIM", 
														Media.CONTEXT.getResourceID("animated_depth_vs", "raw"), 
														Media.CONTEXT.getResourceID("depth_fs", "raw"), 
														new String[]{"a_Position", "a_BoneIndex", "a_BoneWeight"});
			
			
			shadowProgram = ProgramManager.getInstance().getProgram("SHADOW_DEPTH");
			shadowMVPHandle = TyrGL.glGetUniformLocation(shadowProgram.handle, "u_SMVP");
			
			shadowProgramAnim = ProgramManager.getInstance().getProgram("SHADOW_DEPTH_ANIM");
			shadowMVPHandleAnim = TyrGL.glGetUniformLocation(shadowProgram.handle, "u_SMVP");
		}
		
	}
	
	public boolean isShadowsEnabled() { 
		return shadowsEnabled; 
	}
	
	@Override
	public int getShadowMapHandle() {
		return depthTextures[shadowDistanceIndex]; 
	}
	
	@Override
	public float[] getShadowVP() { 
		return shadowVPMatrix; 
	}
	
	@Override
	public int getShadowModelHandle() {
		return 0; 
	}
	
	@Override
	public Program getShadowProgram(boolean animated) {
		if (animated) {
			return shadowProgramAnim;
		} else {
			return shadowProgram;
		}
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
			
			if (shadowsEnabled) {
				//TyrGL.glCullFace(TyrGL.GL_FRONT);
				shadowDistanceIndex = -1;
				float cameraDistance = camera.getAbsolutePos().z;
				
				for (int i = 0; i < shadowTextureSizes.length; ++i) {
					if (cameraDistance < shadowDistances[i]) {
						shadowDistanceIndex = i;
						break;
					}
				}
				
				if (shadowDistanceIndex != -1) {
					TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, shadowBuffers[shadowDistanceIndex]);
					TyrGL.glClear( TyrGL.GL_DEPTH_BUFFER_BIT | TyrGL.GL_COLOR_BUFFER_BIT );
					
					TyrGL.glViewport(0, 0, 	shadowTextureSizes[shadowDistanceIndex], 
											shadowTextureSizes[shadowDistanceIndex]);
					Program.blendDisable();
					
					float[] lightDir = shadowCastingLight.getLightVector();
					
					Vector3 look = new Vector3(lightDir[0], lightDir[1], lightDir[2]);
					Vector3 right = look.cross(new Vector3(0,0,1));
					Vector3 up = right.cross(look);
					up.normalize();
					
					Matrix.orthoM(shadowVPMatrix,  0, -300, 300, -120, 300, -180, 180);
					Matrix.setLookAtM(	shadowViewMatrix, 
										0, 
										lightDir[0], lightDir[1], lightDir[2], 
										0,0,0,
										up.x,up.y,up.z);
				
					Matrix.multiplyMM(shadowVPMatrix, 0, shadowVPMatrix, 0, shadowViewMatrix, 0);
					
					renderChannel = renderChannels.get(DEFAULT_CHANNEL);
					if (renderChannel.enabled) {
						drawShadowChannel(renderChannel, vpMatrix);
					}
					
					renderChannel = renderChannels.get(TRANSLUCENT_CHANNEL);
					if (renderChannel.enabled) {
						drawShadowChannel(renderChannel, vpMatrix);
					}
					

					TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
					//TyrGL.glCullFace(TyrGL.GL_BACK);
					TyrGL.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());
				} else {
					shadowDistanceIndex = shadowTextureSizes.length-1;
					TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, shadowBuffers[shadowDistanceIndex]);
					TyrGL.glClear( TyrGL.GL_DEPTH_BUFFER_BIT | TyrGL.GL_COLOR_BUFFER_BIT );
					TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
				}
			}
			
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
			if (false) {
				channel.octree.setBoundingBoxVisible(true);
			}
			query.init(frustum, transformMatrix);
			channel.octree.query(query);   
		}
		
		Collections.sort(toRender, comparator);
		
		for (int i = 0, toRenderCount = toRender.size(); i < toRenderCount; ++i) {
			IRenderable renderable = toRender.get(i);
			renderable.render(transformMatrix);
		}
		
		toRender.clear();
		
		// Draw all unbounded objects
		if (channel.renderables != null) {
		    int countRenderables = channel.renderables.size();
			for (int i = 0; i < countRenderables; ++i) {
		    	channel.renderables.get(i).render(transformMatrix);
		    }
		}
	}
	
	private void drawShadowChannel(RenderChannel channel, final float[] transformMatrix) {
		
		// Now draw all bounded objects
		if (channel.octree != null) {
			channel.octree.update();
			queryShadow.init(frustum, transformMatrix);
			channel.octree.query(queryShadow);   
		}
		
		// Draw all unbounded objects
		if (channel.renderables != null) {
		    int countRenderables = channel.renderables.size();
			for (int i = 0; i < countRenderables; ++i) {
		    	channel.renderables.get(i).renderShadow(transformMatrix);
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
		if (renderChannel == null) {
			renderChannel = new RenderChannel(channel);
			renderChannels.add(renderChannel);
		}
		
		renderChannel.renderables.add(renderable);
		renderable.setInsertionID(renderChannel.countTotalRenderables);
		renderChannel.countTotalRenderables++;
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
		
		renderable.setInsertionID(renderChannel.countTotalRenderables);
		renderChannel.countTotalRenderables++;
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
	    	
	    	((IRenderable)renderable).setInsertionID(renderChannel.countTotalRenderables);
			renderChannel.countTotalRenderables++;
		}
	}

	@Override
	public void removeRenderable(IRenderable renderable, int channel) {
		RenderChannel renderChannel = renderChannels.get(channel);
		if (renderChannel != null) {
			renderChannel.renderables.remove(renderable);
			renderChannel.countTotalRenderables--;
		} 
	}

	@Override
	public void removeRenderable(BoundedSceneObject renderable, int channel) {
		if (renderable instanceof IRenderable) {
	    	RenderChannel renderChannel = renderChannels.get(channel);
	    	if (renderChannel != null) {
	    		renderChannel.octree.removeObject(renderable);
	    		renderChannel.countTotalRenderables--;
	    	} 
		}
	}
	
	@Override
	public void removeRenderable(BoundedRenderable renderable, int channel) {
		RenderChannel renderChannel = renderChannels.get(channel);
		if (renderChannel != null) {
			renderChannel.octree.removeObject(renderable);
			renderChannel.countTotalRenderables--;
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
		
		viewport.setFullscreen(width, height);
       
		for (int i = 0; i < frameListeners.size(); ++i) {
			frameListeners.get(i).onSurfaceChanged();
		}
		
		if (!init) {
			ProgramManager.getInstance().recreateAll();
			TextureManager.getInstance().reloadAll();
			//SceneManager.getInstance().recreateFonts();
		} 
	}
	
	@Override
	public void render() {
    	if (rendering) {
        	
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