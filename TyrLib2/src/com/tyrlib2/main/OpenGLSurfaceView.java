package com.tyrlib2.main;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.ShaderManager;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;

/**
 * View for the game. Properly sets the renderer up and sets the rendering options.
 * 
 * @author Sascha
 *
 */

public class OpenGLSurfaceView extends GLSurfaceView {
	
	private OpenGLSurfaceView instance = this;
	
	public OpenGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MultisampleConfigChooser());

        
        OpenGLRenderer renderer = new OpenGLRenderer(context);
        
        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(renderer);
        
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        
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
				InputManager.getInstance().onTouch(instance, eventCopy);
			}
	    });
    	return true;
    }
    
}
