package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import com.tyrlib2.graphics.renderer.IBlendable;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.renderer.TyrGL;
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
	private TextureRegion region;
	private boolean blending = true;
	
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
	
	public PointSpriteMaterial(String textureName, TextureRegion region, float size, Color color) {
		program = ProgramManager.getInstance().getProgram("POINT_SHEET");
		
		this.textureName = textureName;
		this.size = size;
		this.color = color;
		this.region = region;
				
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		init(3,0,3, "u_MVPMatrix", "a_Position");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
        super.render(vertexBuffer, modelMatrix);
        

        
		int sizeHandle = TyrGL.glGetUniformLocation(program.handle, "u_Size");
		TyrGL.glUniform1f(sizeHandle, size);
		
		if (region != null ){
			int textureCoordPointSizeX = TyrGL.glGetUniformLocation(program.handle, "u_TextureCoordPointSizeX");
			TyrGL.glUniform1f(textureCoordPointSizeX, region.u2 - region.u1);
			
			int textureCoordPointSizeY = TyrGL.glGetUniformLocation(program.handle, "u_TextureCoordPointSizeY");
			TyrGL.glUniform1f(textureCoordPointSizeY, region.v2 - region.v1);
			
			int textureCoordIn = TyrGL.glGetUniformLocation(program.handle, "u_TextureCoordIn");
			TyrGL.glUniform2f(textureCoordIn, region.u1,region.v1);
		}
		
		int textureHandle = texture.getHandle();
	
		if (program.textureHandle != textureHandle) {
		
//			
//			TyrGL.glEnableVertexAttribArray(textureUniformHandle);
			
		    // Set the active texture unit to texture unit 0.
			TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
		 
		    // Bind the texture to this unit.
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle);
		 
		    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
			int textureUniformHandle = TyrGL.glGetUniformLocation(program.handle, "u_Texture");
			TyrGL.glUniform1i(textureUniformHandle, 0);
		    
		    program.textureHandle = textureHandle;
		    
		    OpenGLRenderer.textureFails++;
		    
		}
	    
		if (blending) {
			Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_SRC_ALPHA);
		} else {
			Program.blendDisable();
		}
	}
	
	public void setBlending(boolean blending) {
		this.blending = blending;
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
	
	public float getSize() {
		return size;
	}
	
	public TextureRegion getRegion() {
		return region;
	}
}
