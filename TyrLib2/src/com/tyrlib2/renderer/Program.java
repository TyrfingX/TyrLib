package com.tyrlib2.renderer;

import android.opengl.GLES20;

/** 
 * Represents a program
 * @author Sascha
 *
 */

public class Program {
	public int handle;
	
	protected String vertexShader;
	protected String fragmentShader;
	protected String[] bindAttributes;
	
	private static Program inUse;
	
	public Program(int handle) {
		this.handle = handle;
	}
	
	/**
	 * Use this program for rendering
	 */
	public void use() {
		if (inUse != this) {
			GLES20.glUseProgram(handle);
			inUse = this;
		}
	}
	
	/**
	 * Link the program and create an OpenGL ES program executable
	 */
	
	public void link() {
		GLES20.glLinkProgram(handle);  
	}
}
