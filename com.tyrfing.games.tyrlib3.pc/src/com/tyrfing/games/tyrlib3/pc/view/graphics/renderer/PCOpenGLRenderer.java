package com.tyrfing.games.tyrlib3.pc.view.graphics.renderer;


import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.tyrfing.games.tyrlib3.OpenGLActivity;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;



public class PCOpenGLRenderer extends OpenGLRenderer implements GLEventListener {

	//private int[] fbo = new int[1];
	//private int[] rbo = new int[2];
	
	private OpenGLActivity activity;
	
	public PCOpenGLRenderer(OpenGLActivity activity) {
		super();
		this.activity = activity;
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		System.out.println("Initialized drawable window for PCOpenGLRenderer");
		try {
			TyrGL.IMPL = new PCGL3(drawable.getGL().getGL3());
			
			defaultSetup();
			activity.loadShaders();
			
			drawable.getGL().glEnable(GL3.GL_VERTEX_PROGRAM_POINT_SIZE);
			
			startRendering();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Fatal error encountered!");
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		System.out.println("Disposing of OpenGL Context");
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		render();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		surfaceChanged(width, height);
	}

}
