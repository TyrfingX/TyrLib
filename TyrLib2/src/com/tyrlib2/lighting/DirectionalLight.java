package com.tyrlib2.lighting;

import android.opengl.Matrix;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.scene.SceneNode;

/**
 * Represents a directional light. Emits light into the specified position.
 * The emitted light has an infinite range.
 * @author Sascha
 *
 */

public class DirectionalLight extends Light {
	
	/** Holds the total transformation by the parent nodes **/
	private float[] modelMatrix;
	
	/** Holds the direction in which this directional light emits **/
	private Vector3 lightDirection;
	
	/** Holds the direction in which this directional light emits
	 *  after transformation with the parent model matrix
	 */
	private float[] lightDirectionVector = { 0, 0, 0, 1 };
	
	/** The default light direction **/
	public static final Vector3 DEFAULT_LIGHT_DIRECTION = new Vector3(0,0,-1);
	
	public DirectionalLight() {
		super(Type.DIRECTIONAL_LIGHT);
		this.lightDirection = DEFAULT_LIGHT_DIRECTION;
	}
	
	public DirectionalLight(Vector3 lightDirection) {
		super(Type.DIRECTIONAL_LIGHT);
		this.lightDirection = lightDirection;
	}


	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		modelMatrix = node.getModelMatrix();
	}
	
	
	/**
	 * Get the direction of this light
	 * @return	The direction into which the light is emitted
	 */
	public Vector3 getLightDirection() {
		return lightDirection;
	}
	
	/**
	 * Set a new light direction
	 * @param lightDirection	The new light direction
	 */
	public void setLightDirection(Vector3 lightDirection) {
		this.lightDirection = lightDirection;
	}
	
	/**
	 * Get the total transformation matrix from the parents
	 * of this directional light
	 * @return	The model transformation matrix
	 */
	
	public float[] getModelMatrix() {
		return modelMatrix;
	}
	
	public float[] getLightVector() {
		
		return lightDirectionVector;
	}

	@Override
	public void update(float[] viewMatrix) {
		if (parent != null) {
			lightDirectionVector[0] = lightDirection.x;
			lightDirectionVector[1] = lightDirection.y;
			lightDirectionVector[2] = lightDirection.z;
		
			Matrix.multiplyMV(lightDirectionVector, 0, modelMatrix, 0, lightDirectionVector, 0);
		}
	}
	
}
