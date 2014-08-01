package com.TyrLib2.PC.main;

import java.net.URL;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import com.tyrlib2.main.Media;
import com.tyrlib2.main.OpenGLActivity;
import com.tyrlib2.math.Matrix;


public abstract class PCOpenGLActivity implements OpenGLActivity {

	private PCOpenGLSurfaceView glView;
	protected boolean consoleMode;
	protected ConsoleView consoleView;
	
	public PCOpenGLActivity(boolean consoleMode, Config config) {
		
		URL url = getClass().getResource("/");
		Logger.init(url.getPath() + "log.txt");
		
		Matrix.IMPL = new PCMatrixImpl();
		Media.CONTEXT = new PCMedia(this);
		
		if (!consoleMode) {
		
	        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL3));
			
	        // We may at this point tweak the caps and request a translucent drawable
	        caps.setBackgroundOpaque(false);
	        caps.setSampleBuffers(true);
	        caps.setNumSamples(4);
	        caps.setAlphaBits(8);
	        caps.setRedBits(8);
	        caps.setBlueBits(8);
	        caps.setGreenBits(8);
	        
	        glView = new PCOpenGLSurfaceView(config, "Test", caps);

		} else {
			consoleView = new ConsoleView();
		}
		
		go();
		
		if (!consoleMode) {
			glView.startRendering();
		} else {
			consoleView.start();
		}
	}
	
	public PCOpenGLActivity() {
		this(false, null);
	}
	
	public PCOpenGLActivity(Config config) {
		this(false, config);
	}

	public PCOpenGLSurfaceView getGLView() {
		return glView;
	}

}
