package com.tyrfing.games.tyrlib3.graphics.materials;

import com.tyrfing.games.tyrlib3.graphics.lighting.Light;
import com.tyrfing.games.tyrlib3.graphics.renderer.Material;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.util.Color;

/**
 * Basic abstract class for material which work with light
 * @author Sascha
 *
 */

public abstract class LightedMaterial extends Material{
	
	/** Lighting information **/
	protected int lightPosHandle;
	protected int normalMatrixHandle;
	protected int ambientHandle;
	protected Color color = Color.BLACK.copy();
	
	private static final Color DEFAULT_AMBIENT = new Color(0,0,0,0);
	
	public LightedMaterial() {
		lighted = true;
	}
	
	public void renderLight(int lightIndex) {
    	
		Light light = null;
		
		SceneManager sceneManager = SceneManager.getInstance();
		
		if (sceneManager.getLightCount() > 0) {
			light = sceneManager.getLight(lightIndex);
		}
		
        Color ambient = DEFAULT_AMBIENT;
		
		if (lightIndex == 0) {
	        //Pass in the global scene illumination only for the first light
			ambient = sceneManager.getAmbientLight();
		}
		
		TyrGL.glUniform4f(ambientHandle,ambient.r+color.r, ambient.g+color.g, ambient.b+color.b, 1);
		
		if (light != null) {
			
			lightPosHandle = TyrGL.glGetUniformLocation(program.handle, "u_LightPos");
		
			/*
	    	if (light.getType() == Type.POINT_LIGHT) {
	    	
	    		// Set the light type to point light
	    		TyrGL.glUniform1f(lightTypeHandle, 1.0f);
	    		
	    	} else if (light.getType() == Type.DIRECTIONAL_LIGHT) {
	    		
	    		// Set the light type to directional light
	    		TyrGL.glUniform1f(lightTypeHandle, 0.5f);
	    	}*/
			
	    	float[] lightPosInEyeSpace = light.getLightVector();
	    	
			// Pass in the light position in eye space.        
	    	TyrGL.glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
		} /* else {
    		// Set the light type to no extra light
			TyrGL.glUniform1f(lightTypeHandle, 0.0f);		
		} */
    
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setLighted(boolean lighted) {
		this.lighted = lighted;
	}
}
