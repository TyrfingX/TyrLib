package com.tyrlib2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.lighting.Light;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.OpenGLRenderer;
import com.tyrlib2.renderer.ProgramManager;
import com.tyrlib2.scene.SceneManager;

public class BasicMultiColorLightedMaterial extends Material {
	private int colorOffset = 6;
	private int colorDataSize = 4;
	private int colorHandle;
	private Color[] colors;
	
	private int normalOffset = 3;
	private int normalDataSize = 3;
	private int normalHandle;
	
	private int lightPosHandle;
	private int mvMatrixHandle;
	
	/** Contains the model*view matrix **/
	private float[] mvMatrix = new float[16];

	
	public BasicMultiColorLightedMaterial(Color[] colors) {
		
		this.colors = colors;
		
		program = ProgramManager.getInstance().getProgram("BASIC_LIGHTED");
		init(10,0,3, "u_MVPMatrix", "a_Position");
		colorHandle = GLES20.glGetAttribLocation(program.handle, "a_Color");
		normalHandle = GLES20.glGetAttribLocation(program.handle, "a_Normal");
		lightPosHandle = GLES20.glGetUniformLocation(program.handle, "u_LightPos");
		mvMatrixHandle = GLES20.glGetUniformLocation(program.handle, "u_MVMatrix"); 
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
	    // Pass in the color information
	    vertexBuffer.position(colorOffset);
	    GLES20.glVertexAttribPointer(colorHandle, colorDataSize, GLES20.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(colorHandle);
	    
	    // Pass in the normal information
	    vertexBuffer.position(normalOffset);
	    GLES20.glVertexAttribPointer(normalHandle, normalDataSize, GLES20.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(normalHandle);
	    
	    SceneManager sceneManager = SceneManager.getInstance();
	    float[] viewMatrix = sceneManager.getRenderer().getCamera().getViewMatrix();
	    Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);  
	    
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);
	    
	    if (sceneManager.getLightCount() != 0) {
	    	
	    	Light light = sceneManager.getLight(0);
	    	float[] lightPosInEyeSpace = light.getEyeSpaceVector();
	    	
	    	// Pass in the light position in eye space.        
	    	GLES20.glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
	    }

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
