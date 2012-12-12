package com.tyrlib2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.OpenGLRenderer;
import com.tyrlib2.renderer.ProgramManager;
import com.tyrlib2.util.Color;

/**
 * A material for simple per vertex colouring
 * @author Sascha
 *
 */

public class BasicMultiColorMaterial extends Material {

	private int colorOffset = 3;
	private int colorDataSize = 4;
	private int colorHandle;
	private Color[] colors;

	
	public BasicMultiColorMaterial(Color[] colors) {
		
		this.colors = colors;
		
		program = ProgramManager.getInstance().getProgram("BASIC");
		init(7,0,3, "u_MVPMatrix", "a_Position");
		colorHandle = GLES20.glGetAttribLocation(program.handle, "a_Color");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
	    // Pass in the color information
	    vertexBuffer.position(colorOffset);
	    GLES20.glVertexAttribPointer(colorHandle, colorDataSize, GLES20.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(colorHandle);
	}
	
	public Color[] getColors() {
		return colors;
	}
	
	/**
	 * Adds the colors to the vertex data.
	 * Repeats the colors if there are more vertices than colors
	 */
	
	public float[] createVertexData(Vector3[] points, short[] drawOrder) {
		float[] vertexData = super.createVertexData(points, drawOrder);
		
		int vertexCount = points.length;;
		for (int i = 0; i < vertexCount; i++) {
			int pos = i * strideBytes;
			int color = i % colors.length;
			vertexData[pos + colorOffset + 0] = colors[color].r;
			vertexData[pos + colorOffset + 1] = colors[color].g;
			vertexData[pos + colorOffset + 2] = colors[color].b;
			vertexData[pos + colorOffset + 3] = colors[color].a;
		}
		
		return vertexData;
	}
}
