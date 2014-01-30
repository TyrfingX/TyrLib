package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;

import com.tyrlib2.graphics.lighting.LightingType;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

/**
 * Default material for rendering 3D objects especially entities.
 * Supports:
 * - Lighting
 * - Per vertex coloring
 * - Skinning/skeletal animation
 * - Texturing
 * @author Sascha
 *
 */

public class DefaultMaterial3 extends LightedMaterial {
	
	/** Per vertex color of this object **/
	private int colorHandle;
	private Color[] colors;
	
	/** Per vertex normals of this object **/
	public static final int normalOffset = 3;
	public static final int normalDataSize = 3;
	private int normalHandle;
	
	/** Texture information of this object **/
	public static final int uvOffset = 6;
	public static final int uvDataSize = 2;
	private int textureUniformHandle;
	private int textureCoordinateHandle;
	private String textureName;
	private Texture texture;
	private float repeatX;
	private float repeatY;
	
	public static final int dataSize = 8;
	public static final int posOffset = 0;
	
	/** Contains the model*view matrix **/
	private float[] mvMatrix = new float[16];
	
	public static final String PER_PIXEL_PROGRAM_NAME = "TEXTURED_PPL";

	private boolean transparent;
	
	private static boolean wasAnimated = false;
	
	public DefaultMaterial3() {
		
	}
	
	public DefaultMaterial3(Program program, String textureName, float repeatX, float repeatY, Color[] colors) {
		this.program = program;
		
		if (colors == null) {
			colors = new Color[1];
			colors[0] = new Color(1,1,1,1);
		}
		
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		setup(textureName, repeatX, repeatY, colors);
	}
	
	public DefaultMaterial3(String textureName, float repeatX, float repeatY, Color[] colors) {

		program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME);
		
