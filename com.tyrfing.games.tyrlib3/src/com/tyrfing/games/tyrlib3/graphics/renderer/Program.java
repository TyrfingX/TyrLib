package com.tyrfing.games.tyrlib3.graphics.renderer;


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
	
	protected static Program inUse;
	
	/** The currently passed mesh **/
	public Mesh mesh;
	
	/** Mesh must be changed **/
	public boolean meshChange;
	
	/** The handle to the texture currently passed to the GPU **/
	public int textureHandle;
	
	private static boolean blending = false;
	private static int ssfactor;
	private static int sdfactor;
	
	public Program(int handle) {
		this.handle = handle;
	}
	
	/**
	 * Use this program for rendering
	 */
	public void use() {
		if (inUse != this) {
			
			if (inUse != null) {
				inUse.mesh = null;
				inUse.textureHandle = 0;
			}
			
			TyrGL.glUseProgram(handle);
			inUse = this;
			
			mesh = null;
			textureHandle = 0;
		}
	}
	
	/**
	 * Link the program and create an OpenGL ES program executable
	 */
	
	public void link() {
		TyrGL.glLinkProgram(handle);  
	}
	
	public static void resetCache() {
		if (inUse != null) {
			inUse.mesh = null;
			inUse.textureHandle = 0;
			inUse = null;
		}
	}
	
	public static void blendEnable(int sfactor, int dfactor) {
		if (!blending) {
			TyrGL.glEnable( TyrGL.GL_BLEND );
			blending = true;
		}
		if (sfactor != ssfactor || dfactor != sdfactor) {
			TyrGL.glBlendFunc( sfactor, dfactor );
			ssfactor = sfactor;
			sdfactor = dfactor;
		}
    	
	}
	
	public static void blendDisable() {
		if (blending) {
			blending = false;
			TyrGL.glDisable(TyrGL.GL_BLEND);
		}
	}
}
