package com.tyrlib2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.renderer.IBlendable;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.ProgramManager;
import com.tyrlib2.renderer.Texture;
import com.tyrlib2.renderer.TextureManager;
import com.tyrlib2.util.Color;

/** 
 * Material for rendering point sprites.
 * @author Sascha
 *
 */

public class PointSpriteMaterial extends Material implements IBlendable {
	
	private String textureName;
	private Texture texture;
	private float size;
	private Color color;
	
	public PointSpriteMaterial(String textureName, float size, Color color) {
		program = ProgramManager.getInstance().getProgram("POINT_SPRITE");
		
		this.textureName = textureName;
		this.size = size;
		this.color = color;
				
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		init(3,0,3, "u_MVPMatrix", "a_Position");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
        super.render(vertexBuffer, modelMatrix);
        
		int sizeHandle = GLES20.glGetUniformLocation(program.handle, "u_Size");
		GLES20.glUniform1f(sizeHandle, size);
		
		int colorHandle = GLES20.glGetUniformLocation(program.handle, "u_Color");
		GLES20.glUniform4f(colorHandle, 1, 1, 1,1);
		
		int textureHandle = texture.getHandle();
	
		if (program.textureHandle != textureHandle) {
		
			int textureUniformHandle = GLES20.glGetUniformLocation(program.handle, "u_Texture");
			GLES20.glEnableVertexAttribArray(textureUniformHandle);
			
		    // Set the active texture unit to texture unit 0.
		    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		 
		    // Bind the texture to this unit.
		    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		 
		    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		    GLES20.glUniform1i(textureUniformHandle, 0);
		    
		    program.textureHandle = textureHandle;
		    
		}
	    
	    GLES20.glEnable( GLES20.GL_BLEND );
	    GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA);
	}
	
	public String getTextureName(){
		return textureName;
	}

	@Override
	public float getAlpha() {
		return color.a;
	}

	@Override
	public void setAlpha(float alpha) {
		this.color.a = alpha;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Material copy() {
		return new PointSpriteMaterial(textureName, size, color.copy());
	}
	
	public Texture getTexture() {
		return texture;
	}
}
