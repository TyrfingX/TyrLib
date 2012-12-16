package com.tyrlib2.lighting;

import java.nio.IntBuffer;

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
	
	/** Size of the shadow textures **/
	public static final int texW = 512;//480;
	public static final int texH = 512;//800;
	int[] fb, depthRb, renderTex;
	IntBuffer texBuffer;
	
	private float intensity = 2;
	
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
		lightDirection.normalize();
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
			Matrix.multiplyMV(lightDirectionVector, 0, viewMatrix, 0, lightDirectionVector, 0);
			
			Vector3 dir = new Vector3(lightDirectionVector[0], lightDirectionVector[1], lightDirectionVector[2]);
			dir.normalize();
			
			lightDirectionVector[0] = dir.x * intensity;
			lightDirectionVector[1] = dir.y * intensity;
			lightDirectionVector[2] = dir.z * intensity;
		}
		
	}
	
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}
