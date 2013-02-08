package com.tyrlib2.graphics.materials;

import android.opengl.GLES20;

import com.tyrlib2.graphics.lighting.Light;
import com.tyrlib2.graphics.lighting.Light.Type;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.scene.SceneManager;
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
    	
		Light light = null;
		
		if (SceneManager.getInstance().getLightCount() > 0) {
			light = SceneManager.getInstance().getLight(lightIndex);
		}
		
        Color ambient = new Color(0,0,0,0);
		
		if (lightIndex == 0) {
	        //Pass in the global scene illumination only for the first light
			ambient = SceneManager.getInstance().getAmbientLight();
		}
		
		GLES20.glUniform4f(ambientHandle, ambient.r, ambient.g, ambient.b, ambient.a);
		
		if (light != null) {
		
	    	if (light.getType() == Type.POINT_LIGHT) {
	    	
	    		// Set the light type to point light
	    		GLES20.glUniform1f(lightTypeHandle, 1.0f);
	    		
	    	} else if (light.getType() == Type.DIRECTIONAL_LIGHT) {
	    		
	    		// Set the light type to directional light
	    		GLES20.glUniform1f(lightTypeHandle, 0.5f);
	    	}
	    	float[] lightPosInEyeSpace = light.getLightVector();
	    	
			// Pass in the light position in eye space.        
			GLES20.glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
		} else {
    		// Set the light type to no extra light
    		GLES20.glUniform1f(lightTypeHandle, 0.0f);			
		}
    	
		

	}
}
