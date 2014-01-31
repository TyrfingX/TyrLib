package com.tyrlib2.graphics.text.programs;

import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.text.AttribVariable;
import com.tyrlib2.graphics.text.Utilities;


public abstract class Program {
	
	private int programHandle;
	private int vertexShaderHandle;
	private int fragmentShaderHandle;
	private boolean mInitialized;
	
	public Program() {
		mInitialized = false;
	}
	
	public void init() {
		init(null, null, null);
	}
	
	public void init(String vertexShaderCode, String fragmentShaderCode, AttribVariable[] programVariables) {
		vertexShaderHandle = Utilities.loadShader(TyrGL.GL_VERTEX_SHADER, vertexShaderCode);
		fragmentShaderHandle = Utilities.loadShader(TyrGL.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		programHandle = Utilities.createProgram(
					vertexShaderHandle, fragmentShaderHandle, programVariables);
		
		mInitialized = true;
	}
	
	public int getHandle() {
		return programHandle;
	}
	
	public void delete() {
		TyrGL.glDeleteShader(vertexShaderHandle);
		TyrGL.glDeleteShader(fragmentShaderHandle);
		TyrGL.glDeleteProgram(programHandle);
		mInitialized = false;
	}
	
	public boolean initialized() {
		return mInitialized;
	}
}