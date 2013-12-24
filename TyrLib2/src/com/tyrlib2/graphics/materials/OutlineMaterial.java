package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.util.Color;

/**
 * Material for 3D highlight of friends and enemies
 * @author Sascha
 *
 */

public class OutlineMaterial extends Material {
	
	/** Coloring **/
	private int colorHandle;
	private Color color;
	
	/** Per vertex normals of this object **/
	public static final int normalOffset = DefaultMaterial3.normalOffset;
	public static final int normalDataSize = DefaultMaterial3.normalDataSize;
	private int normalHandle;
	
	public static final int dataSize = DefaultMaterial3.dataSize;
	public static final int posOffset = DefaultMaterial3.posOffset;
	
	public static final String OUTLINE_PROGRAM = "OUTLINE";

	public OutlineMaterial(Color color) {
		program = ProgramManager.getInstance().getProgram(OUTLINE_PROGRAM);
		
		setup(color);
	}
	
	
	
	protected void setup(Color color) {
		this.color = color;
		
		init(dataSize,posOffset,3, "u_MVPMatrix", "a_Position");
		
		normalHandle = GLES20.glGetAttribLocation(program.handle, "a_Normal");
	    colorHandle = GLES20.glGetUniformLocation(program.handle, "u_Color");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
	    super.render(vertexBuffer, modelMatrix);
		
		if (program.meshChange) {
			passMesh(vertexBuffer);
		}
		
		Program.blendEnable(GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA);
	}
	
	private void passMesh(FloatBuffer vertexBuffer)
	{
		
		// Pass in the color information
		GLES20.glUniform4f(colorHandle, color.r, color.g, color.b, color.a);
	    
		
	    // Pass in the normal information
	    vertexBuffer.position(normalOffset);
	    GLES20.glVertexAttribPointer(normalHandle, normalDataSize, GLES20.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(normalHandle);
	}
	
	public Color getColors() {
		return color;
	}
	
	public int getNormalOffset() {
		return normalOffset;
	}
	
	public Material copy() {
		OutlineMaterial material = new OutlineMaterial(color);
		return material;
	}
	
}
