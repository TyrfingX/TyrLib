package com.tyrfing.tyrlib2.main;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/** 
 * Base activity class 
 * Sets renderer, touch, etc. up. Games customizing these elements should roll their own Activity class.
 * This setup uses OpenGL ES 2.0 and therefore only devices running an OS with >=2.2 are compatible.
 * @author Sascha
 *
 */

public class OpenGLActivity extends Activity {
	private GLSurfaceView glView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        glView = new OpenGLSurfaceView(this);
        setContentView(glView);
    }
}
