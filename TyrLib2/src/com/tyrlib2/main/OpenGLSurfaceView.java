package com.tyrlib2.main;

import com.tyrlib2.renderer.OpenGLRenderer;
import com.tyrlib2.scene.SceneManager;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * View for the game. Properly sets the renderer up and sets the rendering options.
 * 
 * @author Sascha
 *
 */

public class OpenGLSurfaceView extends GLSurfaceView {
	public OpenGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        
        OpenGLRenderer renderer = new OpenGLRenderer(context);
        
        // Setup the SceneManager
        SceneManager.getInstance().setRenderer(renderer);
        
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        
        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }
}
