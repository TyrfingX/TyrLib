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
	
	public static ShaderManager getInstance() {
		if (instance == null) {
			instance = new ShaderManager();
		} 
		
		return instance;
	}
}
