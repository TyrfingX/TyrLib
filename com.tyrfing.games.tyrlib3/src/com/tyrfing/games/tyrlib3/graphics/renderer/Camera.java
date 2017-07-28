package com.tyrfing.games.tyrlib3.graphics.renderer;

import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneObject;
import com.tyrfing.games.tyrlib3.math.Matrix;
import com.tyrfing.games.tyrlib3.math.Quaternion;
import com.tyrfing.games.tyrlib3.math.Vector3F;

/**
 * Transforms World space to eye space
 * @author Sascha
 *
 */

public class Camera extends SceneObject {
	/** Store the view matrix for the transformation **/
	protected float[] viewMatrix = new float[16];
	
	/** Dont know yet what this is **/
	private Vector3F up;
	
	/** Direction in which the camera looks **/
	private Vector3F lookDirection;
	
	private Vector3F rotatedLookDirection;
	
	private Vector3F rotatedUp;
	
	public Camera(Vector3F up) {
		this.up = up;
		up.normalize();
	}
	
	/**
	 * Set a new direction in which to look
	 * @param lookDirection	The direction in which the camera will look
	 */
	public void setLookDirection(Vector3F lookDirection) {
		this.lookDirection = lookDirection;
		this.lookDirection.normalize();
		
		Vector3F right = lookDirection.cross(up);
		up = right.cross(lookDirection);
		up.normalize();
	}
	
	/**
	 * Get the direction which the camera is facing
	 * @return	The direction which the camera is facing
	 */
	
	public Vector3F getLookDirection() {
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
		
		Vector3F pos = parent.getCachedAbsolutePosVector();
		Quaternion rot = parent.getCachedAbsoluteRot();
		
		if (pos == null) {
			pos = new Vector3F();
		}
		
		if (rot != null) {
			rotatedLookDirection = rot.multiply(lookDirection);
			rotatedUp = rot.multiply(this.up);
		} else {
			rotatedLookDirection = new Vector3F(lookDirection);
			rotatedUp = new Vector3F(up);
		}
		
		setLookAt(viewMatrix, pos);

	}
	
	public void setLookAt(float[] matrix, Vector3F pos) {
		Matrix.setLookAtM	(	viewMatrix, 
								0, 
								pos.x, pos.y, pos.z, 
								rotatedLookDirection.x + pos.x, rotatedLookDirection.y + pos.y, rotatedLookDirection.z + pos.z,
								rotatedUp.x, rotatedUp.y, rotatedUp.z);
	}
	
	/**
	 * Get the look diretion of the camera in world space
	 * @return
	 */
	
	public Vector3F getWorldLookDirection() {
		return rotatedLookDirection;
	}
	
	public Vector3F getWorldUpVector() {
		return rotatedUp;
	}
}
