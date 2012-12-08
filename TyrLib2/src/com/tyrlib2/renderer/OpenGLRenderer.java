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

/**
 * This class initiates the actual rendering.
 * @author Sascha
 *
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    
	public static final int BYTES_PER_FLOAT = 4;
	
	private List<IFrameListener> frameListeners;
	private List<Renderable> renderables;
	private Viewport viewport;
	private Camera camera;
	private Context context;
	
	private float[] vpMatrix = new float[16];
	
	public OpenGLRenderer(Context context) {
		frameListeners = new ArrayList<IFrameListener>();
		renderables = new ArrayList<Renderable>();
		this.context = context;
	}
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
		// Create a default program to use
		String basicVertexShader = FileReader.readRawFile(context, com.tyrlib2.R.raw.basic_color_vs);
		String basicFragmentShader = FileReader.readRawFile(context, com.tyrlib2.R.raw.basic_color_fs);
		ShaderManager.getInstance().loadShader("BASIC_VS", GLES20.GL_VERTEX_SHADER, basicVertexShader);
		ShaderManager.getInstance().loadShader("BASIC_FS", GLES20.GL_FRAGMENT_SHADER, basicFragmentShader);
		ProgramManager.getInstance().createProgram("BASIC", "BASIC_VS", "BASIC_FS", new String[]{"a_Position", "a_Color"});
		
        // Set the background frame color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        viewport = new Viewport();
        
        for (IFrameListener listener : frameListeners) {
        	listener.onSurfaceCreated();
        }
    }
    
    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
	    Matrix.multiplyMM(vpMatrix, 0, viewport.projectionMatrix, 0, camera.viewMatrix, 0);
	    
	    for (int i = 0; i < renderables.size(); ++i) {
	    	renderables.get(i).render(vpMatrix);
	    }
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
    
    public void addRenderable(Renderable renderable) {
    	renderables.add(renderable);
    }
    
    public void removeRenderable(Renderable renderable) {
    	renderables.remove(renderable);
    }

}
