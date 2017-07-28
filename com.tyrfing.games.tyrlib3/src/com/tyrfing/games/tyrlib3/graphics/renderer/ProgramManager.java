package com.tyrfing.games.tyrlib3.graphics.renderer;

import java.util.HashMap;
import java.util.Map;

import com.tyrfing.games.tyrlib3.files.FileReader;

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

        int programHandle = TyrGL.glCreateProgram();             // create empty OpenGL ES Program
        Program program = new Program(programHandle);
        TyrGL.glAttachShader(programHandle, vertexShader);   	// add the vertex shader to program
        TyrGL.glAttachShader(programHandle, fragmentShader); 	// add the fragment shader to program
        
        if (bindAttributes != null) {
        	for (int i = 0; i < bindAttributes.length; ++i) {
        		TyrGL.glBindAttribLocation(programHandle, i, bindAttributes[i]);
        	}
        }
        
        program.link();               	// creates OpenGL ES program executables
		
        
        // Get the link status.
        final int[] linkStatus = new int[1];
        TyrGL.glGetProgramiv(programHandle, TyrGL.GL_LINK_STATUS, linkStatus, 0);
     
        // If the link failed, delete the program.
        if (linkStatus[0] == 0)
        {
        	
            String info = TyrGL.glGetProgramInfoLog(programHandle);
        	
        	TyrGL.glDeleteProgram(programHandle);
            programHandle = 0;
            throw new RuntimeException("Error creating program: " + programName + ".\n" + info);
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
	
	public Program createProgram(String programName, int vertexShaderResId, int fragmentShaderResId, String[] bindAttributes) {
		return createProgram(programName, vertexShaderResId, fragmentShaderResId, bindAttributes, null);
	}
	
	public Program createProgram(String programName, int vertexShaderResId, int fragmentShaderResId, String[] bindAttributes, PreprocessorOptions options) {
		String vertexShader = preprocessVertexShader(FileReader.readRawFile(vertexShaderResId),options);
		String fragmentShader =  preprocessFragmentShader(FileReader.readRawFile(fragmentShaderResId),options);
		ShaderManager.getInstance().loadShader(programName + "_VS", TyrGL.GL_VERTEX_SHADER, vertexShader);
		ShaderManager.getInstance().loadShader(programName + "_FS", TyrGL.GL_FRAGMENT_SHADER, fragmentShader);
		Program program = ProgramManager.getInstance()
										.createProgram(programName, programName + "_VS", 
													   programName + "_FS", bindAttributes);
        program.vertexShader = vertexShader;
		program.fragmentShader = fragmentShader;
		program.bindAttributes = bindAttributes;
		return program;
	}
	
	public static String preprocessVertexShader(String vertexShader) {
		return preprocessVertexShader(vertexShader, null);
	}
	
	public static String preprocessFragmentShader(String fragmentShader) {
		return preprocessFragmentShader(fragmentShader, null);
	}
	
	public static String preprocessVertexShader(String vertexShader, PreprocessorOptions options) {
		if (TyrGL.TARGET == TyrGL.PC_TARGET) {
			vertexShader = "#version 150\n" + vertexShader;
			vertexShader = vertexShader.replaceAll("attribute", "in");
			vertexShader = vertexShader.replaceAll("varying", "out");
		}
		
		return preprocessShader(vertexShader, options);
	}
	
	public static String preprocessFragmentShader(String fragmentShader, PreprocessorOptions options) {
		if (TyrGL.TARGET == TyrGL.PC_TARGET) {
			fragmentShader = "#version 150\nout vec4 colorOut;\n" + fragmentShader;
			fragmentShader = fragmentShader.replaceAll("varying", "in");
			fragmentShader = fragmentShader.replaceAll("gl_FragColor", "colorOut");
		}
		return preprocessShader(fragmentShader, options);
	}
	
	public static String preprocessShader(String shader, PreprocessorOptions options) {
		
		StringBuilder builder = new StringBuilder();
		StringBuilder tmp = new StringBuilder();
		int length = shader.length();
		int pos = 0;
		
		while (pos != length) {
			if (shader.charAt(pos) == '#') {
				while (shader.charAt(pos) != ' ') {
					tmp.append(shader.charAt(pos));
					pos++;
				}
				
				String directive = tmp.toString();
				tmp.setLength(0);
				
				pos++;
				
				if (directive.equals("#if")) {
					while (shader.charAt(pos) != ' ') {
						tmp.append(shader.charAt(pos));
						pos++;
					}
					
					String option = tmp.toString();
					tmp.setLength(0);
					
					boolean optionDefined = options != null && options.isDefined(option);
					
					while (shader.charAt(pos) != '#') {
						if (optionDefined) {
							builder.append(shader.charAt(pos));
						}
						pos++;
					}
					
					while (shader.charAt(pos) != ' ') {
						tmp.append(shader.charAt(pos));
						pos++;
					}
					
					directive = tmp.toString();
					tmp.setLength(0);
					
					pos++;
					
					if (!directive.equals("#endif")) {
						throw new RuntimeException("Preprocessor failed!");
					}
				} else if (directive.equals("#ifndef")){
					while (shader.charAt(pos) != ' ') {
						tmp.append(shader.charAt(pos));
						pos++;
					}
					
					String option = tmp.toString();
					tmp.setLength(0);
					
					boolean optionDefined = options != null && !options.isDefined(option);
					
					while (shader.charAt(pos) != '#') {
						if (optionDefined) {
							builder.append(shader.charAt(pos));
						}
						pos++;
					}
					
					while (shader.charAt(pos) != ' ') {
						tmp.append(shader.charAt(pos));
						pos++;
					}
					
					directive = tmp.toString();
					tmp.setLength(0);
					
					pos++;
					
					if (!directive.equals("#endif")) {
						throw new RuntimeException("Preprocessor failed!");
					}
				} else {
					builder.append(directive);
					builder.append(' ');
				}
			} else {
				builder.append(shader.charAt(pos));
				pos++;
			}
		}
		
		return builder.toString();
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
			
			int vertexShader = ShaderManager.getInstance().loadShader(programName + "_VS", TyrGL.GL_VERTEX_SHADER, program.vertexShader);
			int fragmentShader = ShaderManager.getInstance().loadShader(programName + "_FS", TyrGL.GL_FRAGMENT_SHADER, program.fragmentShader);
			
	        int programHandle = TyrGL.glCreateProgram();             // create empty OpenGL ES Program
	        program.handle = programHandle;
	        TyrGL.glAttachShader(programHandle, vertexShader);   	// add the vertex shader to program
	        TyrGL.glAttachShader(programHandle, fragmentShader); 	// add the fragment shader to program
	        
	        if (program.bindAttributes != null) {
	        	for (int i = 0; i < program.bindAttributes.length; ++i) {
	        		TyrGL.glBindAttribLocation(programHandle, i, program.bindAttributes[i]);
	        	}
	        }
	        
	        program.link();               	// creates OpenGL ES program executables
			
	        
	        // Get the link status.
	        final int[] linkStatus = new int[1];
	        TyrGL.glGetProgramiv(programHandle, TyrGL.GL_LINK_STATUS, linkStatus, 0);
	     
	        // If the link failed, delete the program.
	        if (linkStatus[0] == 0)
	        {
	        	TyrGL.glDeleteProgram(programHandle);
	            programHandle = 0;
	            throw new RuntimeException("Error creating program: " + programName + ".");
	        }
			
		}
	}
	
	public boolean isProgramLoaded(String program) {
		return programs.containsKey(program);
	}
}
