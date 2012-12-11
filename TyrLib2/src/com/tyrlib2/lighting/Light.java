package com.tyrlib2.lighting;

import android.opengl.Matrix;

import com.tyrlib2.scene.SceneObject;

/**
 * Represents a source of light
 * @author Sascha
 *
 */

public abstract class Light extends SceneObject {
	
	/** Holds the light position in model space **/
	protected final float[] modelSpaceVector =  { 0,0,0,1 };
	
	/** Holds the transformed position of the light in world space (model) **/
	protected final float[] worldSpaceVector = new float[4];
	
	/** Holds the transformed position of the light in eye space (model*view) **/
	protected final float[] eyeSpaceVector = new float[4];
	
	public enum Type {
		SPOT_LIGHT, POINT_LIGHT, DIRECTIONAL_LIGHT
	}

	/** The type of this light **/
	private Type type;
	
	public Light(Type type) {
		this.type = type;
	}
	
	/**
	 * Get the type of this light
	 * @return	The type of this light
	 */
	
	public Type getType() {
		return type;
	}
	
	public void updateEyeSpaceVector(float[] viewMatrix) {
		if (parent != null) {
			Matrix.multiplyMV(worldSpaceVector, 0, parent.getModelMatrix(), 0, modelSpaceVector, 0);
			Matrix.multiplyMV(eyeSpaceVector, 0, viewMatrix, 0, worldSpaceVector, 0);
		}
	}
	
	public float[] getEyeSpaceVector() {
		return eyeSpaceVector;
	}

}
