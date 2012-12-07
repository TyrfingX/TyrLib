package com.tyrfing.tyrlib2.main;

import com.tyrfing.tyrlib2.renderer.OpenGLRenderer;

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
        
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(new OpenGLRenderer());
        
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }
}
