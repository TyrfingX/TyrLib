package com.tyrfing.games.tyrlib3.view.graphics.lighting;

import java.nio.IntBuffer;

import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.math.Matrix;
import com.tyrfing.games.tyrlib3.model.math.Quaternion;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;

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
	private Vector3F lightDirection;
	
	/** Holds the direction in which this directional light emits
	 *  after transformation with the parent model matrix
	 */
	private float[] lightDirectionVector = { 0, 0, 0, 1 };
	
	/** The default light direction **/
	public static final Vector3F DEFAULT_LIGHT_DIRECTION = new Vector3F(0,0,-1);
	
	private float[] rotMatrix = new float[16];
	
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
	
	public DirectionalLight(Vector3F lightDirection) {
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
	public Vector3F getLightDirection() {
		return lightDirection;
	}
	
	/**
	 * Set a new light direction
	 * @param lightDirection	The new light direction
	 */
	public void setLightDirection(Vector3F lightDirection) {
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
			lightDirection.normalize();
			lightDirection = lightDirection.multiply(intensity);
			lightDirectionVector[0] = -lightDirection.x;
			lightDirectionVector[1] = -lightDirection.y;
			lightDirectionVector[2] = -lightDirection.z;
			
			Quaternion rot = parent.getCachedAbsoluteRot();
			
			rot.toMatrix(rotMatrix);
			Matrix.multiplyMV(lightDirectionVector, 0, rotMatrix, 0, lightDirectionVector, 0);
		}
		
	}
	
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}
