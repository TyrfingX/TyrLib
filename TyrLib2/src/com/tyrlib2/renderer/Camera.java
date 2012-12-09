package com.tyrlib2.renderer;

import android.opengl.Matrix;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.scene.SceneManager;
import com.tyrlib2.scene.SceneObject;

/**
 * Transforms World space to eye space
 * @author Sascha
 *
 */

public class Camera extends SceneObject {
	/** Store the view matrix for the transformation **/
	protected float[] viewMatrix = new float[16];
	
	/** This is where we are looking **/
	private Vector3 lookAt;
	
	/** Dont know yet what this is **/
	private Vector3 up;
	
	public Camera(Vector3 up) {
		this.up = up;
	}
	
	/**
	 * Set a new direction in which to look
	 * @param lookAt
	 */
	public void lookAt(Vector3 lookAt) {
		this.lookAt = lookAt;
		lookAt();
	}
	
	/** 
	 * Update the view matrix
	 */
	
	private void lookAt() {
		Vector3 pos = parent.getAbsolutePos();
		Matrix.setLookAtM(viewMatrix, 0, pos.x, pos.y, pos.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
	}
	
	/**
	 * Use this camera
	 */
	public void use() {
		SceneManager.getInstance().getRenderer().setCamera(this);
	}
	
	public float[] getViewMatrix() {
		return viewMatrix;
	}
}
