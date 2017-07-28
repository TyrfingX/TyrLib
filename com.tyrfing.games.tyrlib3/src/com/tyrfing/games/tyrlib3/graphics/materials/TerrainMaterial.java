package com.tyrfing.games.tyrlib3.graphics.materials;


import java.nio.FloatBuffer;

import com.tyrfing.games.tyrlib3.graphics.renderer.Material;
import com.tyrfing.games.tyrlib3.graphics.renderer.Mesh;
import com.tyrfing.games.tyrlib3.graphics.renderer.OpenGLRenderer;
import com.tyrfing.games.tyrlib3.graphics.renderer.ProgramManager;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.graphics.renderer.VertexLayout;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.graphics.terrain.TerrainTexture;
import com.tyrfing.games.tyrlib3.math.Matrix;
import com.tyrfing.games.tyrlib3.math.Vector2F;

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
	
	public static int TEXTURE_WEIGHT = 4;
	
	public static final int DEFAULT_UV_OFFSET = 3;
	public static final int DEFAULT_UV_SIZE = 2;
	
	public static final int DEFAULT_TEXTURE_WEIGHT_OFFSET = 5;
	public static final int DEFAULT_TEXTURE_WEIGHT_SIZE = 4;
	
	/** Texture information of this object **/
	private int textureCoordinateHandle;
	private TerrainTexture[] textures;
	
	private int[] textureUniformHandle;
	private int textureWeightHandle;
	
	/** Contains the model*view matrix **/
	private float[] mvMatrix = new float[16];
	
	public static final String PROGRAM_NAME = "TERRAIN";
	public static final int TEXTURES_PER_CHUNK = 4;
	
	private Vector2F repeat;
	
	public TerrainMaterial() {
		
	}
	

	public TerrainMaterial(TerrainMaterial terrainMaterial) {
		setup(terrainMaterial.repeat);
		System.arraycopy(terrainMaterial.textures, 0, textures, 0, TerrainMaterial.TEXTURES_PER_CHUNK);
	}
	
	public TerrainMaterial(Vector2F repeat) {
		program = ProgramManager.getInstance().getProgram(PROGRAM_NAME);
		setup(repeat);
	}
	
	protected void setup(Vector2F repeat) {
		lighted = true;
		this.repeat = repeat;
		textures = new TerrainTexture[TerrainMaterial.TEXTURES_PER_CHUNK];
		
		init(0,3, "u_MVPMatrix", "a_Position");
		this.addVertexInfo(VertexLayout.UV, DEFAULT_UV_OFFSET, DEFAULT_UV_SIZE);
		this.addVertexInfo(TEXTURE_WEIGHT, DEFAULT_TEXTURE_WEIGHT_OFFSET, DEFAULT_TEXTURE_WEIGHT_SIZE);
	}
	
	public void render(Mesh mesh, float[] modelMatrix) {
	    super.render(mesh, modelMatrix);
	    
		ambientHandle = TyrGL.glGetUniformLocation(program.handle, "u_Ambient");
		
		textureUniformHandle = new int[TEXTURES_PER_CHUNK];
		
		for (int i = 0; i < TEXTURES_PER_CHUNK; ++i) {
			textureUniformHandle[i] = TyrGL.glGetUniformLocation(program.handle, "u_Texture" + i);
		}
		
	    textureCoordinateHandle = TyrGL.glGetAttribLocation(program.handle, "a_TexCoordinate");
	    textureWeightHandle = TyrGL.glGetAttribLocation(program.handle, "a_TexWeights");
		
		
		if (program.meshChange) {
			passMesh(mesh);
		}
	    
	    SceneManager sceneManager = SceneManager.getInstance();
	    float[] viewMatrix = sceneManager.getRenderer().getCamera().getViewMatrix();
	    Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);  
        
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
	
	protected void passMesh(Mesh mesh)
	{	
		FloatBuffer vertexBuffer = mesh.getVertexBuffer();
		if (mesh.isUsingVBO()) {
			
			if (textureWeightHandle != -1) {
			    TyrGL.glVertexAttribPointer(textureWeightHandle, getTextureWeightSize(), TyrGL.GL_FLOAT, false,
			    							getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, getTextureWeightOffset() * OpenGLRenderer.BYTES_PER_FLOAT);
			 
			    TyrGL.glEnableVertexAttribArray(textureWeightHandle);
			}
			
			if (textureCoordinateHandle != -1) {
		        // Pass in the texture coordinate information
		        TyrGL.glVertexAttribPointer(textureCoordinateHandle, getUVSize(), TyrGL.GL_FLOAT, false, 
		        		getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, getUVOffset() * OpenGLRenderer.BYTES_PER_FLOAT);
		        TyrGL.glEnableVertexAttribArray(textureCoordinateHandle);
			}
			
			
			
		} else {
		    
	        // Pass in the texture weight information
	        vertexBuffer.position(getInfoOffset(TEXTURE_WEIGHT));
	        TyrGL.glVertexAttribPointer(textureWeightHandle, getInfoSize(TEXTURE_WEIGHT), TyrGL.GL_FLOAT, false, 
	        		getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	        
	        TyrGL.glEnableVertexAttribArray(textureWeightHandle);
			
	        // Pass in the texture coordinate information
	        vertexBuffer.position(getUVOffset());
	        TyrGL.glVertexAttribPointer(textureCoordinateHandle, getUVSize(), TyrGL.GL_FLOAT, false, 
	        		getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	        
	        TyrGL.glEnableVertexAttribArray(textureCoordinateHandle);
		}
	}
	
	public int getUVOffset(){
		return getInfoOffset(VertexLayout.UV);
	}
	
	public int getUVSize(){
		return getInfoSize(VertexLayout.UV);
	}
	
	public int getTextureWeightOffset() {
		return getInfoOffset(TEXTURE_WEIGHT);
	}
	
	public int getTextureWeightSize() {
		return getInfoSize(TEXTURE_WEIGHT);
	}
	
	public Material copy() {
		TerrainMaterial material = new TerrainMaterial(this);
		return material;
	}
	
	public Vector2F getRepeat() {
		return repeat;
	}
	
	public void setTexture(TerrainTexture terrainTexture, int texid) {
		textures[texid] = terrainTexture;
	}
	
	public TerrainTexture getTerrainTexture(int id) {
		return textures[id];
	}
}
