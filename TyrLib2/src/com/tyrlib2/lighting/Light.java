package com.tyrlib2.lighting;

import com.tyrlib2.scene.SceneObject;

/**
 * Represents a source of light
 * @author Sascha
 *
 */

public abstract class Light extends SceneObject {
	
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

}
