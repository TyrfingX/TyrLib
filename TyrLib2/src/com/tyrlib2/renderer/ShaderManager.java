package com.tyrlib2.renderer;

/** 
 * Takes care of loading vertex and fragment programs and compiling them
 * and cashing the compiled shader.
 * @author Sascha
 *
 */

public class ShaderManager {
	private static ShaderManager instance;
	
	public ShaderManager() {
		
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
}
