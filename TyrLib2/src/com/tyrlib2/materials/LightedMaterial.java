package com.tyrlib2.materials;

import android.opengl.GLES20;

import com.tyrlib2.lighting.Light;
import com.tyrlib2.lighting.Light.Type;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.scene.SceneManager;
import com.tyrlib2.util.Color;

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
	
	public void renderLight(int lightIndex) {
    	
		Light light = SceneManager.getInstance().getLight(lightIndex);

        Color ambient = new Color(0,0,0,0);
		
		if (lightIndex == 0) {
	        //Pass in the global scene illumination only for the first light
			ambient = SceneManager.getInstance().getAmbientLight();
		}
		
		GLES20.glUniform4f(ambientHandle, ambient.r, ambient.g, ambient.b, ambient.a);
		
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
