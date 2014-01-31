package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.terrain.TerrainTexture;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector2;

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

public class TerrainMaterial extends LightedMaterial {
	
	/** Per vertex normals of this object **/
	private int normalOffset = 3;
	private int normalDataSize = 3;
	private int normalHandle;
	
	/** Texture information of this object **/
	private int uvOffset = 6;
	private int uvDataSize = 2;
	private int textureCoordinateHandle;
	private TerrainTexture[] textures;
	
	private int[] textureUniformHandle;
	private int textureWeightHandle;
	
	private int textureWeightOffset = 8;
	private int textureWeightDataSize = 4;
	
	/** Contains the model*view matrix **/
	private float[] mvMatrix = new float[16];
	
	public static final String PROGRAM_NAME = "TERRAIN";
	public static final int TEXTURES_PER_CHUNK = 4;
	
	private Vector2 repeat;
	
	public TerrainMaterial() {
		
	}
	

	public TerrainMaterial(TerrainMaterial terrainMaterial) {
		setup(terrainMaterial.repeat);
		for (int i = 0; i < TerrainMaterial.TEXTURES_PER_CHUNK; ++i) {
			textures[i] = terrainMaterial.textures[i];
		}
	}
	
	public TerrainMaterial(Vector2 repeat) {
		program = ProgramManager.getInstance().getProgram(PROGRAM_NAME);
		setup(repeat);
	}
	
	protected void setup(Vector2 repeat) {
		lighted = true;
		this.repeat = repeat;
		textures = new TerrainTexture[TerrainMaterial.TEXTURES_PER_CHUNK];
		
		init(12,0,3, "u_MVPMatrix", "a_Position");

	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
	    super.render(vertexBuffer, modelMatrix);
	    
		normalHandle = TyrGL.glGetAttribLocation(program.handle, "a_Normal");
		lightPosHandle = TyrGL.glGetUniformLocation(program.handle, "u_LightPos");
		lightTypeHandle = TyrGL.glGetUniformLocation(program.handle, "u_LightType");
		mvMatrixHandle = TyrGL.glGetUniformLocation(program.handle, "u_MVMatrix"); 
		ambientHandle = TyrGL.glGetUniformLocation(program.handle, "u_Ambient");
		
		textureUniformHandle = new int[TEXTURES_PER_CHUNK];
		
		for (int i = 0; i < TEXTURES_PER_CHUNK; ++i) {
			textureUniformHandle[i] = TyrGL.glGetUniformLocation(program.handle, "u_Texture" + i);
		}
		
	    textureCoordinateHandle = TyrGL.glGetAttribLocation(program.handle, "a_TexCoordinate");
	    textureWeightHandle = TyrGL.glGetAttribLocation(program.handle, "a_TexWeights");
		
		
		if (program.meshChange) {
			
		    // Pass in the normal information
		    vertexBuffer.position(normalOffset);
		    TyrGL.glVertexAttribPointer(normalHandle, normalDataSize, TyrGL.GL_FLOAT, false,
		    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
		 
		    TyrGL.glEnableVertexAttribArray(normalHandle);
		    
	        // Pass in the texture coordinate information
	        vertexBuffer.position(uvOffset);
	        TyrGL.glVertexAttribPointer(textureCoordinateHandle, uvDataSize, TyrGL.GL_FLOAT, false, 
	        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	        
	        TyrGL.glEnableVertexAttribArray(textureCoordinateHandle);
	        
	        
	        // Pass in the texture weight information
	        vertexBuffer.position(textureWeightOffset);
	        TyrGL.glVertexAttribPointer(textureWeightHandle, textureWeightDataSize, TyrGL.GL_FLOAT, false, 
	        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	        
	        TyrGL.glEnableVertexAttribArray(textureWeightHandle);
	    
		}
	    
	    SceneManager sceneManager = SceneManager.getInstance();
	    float[] viewMatrix = sceneManager.getRenderer().getCamera().getViewMatrix();
	    Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);  
	    
        // Pass in the modelview matrix.
	    TyrGL.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);
        
		for (int i = 0; i < TEXTURES_PER_CHUNK; ++i) {
			
			if (textures[i] != null) {
			
				int textureHandle = textures[i].getTexture().getHandle();
				
			    // Set the active texture unit to texture unit 0.
				TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0 + i);
			 
			    // Bind the texture to this unit.
				TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle);
			    
			    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
				TyrGL.glUniform1i(textureUniformHandle[i], i);
			}
		}


	}
	
	public int getNormalOffset() {
		return normalOffset;
	}
	
	public int getUVOffset(){
		return uvOffset;
	}
	
	public int getTextureWeightOffset() {
		return textureWeightOffset;
	}
	
	public Material copy() {
		TerrainMaterial material = new TerrainMaterial(this);
		return material;
	}
	
	public Vector2 getRepeat() {
		return repeat;
	}
	
	public void setTexture(TerrainTexture terrainTexture, int texid) {
		textures[texid] = terrainTexture;
	}
	
	public TerrainTexture getTerrainTexture(int id) {
		return textures[id];
	}
}
