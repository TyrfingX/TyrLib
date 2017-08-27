package com.tyrfing.games.id17.geometry;

import java.nio.FloatBuffer;

import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldChunk;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.renderer.VertexLayout;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector3;

public class GrassMaterial extends Material implements IUpdateable {
	
	/** Texture information of this object **/
	public static final int DEFAULT_UV_OFFSET = 6;
	public static final int DEFAULT_UV_SIZE = 2;
	
	/** Texture information of this object **/
	private int textureUniformHandle;
	private int textureCoordinateHandle;
	private int ambientHandle;
	private int windDirHandle;
	private int ownerHandle;
	private String textureName;
	private Texture texture;
	private WorldChunk chunk;
	
	private int shadowTextureHandle;
	private int depthMVPHandle;
	
	
	public static final int posOffset = 0;
	public static final String PROGRAM_NAME = "GRASS";
	
	private float time;
	private Vector3 windDir;
	private static final float[] mvpMatrix = new float[16];
	private int modelMatrixHandle;
	
	private static float[] shadowMVP = new float[16];

	public GrassMaterial(WorldChunk chunk, String textureName, Vector3 windDir, float windStrength, float initTime) {

		program = ProgramManager.getInstance().getProgram(PROGRAM_NAME);
		this.chunk = chunk;
		this.windDir = new Vector3(windDir);
		this.windDir.normalize();
		this.windDir = windDir.multiply(windStrength);
		this.time += initTime;
		setup(textureName);

	}

	protected void setup(String textureName) {
		this.textureName = textureName;

		init(posOffset,3, "u_MVPMatrix", "a_Position");
		
		addVertexInfo(VertexLayout.UV, DEFAULT_UV_OFFSET, DEFAULT_UV_SIZE);
		vertexLayout.setBytestride(vertexLayout.getByteStride()+DefaultMaterial3.DEFAULT_NORMAL_SIZE);
		
		texture = TextureManager.getInstance().getTexture(textureName);

		textureUniformHandle = TyrGL.glGetUniformLocation(program.handle, "u_Texture");
		ambientHandle = TyrGL.glGetUniformLocation(program.handle, "u_Ambient");
		textureCoordinateHandle = TyrGL.glGetAttribLocation(program.handle, "a_TexCoordinate");
		windDirHandle = TyrGL.glGetUniformLocation(program.handle, "u_WindDir");
		ownerHandle = TyrGL.glGetUniformLocation(program.handle, "u_Owner");
		modelMatrixHandle = TyrGL.glGetUniformLocation(program.handle, "u_M");
		shadowTextureHandle = TyrGL.glGetUniformLocation(program.handle, "u_ShadowMap");
		depthMVPHandle = TyrGL.glGetUniformLocation(program.handle, "u_DepthMVP");
	}

	@Override
	public void render(Mesh mesh, float[] modelMatrix) {
		
		if (SceneManager.getInstance().getRenderer().isShadowsEnabled() && depthMVPHandle != -1) {
			  // Apply the projection and view transformation
			Matrix.multiplyMM(shadowMVP, 0, SceneManager.getInstance().getRenderer().getShadowVP(), 0, modelMatrix, 0);
			
	        // Combine the rotation matrix with the projection and camera view
			TyrGL.glUniformMatrix4fv(depthMVPHandle, 1, false, shadowMVP, 0);
			
		    // Set the active texture unit to texture unit 2.
			TyrGL.glActiveTexture(TyrGL.GL_TEXTURE2);
	 
		    // Bind the texture to this unit.
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, SceneManager.getInstance().getRenderer().getShadowMapHandle());
		 
		    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
			TyrGL.glUniform1i(shadowTextureHandle, 2);
		}
		
        float diffuse = 0.9f;
        TyrGL.glUniform4f(ambientHandle, diffuse, diffuse, diffuse, World.getInstance().getWinter());
        TyrGL.glUniform4f(windDirHandle, windDir.x, windDir.y, windDir.z, time);
        TyrGL.glUniform1f(ownerHandle, chunk.getOwnerValue());
		
		Matrix.setLookAtM(	mvpMatrix, 
				0, 
				0,0,1, 
				0,0,0,
				1,0,0);

        // Apply the projection and view transformation
		Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);
        // Combine the rotation matrix with the projection and camera view
		TyrGL.glUniformMatrix4fv(modelMatrixHandle, 1, false, mvpMatrix, 0);
        
		if (program.meshChange) {
			passMesh(mesh);
		}

		int textureHandle = texture.getHandle();
		if (program.textureHandle != textureHandle) {
			passTexture(textureHandle);
		}
		Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_ONE_MINUS_SRC_ALPHA);

	}

	private void passMesh(Mesh mesh)
	{	
		
		FloatBuffer vertexBuffer = mesh.getVertexBuffer();
		if (mesh.isUsingVBO()) {
			// Pass in the texture coordinate information
			TyrGL.glVertexAttribPointer(textureCoordinateHandle, getInfoSize(VertexLayout.UV), TyrGL.GL_FLOAT, false, 
					getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, getInfoOffset(VertexLayout.UV) * OpenGLRenderer.BYTES_PER_FLOAT);

			TyrGL.glEnableVertexAttribArray(textureCoordinateHandle);
		} else {
			// Pass in the texture coordinate information
			vertexBuffer.position(getInfoOffset(VertexLayout.UV));
			TyrGL.glVertexAttribPointer(textureCoordinateHandle, getInfoSize(VertexLayout.UV), TyrGL.GL_FLOAT, false, 
					getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);

			TyrGL.glEnableVertexAttribArray(textureCoordinateHandle);
		}
	}

	private void passTexture(int textureHandle) {
		// Set the active texture unit to texture unit 0.
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);

		// Bind the texture to this unit.
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle);

		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		TyrGL.glUniform1i(textureUniformHandle, 0);

		program.textureHandle = textureHandle;

		OpenGLRenderer.setTextureFails(OpenGLRenderer.getTextureFails() + 1);
	}




	public String getTextureName() {
		return textureName;
	}
	
	public int getUVOffset(){
		return getInfoOffset(VertexLayout.UV);
	}

	public void setTexture(Texture texture, String textureName) {
		this.texture = texture;
		this.textureName = textureName;
	}

	public void setTexture( String textureName) {
		this.textureName = textureName;
		this.texture = TextureManager.getInstance().getTexture(textureName);
	}

	@Override
	public void onUpdate(float time) {
		if (World.getInstance().getSeason() != 3) {
			this.time += time;
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}




}
