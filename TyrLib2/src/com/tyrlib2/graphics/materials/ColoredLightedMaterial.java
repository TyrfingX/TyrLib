package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.graphics.lighting.LightingType;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class ColoredLightedMaterial extends LightedMaterial {
	private int colorOffset = 6;
	private int colorDataSize = 4;
	private int colorHandle;
	private Color[] colors;
	
	private int normalOffset = 3;
	private int normalDataSize = 3;
	private int normalHandle;
	
	/** Contains the model*view matrix **/
	private float[] mvMatrix = new float[16];

	
	public static final String PER_VERTEX_PROGRAM_NAME = "BASIC_LIGHTED";
	public static final String PER_PIXEL_PROGRAM_NAME = "BASIC_PER_PIXEL_LIGHTED";
	
	public ColoredLightedMaterial(Color[] colors, LightingType type) {
		
		this.colors = colors;
		
		switch (type) {
		case PER_PIXEL:
			program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME);
			break;
		case PER_VERTEX:
			program = ProgramManager.getInstance().getProgram(PER_VERTEX_PROGRAM_NAME);
			break;
		}
		
		
		init(10,0,3, "u_MVPMatrix", "a_Position");
		colorHandle = TyrGL.glGetAttribLocation(program.handle, "a_Color");
		normalHandle = TyrGL.glGetAttribLocation(program.handle, "a_Normal");
		lightPosHandle = TyrGL.glGetUniformLocation(program.handle, "u_LightPos");
		mvMatrixHandle = TyrGL.glGetUniformLocation(program.handle, "u_MVMatrix"); 
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
	    // Pass in the color information
	    vertexBuffer.position(colorOffset);
	    TyrGL.glVertexAttribPointer(colorHandle, colorDataSize, TyrGL.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    TyrGL.glEnableVertexAttribArray(colorHandle);
	    
	    // Pass in the normal information
	    vertexBuffer.position(normalOffset);
	    TyrGL.glVertexAttribPointer(normalHandle, normalDataSize, TyrGL.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    TyrGL.glEnableVertexAttribArray(normalHandle);
	    
	    SceneManager sceneManager = SceneManager.getInstance();
	    float[] viewMatrix = sceneManager.getRenderer().getCamera().getViewMatrix();
	    Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);  
	    
        // Pass in the modelview matrix.
	    TyrGL.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);

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
		
		int vertexCount = points.length;
		
		Vector3[] normals = new Vector3[points.length];
		for (int i = 0; i < vertexCount; ++i) {
			normals[i] = new Vector3();

			for (int j = 0; j < drawOrder.length; j += 3) {
				if (drawOrder[j] == i || drawOrder[j+1] == i || drawOrder[j + 2] == i) {
					Vector3 u = points[drawOrder[j]].vectorTo(points[drawOrder[j+1]]);
					Vector3 v = points[drawOrder[j+1]].vectorTo(points[drawOrder[j+2]]);
					Vector3 normal = u.cross(v);
					normal.normalize();
					
					normals[i] = normals[i].add(normal);
				}
			}
			normals[i].normalize();
		}
		
		
		
		for (int i = 0; i < vertexCount; i++) {
			
			int pos = i * strideBytes;
			int color = i % colors.length;
			vertexData[pos + colorOffset + 0] = colors[color].r;
			vertexData[pos + colorOffset + 1] = colors[color].g;
			vertexData[pos + colorOffset + 2] = colors[color].b;
			vertexData[pos + colorOffset + 3] = colors[color].a;
			
			vertexData[pos + normalOffset + 0] = normals[i].x;
			vertexData[pos + normalOffset + 1] = normals[i].y;
			vertexData[pos + normalOffset + 2] = normals[i].z;
			
		}
		
		return vertexData;
	}

}
