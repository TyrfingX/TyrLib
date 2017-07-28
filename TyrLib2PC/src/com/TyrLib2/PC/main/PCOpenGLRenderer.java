package com.TyrLib2.PC.main;


import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.main.OpenGLActivity;



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
		
		GL3 gl = drawable.getGL().getGL3();
		/*
		gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, fbo[0] );
		gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_RENDERBUFFER, rbo[0]);
		gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_DEPTH_ATTACHMENT, GL3.GL_RENDERBUFFER, rbo[1]);
		*/
		
		render();
		/*
		gl.glBindFramebuffer(GL3.GL_READ_FRAMEBUFFER, fbo[0]); // Unseren Framebuffer also Quelle binden
		gl.glBindFramebuffer(GL3.GL_DRAW_FRAMEBUFFER, 0); // Den OpenGL Framebuffer als Ziel binden
		gl.glBlitFramebuffer(0, 0, drawable.getWidth(), drawable.getHeight(), 0, 0, 
							 drawable.getWidth(), drawable.getHeight(), GL3.GL_COLOR_BUFFER_BIT, GL3.GL_NEAREST);
		*/
		

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		surfaceChanged(width, height);
	}

}
