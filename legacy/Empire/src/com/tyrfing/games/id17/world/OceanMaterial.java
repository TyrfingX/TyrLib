package com.tyrfing.games.id17.world;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.lighting.Light;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.util.Color;

public class OceanMaterial extends DefaultMaterial3 implements IUpdateable {
	
	private int modelMatrixHandle;
	
	private static final float[] mvpMatrix = new float[16];
	
	private int waveTimeHandle;
	private float waveTime;
	private int bumpMapTextureHandle;
	private int viewDirectionHandle;
	private int fogMapHandle;
	private int sizeHandle;
	
	public OceanMaterial(String textureName, float repeatX, float repeatY, Color[] colors) {
		super(ProgramManager.getInstance().getProgram("OCEAN_PROGRAM"), textureName, repeatX, repeatY, colors);
		lighted = false;
		World.getInstance().getUpdater().addItem(this);
	}
	
	@Override
	public void updateHandles() {
		super.updateHandles();
		//modelMatrixHandle = TyrGL.glGetUniformLocation(program.handle, "u_M");
		waveTimeHandle = TyrGL.glGetUniformLocation(program.handle, "u_Time");
		bumpMapTextureHandle = TyrGL.glGetUniformLocation(program.handle, "u_BumpMap");
		viewDirectionHandle = TyrGL.glGetUniformLocation(program.handle, "u_CamPos");
		lightPosHandle = TyrGL.glGetUniformLocation(program.handle, "u_LightPos");
		fogMapHandle = TyrGL.glGetUniformLocation(program.handle, "u_FogMap");
		sizeHandle = TyrGL.glGetUniformLocation(program.handle, "u_Size");
	}
	
	@Override
	public void render(Mesh mesh, float[] modelMatrix) {
		super.render(mesh, modelMatrix);
		TyrGL.glUniform1f(waveTimeHandle, waveTime);
		Light light = SceneManager.getInstance().getLight(0);
		TyrGL.glUniform3f(lightPosHandle, light.getLightVector()[0], light.getLightVector()[1], light.getLightVector()[2]);
		Camera camera = SceneManager.getInstance().getActiveCamera();
		TyrGL.glUniform3f(viewDirectionHandle, camera.getLookDirection().x, camera.getLookDirection().y, camera.getLookDirection().z);
		
		TyrGL.glUniform2f(sizeHandle, 1/repeatX, 1/repeatY);
		
		/*
		Matrix.setLookAtM(	mvpMatrix, 
				0, 
				0,0,1, 
				0,0,0,
				1,0,0);

        // Apply the projection and view transformation
		Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);
        // Combine the rotation matrix with the projection and camera view
		TyrGL.glUniformMatrix4fv(modelMatrixHandle, 1, false, mvpMatrix, 0);
		*/
	}
	
	@Override
	protected void passTexture(int textureHandle) {
		super.passTexture(textureHandle);
			
	    // Set the active texture unit to texture unit 1.
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE3);
		
	    // Bind the texture to this unit.
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, TextureManager.getInstance().getTexture("BUMP_MAP_TEST").getHandle());
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 1.
		TyrGL.glUniform1i(bumpMapTextureHandle, 3);
		
		Texture fogMap = TextureManager.getInstance().getTexture("FOG_MAP");
		
		if (fogMap == null) {
			fogMap = TextureManager.getInstance().getTexture("WHITE");
		}
		
	    // Set the active texture unit to texture unit 1.
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE2);
		
	    // Bind the texture to this unit.
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, fogMap.getHandle());
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 1.
		TyrGL.glUniform1i(fogMapHandle, 2);
		
	}

	@Override
	public void onUpdate(float time) {
		waveTime += time;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
