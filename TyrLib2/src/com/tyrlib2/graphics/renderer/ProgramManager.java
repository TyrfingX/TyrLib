package com.tyrlib2.graphics.renderer;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.opengl.GLES20;

import com.tyrlib2.files.FileReader;

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
	
	public void destroy() {
		instance = null;
	}
	
	/**
	 * Creates a new OpenGL program using a vertex and a fragment shader
	 * @param programName			Name of the new program
	 * @param vertexShaderName		Name of the vertex shader
	 * @param fragmentShaderName	Name of the fragment shader
	 * @param bindAttributes		Binds the attributes of vertex and fragment shader
	 * @return						An object representing the program
	 */
	
	protected Program createProgram(String programName, String vertexShaderName, String fragmentShaderName, String[] bindAttributes) {
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
	 * Creates a new program. The passed ressources will be loaded an receive the name
	 * PROGRAMNAME_VS and PROGRAMNAME_FS respectively.
	 * @param programName			The name of the program. Vertexshader and fragmentshader 
	 * 								will receive the suffix _vs and _fs respectively.
	 * @param context				The context from where the ressources can be loaded
	 * @param vertexShaderResId		The id of the ressource containing the vertex shader
	 * @param fragmenShaderResId	The id of the ressource contaiing the fragment shader
	 * @return						The compiled program
	 */
	
	public Program createProgram(String programName, Context context, int vertexShaderResId, int fragmentShaderResId, String[] bindAttributes) {
		String vertexShader = FileReader.readRawFile(context, vertexShaderResId);
		String fragmentShader = FileReader.readRawFile(context, fragmentShaderResId);
		ShaderManager.getInstance().loadShader(programName + "_VS", GLES20.GL_VERTEX_SHADER, vertexShader);
		ShaderManager.getInstance().loadShader(programName + "_FS", GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		Program program = ProgramManager.getInstance()
										.createProgram(programName, programName + "_VS", 
													   programName + "_FS", bindAttributes);
        program.vertexShader = vertexShader;
		program.fragmentShader = fragmentShader;
		program.bindAttributes = bindAttributes;
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
	
	/**
	 * Recreate all current programs
	 */
	
	public void recreateAll() {
		Program.inUse = null;
		
		for (String programName : programs.keySet()) {
			
			Program program = programs.get(programName);
			
			int vertexShader = ShaderManager.getInstance().loadShader(programName + "_VS", GLES20.GL_VERTEX_SHADER, program.vertexShader);
			int fragmentShader = ShaderManager.getInstance().loadShader(programName + "_FS", GLES20.GL_FRAGMENT_SHADER, program.fragmentShader);
			
	        int programHandle = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	        program.handle = programHandle;
	        GLES20.glAttachShader(programHandle, vertexShader);   	// add the vertex shader to program
	        GLES20.glAttachShader(programHandle, fragmentShader); 	// add the fragment shader to program
	        
	        if (program.bindAttributes != null) {
	        	for (int i = 0; i < program.bindAttributes.length; ++i) {
	        		GLES20.glBindAttribLocation(programHandle, i, program.bindAttributes[i]);
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
			
		}
	}
	
	public boolean isProgramLoaded(String program) {
		return programs.containsKey(program);
	}
}
