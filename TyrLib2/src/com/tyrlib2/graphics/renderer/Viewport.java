package com.tyrlib2.graphics.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * This transforms a 3D scene into a 2D scene
 * @author Sascha
 *
 */

public class Viewport {
	
	/** Matrix for projection **/
	protected float[] projectionMatrix = new float[16];
	private int width;
	private int height;
	private float ratio;
	
	private int nearClip = 3;
	private int farClip = 6000;
	
	/**
	 * Creates a blank view port
	 */
	public Viewport() {
		
	}
	
	/**
	 * Creates a full screen view port
	 * @param width		The width of the screen
	 * @param height	The height of the screen
	 */
	public Viewport(int width, int height) {
	    // Set the OpenGL viewport
	    GLES20.glViewport(0, 0, width, height);
	    
	    // Create a full screen view port
	    setFullscreen(width, height);
	}
	
	/** 
	 * Update the frustum
	 */
	
	public void update() {
		setFullscreen(width, height);
	}
	
	public void setFullscreen(int width, int height) {
		this.width = width;
		this.height = height;
		ratio = (float) width / height;
		Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, nearClip, farClip);
	}
	
	public float getNearClipWidth() {
		return ratio * 2;
	}
	
	public float getNearClipHeight() {
		return 2;
	}
	
	public float[] getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getNearClip() {
		return nearClip;
	}
	
	public int getFarClip() {
		return farClip;
	}
	
	public int getMinExtends() {
		return Math.min(width, height);
	}
	
	public float getRatio() {
		return ratio;
	}
}
