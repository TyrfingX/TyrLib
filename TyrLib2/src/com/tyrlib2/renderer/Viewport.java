package com.tyrlib2.renderer;

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
		Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 12);
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
}
