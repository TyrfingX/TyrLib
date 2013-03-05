package com.tyrlib2.graphics.renderer;

import android.opengl.Matrix;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

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
	
	private Vector3 rotatedLookDirection;
	
	private Vector3 rotatedUp;
	
	public Camera(Vector3 up) {
		this.up = up;
		up.normalize();
	}
	
	/**
	 * Set a new direction in which to look
	 * @param lookDirection	The direction in which the camera will look
	 */
	public void setLookDirection(Vector3 lookDirection) {
		this.lookDirection = lookDirection;
		this.lookDirection.normalize();
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
		
		Vector3 pos = parent.getCachedAbsolutePos();
		Quaternion rot = parent.getCachedAbsoluteRot();
		
		rotatedLookDirection = rot.multiply(lookDirection);
		rotatedUp = rot.multiply(this.up);
		
		Matrix.setLookAtM	(viewMatrix, 
							0, 
							pos.x, pos.y, pos.z, 
							rotatedLookDirection.x + pos.x, rotatedLookDirection.y + pos.y, rotatedLookDirection.z + pos.z,
							rotatedUp.x, rotatedUp.y, rotatedUp.z);

	}
	
	/**
	 * Get the look diretion of the camera in world space
	 * @return
	 */
	
	public Vector3 getWorldLookDirection() {
		return rotatedLookDirection;
	}
	
	public Vector3 getWorldUpVector() {
		return rotatedUp;
	}
}