		if (colors == null) {
			colors = new Color[1];
			colors[0] = new Color(1,1,1,1);
		}
		
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		setup(textureName, repeatX, repeatY, colors);

	}
	
	public DefaultMaterial3(String textureName, float repeatX,
			float repeatY, Color[] colors,
			boolean animated) {
		
		if (colors == null) {
			colors = new Color[1];
			colors[0] = new Color(1,1,1,1);
		}
		
		String add = "";
		
		if (animated) {
			add = "_ANIMATED";
		}
		
	
		program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME + add);


		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		setup(textureName, repeatX, repeatY, colors);
		
	}

	protected void setup(String textureName, float repeatX, float repeatY, Color[] colors) {
		lighted = true;
		
		this.boneParam = "u_Bone";
		this.boneIndexParam = "a_BoneIndex";
		this.boneWeightParam = "a_BoneWeight";
		
		this.textureName = textureName;
		this.colors = colors; 
		this.repeatX = repeatX;
		this.repeatY = repeatY;
		
		init(dataSize,posOffset,3, "u_MVPMatrix", "a_Position");
		
		mvMatrixHandle = GLES20.glGetUniformLocation(program.handle, "u_MVMatrix"); 
		textureUniformHandle = GLES20.glGetUniformLocation(program.handle, "u_Texture");
		ambientHandle = GLES20.glGetUniformLocation(program.handle, "u_Ambient");
		textureUniformHandle = GLES20.glGetUniformLocation(program.handle, "u_Texture");
		colorHandle = GLES20.glGetAttribLocation(program.handle, "a_Color");
		normalHandle = GLES20.glGetAttribLocation(program.handle, "a_Normal");
		textureCoordinateHandle = GLES20.glGetAttribLocation(program.handle, "a_TexCoordinate");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
		
		if (program.meshChange) {
			passMesh(vertexBuffer);
		}
	    
		//passModelViewMatrix(modelMatrix);
	    
		int textureHandle = texture.getHandle();
        if (program.textureHandle != textureHandle) {
        	passTexture(textureHandle);
        }
	    
	    if (!animated && wasAnimated) {

	    	// pass some data to make sure skinning is disabled
	    	int indexHandle = GLES20.glGetAttribLocation(program.handle, boneIndexParam);
	    	GLES20.glEnableVertexAttribArray(indexHandle);
	    	GLES20.glVertexAttrib4f(indexHandle, -1, -1, -1, -1);
	    	wasAnimated = false;
	    } else {
	    	wasAnimated = true;
	    }
	    
	    if (transparent) {
	    	Program.blendEnable(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	    }
	}
	
	
	public void render(int vboBuffer, float[] modelMatrix) {
		ambientHandle = GLES20.glGetUniformLocation(program.handle, "u_Ambient");
		
		if (program.meshChange) {
			passMesh(vboBuffer);
		}
	    
		//passModelViewMatrix(modelMatrix);
	    
		int textureHandle = texture.getHandle();
        if (program.textureHandle != textureHandle) {
        	
        	passTexture(textureHandle);
        }
        
	    if (!animated) {

	    	// pass some data to make sure skinning is disabled
	    	int indexHandle = GLES20.glGetAttribLocation(program.handle, boneIndexParam);
	    	GLES20.glEnableVertexAttribArray(indexHandle);
	    	GLES20.glVertexAttrib4f(indexHandle, -1, -1, -1, -1);
	    }
	    
	    if (transparent) {
	    	Program.blendEnable(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	    }
	}
	
//	private void passModelViewMatrix(float[] modelMatrix) {
//	    SceneManager sceneManager = SceneManager.getInstance();
//	    float[] viewMatrix = sceneManager.getRenderer().getCamera().getViewMatrix();
//	    Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);  
//	    
//	    
//        // Pass in the modelview matrix.
//        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);
//	}
	
	private void passMesh(FloatBuffer vertexBuffer)
	{	
	    // Pass in the normal information
	    vertexBuffer.position(normalOffset);
	    GLES20.glVertexAttribPointer(normalHandle, normalDataSize, GLES20.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    GLES20.glEnableVertexAttribArray(normalHandle);
	    
        // Pass in the texture coordinate information
        vertexBuffer.position(uvOffset);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, uvDataSize, GLES20.GL_FLOAT, false, 
        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
        
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
	}
	
	private void passMesh(int vboBufferHandle)
	{
		
	    // Pass in the normal information
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboBufferHandle);
	    GLES20.glEnableVertexAttribArray(normalHandle);
	    GLES20.glVertexAttribPointer(normalHandle, normalDataSize, GLES20.GL_FLOAT, false,
	    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, normalOffset * OpenGLRenderer.BYTES_PER_FLOAT);
	    
        // Pass in the texture coordinate information
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboBufferHandle);
	    GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, uvDataSize, GLES20.GL_FLOAT, false, 
        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, uvOffset * OpenGLRenderer.BYTES_PER_FLOAT);
        
        
	}
	
	private void passTexture(int textureHandle) {
	    // Set the active texture unit to texture unit 0.
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	 
	    // Bind the texture to this unit.
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
	    GLES20.glUniform1i(textureUniformHandle, 0);
	    
	    program.textureHandle = textureHandle;
	    
	    OpenGLRenderer.textureFails++;
	}

	public Color[] getColors() {
		return colors;
	}
	
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
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
				if (j + 2 < drawOrder.length) {
					if (drawOrder[j] == i || drawOrder[j+1] == i || drawOrder[j + 2] == i) {
						Vector3 u = points[drawOrder[j]].vectorTo(points[drawOrder[j+1]]);
						Vector3 v = points[drawOrder[j+1]].vectorTo(points[drawOrder[j+2]]);
						Vector3 normal = u.cross(v);
						normal.normalize();
						
						normals[i] = normals[i].add(normal);
					}
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
			
			vertexData[pos + normalOffset + 0] = normals[i].x;
			vertexData[pos + normalOffset + 1] = normals[i].y;
			vertexData[pos + normalOffset + 2] = normals[i].z;
			
			vertexData[pos + uvOffset + 0] = uvCoords[uvCoord] * repeatX;
			vertexData[pos + uvOffset + 1] = uvCoords[uvCoord+1] * repeatY;
			
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
	
	public int getNormalOffset() {
		return normalOffset;
	}
	
	public int getUVOffset(){
		return uvOffset;
	}
	
	public Material copy() {
		DefaultMaterial3 material = new DefaultMaterial3(textureName, repeatX, repeatY, colors, animated);
		return material;
	}
	
	public Material copy(boolean animated) {
		DefaultMaterial3 material = new DefaultMaterial3(textureName, repeatX, repeatY, colors, animated);
		return material;
	}
	
	public void setTexture(Texture texture, String textureName) {
		this.texture = texture;
		this.textureName = textureName;
	}
	
	public void setTexture( String textureName) {
		this.textureName = textureName;
		this.texture = TextureManager.getInstance().getTexture(textureName);
	}
	
	public Vector2 getRepeat() {
		return new Vector2(repeatX, repeatY);
	}
	
	
}
