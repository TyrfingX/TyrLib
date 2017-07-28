package com.tyrlib2.graphics.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

import com.tyrlib2.main.OpenGLActivity;
import com.tyrlib2.main.OpenGLSurfaceView;

/**
 * This class initiates the actual rendering.
 * @author Sascha
 *
 */

public class AndroidOpenGLRenderer extends OpenGLRenderer implements GLSurfaceView.Renderer {
	
	private OpenGLActivity activity;
	
	public AndroidOpenGLRenderer(OpenGLActivity activity) {
		super();
		this.activity = activity;
	}
	
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		
	//	GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glEnable(GLES20.GL_ALPHA_BITS);
		//GLES20.glSampleCoverage(4, false);
		
		defaultSetup();
		activity.loadShaders();
		startRendering();

	}
	
	public void onDrawFrame(GL10 unused) {
		render();
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void onSurfaceChanged(GL10 unused, int width, int height) {
    	surfaceChanged(width, height);
    }

	@Override
	public void queueEvent(Runnable r) {
		OpenGLSurfaceView.instance.queueEvent(r);
	}
}
