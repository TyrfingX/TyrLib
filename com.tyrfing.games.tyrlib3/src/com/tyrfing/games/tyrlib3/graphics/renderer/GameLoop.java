package com.tyrfing.games.tyrlib3.graphics.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.tyrfing.games.tyrlib3.graphics.compositors.Composit;
import com.tyrfing.games.tyrlib3.graphics.compositors.Compositor;
import com.tyrfing.games.tyrlib3.graphics.compositors.Precision;
import com.tyrfing.games.tyrlib3.graphics.lighting.Light;
import com.tyrfing.games.tyrlib3.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.graphics.scene.Octree;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.math.FrustumG;

public abstract class GameLoop {
	
	public static float BILLION = 1000000000;
	
	public IErrorHandler errorHandler;

	protected List<IFrameListener> frameListeners;
	protected long lastTime;
	protected boolean rendering = false;
	protected SceneNode rootSceneNode;
	protected boolean skipRendering;
	
	protected static boolean init = false;
	public boolean serverMode;

	public List<IRenderable> toRender = new ArrayList<IRenderable>();
	
	public GameLoop(boolean serverMode) {
		
		this.serverMode = serverMode;
		
		if (!init) {
			rootSceneNode = new SceneNode();
			frameListeners = new Vector<IFrameListener>();
		}
	}
	
	public boolean isInServerMode() {
		return serverMode;
	}
	
	public void setSkipRendering(boolean skipRendering) {
		this.skipRendering = skipRendering;
	}
	
	public abstract void queueEvent(Runnable r);
	
	public void startRendering() {
		System.out.println("Surface created");
        lastTime = 0;
        rendering = true;
		
        for (int i = 0; i < frameListeners.size(); ++i) {
           	frameListeners.get(i).onSurfaceCreated();
        }
	}

	public void render() {
		try {
		    if (lastTime != 0) {
		    	
		    	long time = System.nanoTime();
		    	long diff = time - lastTime;
		    	
		    	lastTime = System.nanoTime();
		    	updateListeners(diff);
		    } else {
		    	lastTime = System.nanoTime();
		    }
		} catch (Exception e) {
			e.printStackTrace();
			
			if (errorHandler != null) {
				errorHandler.onError();
			}
		}
	}
	
	protected void updateListeners(long diff) {
        for (int i = 0; i < frameListeners.size(); ++i) {
        	frameListeners.get(i).onFrameRendered(diff / BILLION);
        }
	}
	
	public void destroy() {
		rendering = false;
		init = false;
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
	
	public SceneNode getRootSceneNode() {
		return rootSceneNode;
	}
	
	public void destroyRenderables(int channel) {}
	public void setRenderChannelEnabled(boolean enabled, int channel) {}
	public Viewport getViewport() { return null; }
	public void setViewport(Viewport viewport) {} 
	public void setCamera(Camera camera) { }
	public Camera getCamera() { return null; }
	public FrustumG getFrustum() { return null; }
	public void addRenderable(IRenderable renderable) {}
	public void addRenderable(BoundedRenderable renderable) {}
	public void addRenderable(BoundedSceneObject renderable) {}
	public void removeRenderable(IRenderable renderable) {}
	public void removeRenderable(BoundedRenderable renderable) {}
	public void removeRenderable(BoundedSceneObject renderable) {}
	public void addRenderable(IRenderable renderable, int channel) {}
	public void addRenderable(BoundedRenderable renderable, int channel) {}
	public void addRenderable(BoundedSceneObject renderable, int channel) {}
	public void removeRenderable(IRenderable renderable, int channel) {}
	public void removeRenderable(BoundedSceneObject renderable, int channel) {}
	public void removeRenderable(BoundedRenderable renderable, int channel) {}
	public IRenderable getRenderable(int index) { return null; }
	public int getCountRenderables() { return 0; }
	public Octree getOctree(int channel) { return null; }
	public void setShadowsEnabled(boolean state, Light caster, int textureSizes[], float distances[]) { }
	public boolean isShadowsEnabled() { return false; }
	public int getShadowMapHandle() { return 0; }
	public float[] getShadowVP() { return null; }
	public int getShadowModelHandle() {return 0; }
	public Program getShadowProgram(boolean animated) {return null;}
	public void enableOffscreenRendering(Precision precision) {} ;
	public void addComposit(Composit composit) { }
	public Composit getComposit(int index) { return null; }
	public Compositor getCompositor() { return null; }
	
}
