package com.tyrlib2.renderer;

import java.util.HashMap;
import java.util.Map;

import android.opengl.GLES20;

/** 
 * Takes care of loading vertex and fragment shaders and cashing them
 * @author Sascha
 *
 */

public class ShaderManager {
	private static ShaderManager instance;
	private Map<String, Integer> shaders;
	
	public ShaderManager() {
		shaders = new HashMap<String, Integer>();
	}
	
	/**
	 * Get an instance of the ShaderManager. If it does not exist yet, it will be created.
	 * @return An instance of the ShaderManager.
	 */
	
	public static ShaderManager getInstance() {
		if (instance == null) {
			instance = new ShaderManager();
		} 
		
		return instance;
	}
	
	/**
	 * Loads a shader
	 * @param name
	 * @param type
	 * @param shaderCode
	 * @return
	 */
	
    public int loadShader(String name, int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        shaders.put(name, shader);
        
        return shader;
    }
    
    /**
     * Gets a shader
     * @param name	Name of the shader
     * @return		A handle to the shader
     */
    
    public int getShader(String name) {
    	return shaders.get(name);
    }
}
