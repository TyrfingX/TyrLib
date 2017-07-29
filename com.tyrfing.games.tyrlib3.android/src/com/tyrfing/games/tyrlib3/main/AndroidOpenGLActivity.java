package com.tyrfing.games.tyrlib3.main;

import com.tyrfing.games.tyrlib3.main.OpenGLActivity;
import com.tyrfing.games.tyrlib3.model.math.AndroidMatrixImpl;
import com.tyrfing.games.tyrlib3.model.math.Matrix;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.GLES20Impl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/** 
 * Base activity class 
 * Sets renderer, touch, etc. up. Games customizing these elements should roll their own Activity class.
 * This setup uses OpenGL ES 2.0 and therefore only devices running an OS with >=2.2 are compatible.
 * @author Sascha
 *
 */

public abstract class AndroidOpenGLActivity extends Activity implements OpenGLActivity {
	private GLSurfaceView glView;
	public static boolean RUNNING = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (RUNNING) {
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

    	RUNNING = true;
    	AndroidMedia.CONTEXT = new AndroidMedia(this);
    	TyrGL.IMPL = new GLES20Impl();
    	Matrix.IMPL = new AndroidMatrixImpl();
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(0);
        
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        glView = new OpenGLSurfaceView(this);
        
        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
     
        if (supportsEs2)
        {
        	setContentView(glView);
        	this.go();
        }
        else
        {
            // TODO: OpenGL 1 renderer
            return;
        }

    }
    
    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        while(glView == null) {
        	Thread.yield();
        }
        
        glView.onResume();
    }
     
    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        glView.onPause();
    }
    
    public void close() {
		this.finish();
    }
}
