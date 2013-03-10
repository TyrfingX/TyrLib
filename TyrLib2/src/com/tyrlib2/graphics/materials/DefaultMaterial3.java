package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.graphics.lighting.LightingType;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneManager;
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
	
	private LightingType type;
	
	/** Per vertex color of this object **/
	public static final int colorOffset = 6;
	public static final int colorDataSize = 4;
	private int colorHandle;
	private Color[] colors;
	
	/** Per vertex normals of this object **/
	public static final int normalOffset = 3;
	public static final int normalDataSize = 3;
	private int normalHandle;
	
	/** Texture information of this object **/
	public static final int uvOffset = 10;
	public static final int uvDataSize = 2;
	private int textureUniformHandle;
	private int textureCoordinateHandle;
	private String textureName;
	private Texture texture;
	private float repeatX;
	private float repeatY;
	
	public static final int dataSize = 12;
	public static final int posOffset = 0;
	
	/** Contains the model*view matrix **/
	private float[] mvMatrix = new float[16];
	
	public static final String PER_VERTEX_PROGRAM_NAME = "TEXTURED_PVL";
	public static final String PER_PIXEL_PROGRAM_NAME = "TEXTURED_PPL";

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
		
		setup(textureName, repeatX, repeatY, type, colors);
	}
	
	public DefaultMaterial3(Context context, String textureName, float repeatX, float repeatY, LightingType type, Color[] colors) {

		program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME);
		
		if (colors == null) {
			colors = new Color[1];
			colors[0] = new Color(1,1,1,1);
		}
		
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		setup(textureName, repeatX, repeatY, type, colors);

	}
	
	public DefaultMaterial3(String textureName, float repeatX, float repeatY, LightingType type, Color[] colors) {
		

		if (colors == null) {
			colors = new Color[1];
			colors[0] = new Color(1,1,1,1);
		}
		
		switch (type) {
		case PER_PIXEL:
			program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME);
			break;
		case PER_VERTEX:
			program = ProgramManager.getInstance().getProgram(PER_VERTEX_PROGRAM_NAME);
			break;
		}

		this.type = type;
		
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		setup(textureName, repeatX, repeatY, type, colors);


	}
	
	protected void setup(String textureName, float repeatX, float repeatY, LightingType type, Color[] colors) {
		lighted = true;
		this.type = type;
		
		this.boneParam = "u_Bone";
		this.boneIndexParam = "a_BoneIndex";
		this.boneWeightParam = "a_BoneWeight";
		
		this.textureName = textureName;
		this.colors = colors; 
		this.repeatX = repeatX;
		this.repeatY = repeatY;
		
		init(12,posOffset,3, "u_MVPMatrix", "a_Position");

	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
	    super.render(vertexBuffer, modelMatrix);
	    
		normalHandle = GLES20.glGetAttribLocation(program.handle, "a_Normal");
		lightPosHandle = GLES20.glGetUniformLocation(program.handle, "u_LightPos");
		lightTypeHandle = GLES20.glGetUniformLocation(program.handle, "u_LightType");
		mvMatrixHandle = GLES20.glGetUniformLocation(program.handle, "u_MVMatrix"); 
		ambientHandle = GLES20.glGetUniformLocation(program.handle, "u_Ambient");
	    textureUniformHandle = GLES20.glGetUniformLocation(program.handle, "u_Texture");
	    textureCoordinateHandle = GLES20.glGetAttribLocation(program.handle, "a_TexCoordinate");
	    colorHandle = GLES20.glGetAttribLocation(program.handle, "a_Color");
		int textureHandle = texture.getHandle();
		
		if (program.meshChange) {
		
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
		    
	        // Pass in the texture coordinate information
	        vertexBuffer.position(uvOffset);
	        GLES20.glVertexAttribPointer(textureCoordinateHandle, uvDataSize, GLES20.GL_FLOAT, false, 
	        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	        
	        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
	    
		}
	    
	    SceneManager sceneManager = SceneManager.getInstance();
	    float[] viewMatrix = sceneManager.getRenderer().getCamera().getViewMatrix();
	    Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);  
	    
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);
	    
        if (program.textureHandle != textureHandle) {
        
		    // Set the active texture unit to texture unit 0.
		    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		 
		    // Bind the texture to this unit.
		    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		 
		    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		    GLES20.glUniform1i(textureUniformHandle, 0);
		    
		    program.textureHandle = textureHandle;
	    
        }
	    
	    if (!animated) {

	    	// pass some data to make sure skinning is disabled
	    	int indexHandle = GLES20.glGetAttribLocation(program.handle, boneIndexParam);
	    	GLES20.glDisableVertexAttribArray(indexHandle);
	    	GLES20.glVertexAttrib4f(indexHandle, -1, -1, -1, -1);
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
			int color = i % colors.length;
			vertexData[pos + colorOffset + 0] = colors[color].r;
			vertexData[pos + colorOffset + 1] = colors[color].g;
			vertexData[pos + colorOffset + 2] = colors[color].b;
			vertexData[pos + colorOffset + 3] = colors[color].a;
			
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
	
	public int getColorOffset() {
		return colorOffset;
	}
	
	public int getNormalOffset() {
		return normalOffset;
	}
	
	public int getUVOffset(){
		return uvOffset;
	}
	
	public Material copy() {
		DefaultMaterial3 material = new DefaultMaterial3(textureName, repeatX, repeatY, type, colors);
		return material;
	}
	
	public void setTexture(Texture texture, String textureName) {
		this.texture = texture;
		this.textureName = textureName;
	}
	
	public Vector2 getRepeat() {
		return new Vector2(repeatX, repeatY);
	}
	
	
}
