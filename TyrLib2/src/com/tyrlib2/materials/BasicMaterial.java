package com.tyrlib2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.OpenGLRenderer;
import com.tyrlib2.renderer.ProgramManager;

/**
 * A material for simple per vertex colouring
 * @author Sascha
 *
 */

public class BasicMaterial extends Material {

	private int colorOffset = 3;
	private int colorDataSize = 4;
	private int colorHandle;
	private Color color;

	
	public BasicMaterial(Color color) {
		
		this.color = color;
		
		program = ProgramManager.getInstance().getProgram("BASIC");
		init(7,0,3, "u_MVPMatrix", "a_Position");
		colorHandle = GLES20.glGetAttribLocation(program.handle, "a_Color");
	}
	
	public void render(FloatBuffer vertexBuffer) {
	    // Pass in the color information
	    vertexBuffer.position(colorOffset);
	    GLES20.glVertexAttribPointer(colorHandle, colorDataSize, GLES20.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(colorHandle);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void addVertexData(float[] vertexData) {
		int vertexCount = vertexData.length / strideBytes;
		for (int i = 0; i < vertexCount; i++) {
			int pos = i * strideBytes;
			vertexData[pos + colorOffset + 0] = color.r;
			vertexData[pos + colorOffset + 1] = color.g;
			vertexData[pos + colorOffset + 2] = color.b;
			vertexData[pos + colorOffset + 3] = color.a;
		}
	}
}
