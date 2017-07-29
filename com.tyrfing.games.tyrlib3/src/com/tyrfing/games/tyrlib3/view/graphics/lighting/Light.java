package com.tyrfing.games.tyrlib3.view.graphics.lighting;

import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneObject;

/**
 * Represents a source of light
 * @author Sascha
 *
 */

public abstract class Light extends SceneObject {
	
	public enum Type {
		POINT_LIGHT, DIRECTIONAL_LIGHT
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
	
	/**
	 * Get a vector characteristic for this light
	 * @return	A vector characteristic for this light
	 */
	
	public abstract float[] getLightVector();
	
	/**
	 * Update the characteristic vector of this light
	 * @param viewMatrix	The view Matrix
	 */
	public abstract void update(float[] viewMatrix);



}
