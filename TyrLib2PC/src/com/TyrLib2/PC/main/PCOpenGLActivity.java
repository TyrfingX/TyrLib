package com.TyrLib2.PC.main;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import com.tyrlib2.main.Media;
import com.tyrlib2.main.OpenGLActivity;
import com.tyrlib2.math.Matrix;


public abstract class PCOpenGLActivity implements OpenGLActivity {

	private PCOpenGLSurfaceView glView;
	
	public PCOpenGLActivity() {
		
        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL3));
		
        // We may at this point tweak the caps and request a translucent drawable
        caps.setBackgroundOpaque(false);
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);
        //caps.setAlphaBits(8);
        //caps.setRedBits(8);
        //caps.setBlueBits(8);
       // caps.setGreenBits(8);
        
		Media.CONTEXT = new PCMedia(this);
		Matrix.IMPL = new PCMatrixImpl();
	
		glView = new PCOpenGLSurfaceView("Test", caps);
		glView.setSize(1600, 900);
		glView.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		go();
		
		glView.startRendering();
		
		
	}
	
	public PCOpenGLSurfaceView getGLView() {
		return glView;
	}

}
