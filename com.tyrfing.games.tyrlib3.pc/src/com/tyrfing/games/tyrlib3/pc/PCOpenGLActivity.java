package com.tyrfing.games.tyrlib3.pc;

import java.net.URL;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import com.tyrfing.games.tyrlib3.Media;
import com.tyrfing.games.tyrlib3.OpenGLActivity;
import com.tyrfing.games.tyrlib3.model.math.Matrix;
import com.tyrfing.games.tyrlib3.pc.model.config.Config;
import com.tyrfing.games.tyrlib3.pc.model.math.PCMatrixImpl;
import com.tyrfing.games.tyrlib3.pc.util.Logger;
import com.tyrfing.games.tyrlib3.pc.view.graphics.renderer.ConsoleView;


public abstract class PCOpenGLActivity implements OpenGLActivity {

	private PCOpenGLSurfaceView glView;
	protected boolean consoleMode;
	protected ConsoleView consoleView;
	
	public PCOpenGLActivity(JFrame frame, boolean consoleMode, Config config, String name) {
		
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
	        
	        glView = new PCOpenGLSurfaceView(this, frame, config, name, caps);

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
	
	public PCOpenGLActivity(JFrame frame) {
		this(frame, false, null, "Unnamed");
	}
	
	public PCOpenGLActivity(JFrame frame, Config config, String name) {
		this(frame, false, config, name);
	}

	public PCOpenGLSurfaceView getGLView() {
		return glView;
	}

}
