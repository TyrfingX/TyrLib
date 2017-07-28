package com.tyrlib2.graphics.renderer;

import java.util.HashMap;
import java.util.Map;

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
	
	public void destroy() {
		instance = null;
	}
	
	/**
	 * Loads a shader
	 * @param name			Name of the shader
	 * @param type			Type of the shader (Fragment or Vertex Shader)
	 * @param shaderCode	Code of the shader
	 * @return				A handle to the shader
	 */
	
    public int loadShader(String name, int type, String shaderCode){

        // create a vertex shader type (TyrGL.GL_VERTEX_SHADER)
        // or a fragment shader type (TyrGL.GL_FRAGMENT_SHADER)
        int shader = TyrGL.glCreateShader(type);

        // add the source code to the shader and compile it
        TyrGL.glShaderSource(shader, shaderCode);
        TyrGL.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        TyrGL.glGetShaderiv(shader, TyrGL.GL_COMPILE_STATUS, compileStatus, 0);
     
        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0)
        {
        	
            String info = TyrGL.glGetShaderInfoLog(shader);
        	System.out.println(info);
        	
        	int error = TyrGL.glGetError();
        	System.out.println(error);
            
        	TyrGL.glDeleteShader(shader);
            shader = 0;
            
            if (type == TyrGL.GL_VERTEX_SHADER) {
            	throw new RuntimeException("Error creating vertex shader: " + name);
            } else {
            	throw new RuntimeException("Error creating fragment shader: " + name);
            }
        } else {
        	shaders.put(name, shader);
        }
        
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
