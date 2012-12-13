package com.tyrlib2.materials;

import android.opengl.GLES20;

import com.tyrlib2.lighting.Light;
import com.tyrlib2.lighting.Light.Type;
import com.tyrlib2.renderer.Material;

/**
 * Basic abstract class for material which work with light
 * @author Sascha
 *
 */

public abstract class LightedMaterial extends Material{
	
	/** Lighting information **/
	protected int lightPosHandle;
	protected int mvMatrixHandle;
	protected int ambientHandle;
	protected int lightTypeHandle;
	
	public LightedMaterial() {
		lighted = true;
	}
	
	public void renderLight(Light light) {
    	
    	if (light.getType() == Type.POINT_LIGHT) {
    	
    		// Set the light type to point light
    		GLES20.glUniform1f(lightTypeHandle, 0.0f);
    		
    	} else if (light.getType() == Type.DIRECTIONAL_LIGHT) {
    		
    		// Set the light type to directional light
    		GLES20.glUniform1f(lightTypeHandle, 1.0f);
    	}
    	
		float[] lightPosInEyeSpace = light.getLightVector();
    	
		// Pass in the light position in eye space.        
		GLES20.glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);

	}
}
