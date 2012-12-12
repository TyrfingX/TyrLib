package com.tyrlib2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.lighting.Light;
import com.tyrlib2.lighting.LightingType;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.OpenGLRenderer;
import com.tyrlib2.renderer.ProgramManager;
import com.tyrlib2.renderer.TextureManager;
import com.tyrlib2.scene.SceneManager;
import com.tyrlib2.util.Color;

public class TexturedMaterial extends Material {
	
	
	/** Per vertex color of this object **/
	private int colorOffset = 6;
	private int colorDataSize = 4;
	private int colorHandle;
	private Color[] colors;
	
	/** Per vertex normals of this object **/
	private int normalOffset = 3;
	private int normalDataSize = 3;
	private int normalHandle;
	
	/** Texture information of this object **/
	private int uvOffset = 10;
	private int uvDataSize = 2;
	private int textureUniformHandle;
	private int textureCoordinateHandle;
	private String textureName;
	private int textureHandle;
	
	/** Lighting information **/
	private int lightPosHandle;
	private int mvMatrixHandle;
	private int ambientHandle;
	
	/** Contains the model*view matrix **/
	private float[] mvMatrix = new float[16];
	
	public static final String PER_VERTEX_PROGRAM_NAME = "TEXTURED_PVL";
	public static final String PER_PIXEL_PROGRAM_NAME = "TEXTURED_PPL";

	public TexturedMaterial(String textureName, LightingType type, Color[] colors) {
		
		switch (type) {
		case PER_PIXEL:
			program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME);
			break;
		case PER_VERTEX:
			program = ProgramManager.getInstance().getProgram(PER_VERTEX_PROGRAM_NAME);
			break;
		}
		
		this.textureName = textureName;
		this.colors = colors; 
		textureHandle = TextureManager.getInstance().getTextureHandle(textureName);
		
		init(12,0,3, "u_MVPMatrix", "a_Position");
		
		normalHandle = GLES20.glGetAttribLocation(program.handle, "a_Normal");
		lightPosHandle = GLES20.glGetUniformLocation(program.handle, "u_LightPos");
		mvMatrixHandle = GLES20.glGetUniformLocation(program.handle, "u_MVMatrix"); 
		ambientHandle = GLES20.glGetUniformLocation(program.handle, "u_Ambient");
	    textureUniformHandle = GLES20.glGetUniformLocation(program.handle, "u_Texture");
	    textureCoordinateHandle = GLES20.glGetAttribLocation(program.handle, "a_TexCoordinate");
	    colorHandle = GLES20.glGetAttribLocation(program.handle, "a_Color");
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
        
        //Pass in the global scene illumination
        Color ambient = SceneManager.getInstance().getAmbientLight();
	    GLES20.glUniform4f(ambientHandle, ambient.r, ambient.g, ambient.b, ambient.a);
        
	    if (sceneManager.getLightCount() != 0) {
	    	
	    	Light light = sceneManager.getLight(0);
	    	float[] lightPosInEyeSpace = light.getEyeSpaceVector();
	    	
	    	// Pass in the light position in eye space.        
	    	GLES20.glUniform3f(lightPosHandle, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
	    }
	 
	    
        // Pass in the texture coordinate information
        vertexBuffer.position(uvOffset);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, uvDataSize, GLES20.GL_FLOAT, false, 
        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
        
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
	    
	    // Set the active texture unit to texture unit 0.
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	 
	    // Bind the texture to this unit.
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
	    GLES20.glUniform1i(textureUniformHandle, 0);

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
		
		
		// Assign some arbitary uvCoordinates for the textures
		// The default uvCoords assigned by this work for shapes
		// like planes, meshs, etc
		float[] uvCoords = {
			0.0f, 0.0f, 				
			0.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
		};
		
		int uvCoord = 0;
		
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
			
			vertexData[pos + uvOffset + 0] = uvCoords[uvCoord];
			vertexData[pos + uvOffset + 1] = uvCoords[uvCoord+1];
			
			uvCoord += 2;
			
			if (uvCoord >= uvCoords.length) {
				uvCoord = 0;
				// Increase the coordinate numbers in order to prevent
				// weird clamping effects to occur in most cases
				for (int j = 0; j < uvCoords.length; ++j) {
					uvCoords[j] = uvCoords[j] + 1;
				}
			}
			
		}
		
		return vertexData;
	}
	
	public String getTextureName() {
		return textureName;
	}
}
