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
	
	/** Dont know yet what this is **/
	private Vector3 up;
	
	/** Direction in which the camera looks **/
	private Vector3 lookDirection;
	
	public Camera(Vector3 up) {
		this.up = up;
	}
	
	/**
	 * Set a new direction in which to look
	 * @param lookDirection	The direction in which the camera will look
	 */
	public void setLookDirection(Vector3 lookDirection) {
		this.lookDirection = lookDirection;
	}
	
	/**
	 * Get the direction which the camera is facing
	 * @return	The direction which the camera is facing
	 */
	
	public Vector3 getLookDirection() {
		return lookDirection;
	}
	
	/**
	 * Use this camera
	 */
	public void use() {
		SceneManager.getInstance().getRenderer().setCamera(this);
	}
	
	/**
	 * Get the view matrix
	 * @return	The view matrix of the camera
	 */
	
	public float[] getViewMatrix() {
		return viewMatrix;
	}
	
	/**
	 * Update the view matrix if the parent node changed
	 */
	
	public void update() {
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, 10.5f, 0, 0, 0, up.x, up.y, up.z);
		//Matrix.multiplyMM(viewMatrix, 0, viewMatrix, 0, parent.getModelMatrix(), 0);
	}
}
