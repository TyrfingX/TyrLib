package com.tyrlib2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.renderer.IBlendable;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.ProgramManager;
import com.tyrlib2.renderer.Texture;
import com.tyrlib2.renderer.TextureManager;

/** 
 * Material for rendering point sprites.
 * @author Sascha
 *
 */

public class PointSpriteMaterial extends Material implements IBlendable {
	
	private String textureName;
	private Texture texture;
	private float size;
	private float alpha;
	
	public PointSpriteMaterial(String textureName, float size, float alpha) {
		program = ProgramManager.getInstance().getProgram("POINT_SPRITE");
		
		this.textureName = textureName;
		this.size = size;
		this.alpha = alpha;
				
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		init(3,0,3, "u_MVPMatrix", "a_Position");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
        
		int sizeHandle = GLES20.glGetUniformLocation(program.handle, "u_Size");
		GLES20.glUniform2f(sizeHandle, size, alpha);
		
		int textureHandle = texture.getHandle();
		int textureUniformHandle = GLES20.glGetUniformLocation(program.handle, "u_Texture");
		
	    // Set the active texture unit to texture unit 0.
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	 
	    // Bind the texture to this unit.
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
	    GLES20.glUniform1i(textureUniformHandle, 0);
	    
	    GLES20.glEnable( GLES20.GL_BLEND );
	    GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA);
	}
	
	public String getTextureName(){
		return textureName;
	}

	@Override
	public float getAlpha() {
		return alpha;
	}

	@Override
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
}
