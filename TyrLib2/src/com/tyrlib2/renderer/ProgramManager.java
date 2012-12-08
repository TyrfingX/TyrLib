package com.tyrlib2.renderer;

import java.util.HashMap;
import java.util.Map;

import android.opengl.GLES20;

/**
 * This class takes care of linking and compiling programs and cashing the result
 * @author Sascha
 *
 */

public class ProgramManager {
	
	/** Singleton instance **/
	private static ProgramManager instance;

	/** Compiled programs **/
	private Map<String, Program> programs;
	
	public ProgramManager() {
		programs = new HashMap<String, Program>();
	}
	
	/**
	 * Gets an instance to this singleton object
	 * @return Instance to this singleton object
	 */
	public static ProgramManager getInstance() {
		if (instance == null) {
			instance = new ProgramManager();
		} 
		
		return instance;
	}
	
	/**
	 * Creates a new OpenGL program using a vertex and a fragment shader
	 * @param programName			Name of the new program
	 * @param vertexShaderName		Name of the vertex shader
	 * @param fragmentShaderName	Name of the fragment shader
	 * @param bindAttributes		Binds the attributes of vertex and fragment shader
	 * @return						An object representing the program
	 */
	
	public Program createProgram(String programName, String vertexShaderName, String fragmentShaderName, String[] bindAttributes) {
        int vertexShader = ShaderManager.getInstance().getShader(vertexShaderName);
        int fragmentShader = ShaderManager.getInstance().getShader(fragmentShaderName);

        int programHandle = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        Program program = new Program(programHandle);
        GLES20.glAttachShader(programHandle, vertexShader);   	// add the vertex shader to program
        GLES20.glAttachShader(programHandle, fragmentShader); 	// add the fragment shader to program
        
        if (bindAttributes != null) {
        	for (int i = 0; i < bindAttributes.length; ++i) {
        		GLES20.glBindAttribLocation(programHandle, i, bindAttributes[i]);
        	}
        }
        
        program.link();               	// creates OpenGL ES program executables
		
        
        // Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
     
        // If the link failed, delete the program.
        if (linkStatus[0] == 0)
        {
            GLES20.glDeleteProgram(programHandle);
            programHandle = 0;
            throw new RuntimeException("Error creating program: " + programName + ".");
        }
        
        programs.put(programName, program);
		return program;
	}
	
	/**
	 * Gets a compiled OpenGL program
	 * @param programName	Name of the program
	 * @return				A handle to the compiled program
	 */
	
	public Program getProgram(String programName) {
		return programs.get(programName);
	}
}
