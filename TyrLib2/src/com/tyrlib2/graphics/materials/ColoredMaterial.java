package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.graphics.renderer.IBlendable;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

/**
 * A material for simple per vertex colouring
 * @author Sascha
 *
 */

public class ColoredMaterial extends Material implements IBlendable {

	private int colorOffset = 3;
	private int colorDataSize = 4;
	private int colorHandle;
	private Color[] colors;

	private float alpha = 1;
	
	public ColoredMaterial(Color[] colors) {
		
		this.colors = colors;
		
		program = ProgramManager.getInstance().getProgram("BASIC");
		init(7,0,3, "u_MVPMatrix", "a_Position");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
		super.render(vertexBuffer, modelMatrix);
		
		colorHandle = GLES20.glGetAttribLocation(program.handle, "a_Color");
		
	    // Pass in the color information
	    vertexBuffer.position(colorOffset);
	    GLES20.glVertexAttribPointer(colorHandle, colorDataSize, GLES20.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(colorHandle);
	    
	    int alphaHandle = GLES20.glGetUniformLocation(program.handle, "u_Alpha");
	    GLES20.glUniform1f(alphaHandle, alpha);
	    
	    GLES20.glEnable( GLES20.GL_BLEND );
	    GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA );
	}
	
	public Color[] getColors() {
		return colors;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public float getAlpha() {
		return alpha;
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
