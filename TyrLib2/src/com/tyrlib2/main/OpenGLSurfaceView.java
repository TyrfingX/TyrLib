package com.tyrlib2.main;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.tyrlib2.graphics.renderer.AndroidOpenGLRenderer;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.input.AndroidMotionEvent;
import com.tyrlib2.input.AndroidView;
import com.tyrlib2.input.InputManager;

/**
 * View for the game. Properly sets the renderer up and sets the rendering options.
 * 
 * @author Sascha
 *
 */

public class OpenGLSurfaceView extends GLSurfaceView {
	
	private OpenGLSurfaceView instance = this;
	private AndroidView view = new AndroidView(this);
	
	public OpenGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());
        
        InputManager.getInstance();
        
        AndroidOpenGLRenderer renderer = new AndroidOpenGLRenderer();
        
        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(renderer);
        
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        
        setKeepScreenOn(true);
        
        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
    }
	
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {    	
    	final MotionEvent eventCopy = MotionEvent.obtain(event);
    	queueEvent(new Runnable() {
			@Override
			public void run() {
				InputManager.getInstance().onTouch(view, new AndroidMotionEvent(eventCopy));
			}
	    });
    	return true;
    }
    
}
