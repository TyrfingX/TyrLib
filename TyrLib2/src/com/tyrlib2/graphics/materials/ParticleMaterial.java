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

public class ParticleMaterial extends Material implements IBlendable {
	
	private String textureName;
	private Texture texture;
	private Color color;
	private TextureRegion region;
	private boolean blending = true;
	
	public ParticleMaterial(String textureName, Color color) {
		program = ProgramManager.getInstance().getProgram("PARTICLE");
		
		this.textureName = textureName;
		this.color = color;
		this.region = new TextureRegion();
				
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		init(3,0,3, "u_MVPMatrix", "a_Position");
	}
	
	public ParticleMaterial(String textureName, TextureRegion region, Color color) {
		program = ProgramManager.getInstance().getProgram("PARTICLE");
		
		this.textureName = textureName;
		this.color = color;
		this.region = region;
				
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		init(3,0,3, "u_MVPMatrix", "a_Position");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
        super.render(vertexBuffer, modelMatrix);
		
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
			Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_ONE_MINUS_SRC_ALPHA);
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
		return new ParticleMaterial(textureName, color.copy());
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public TextureRegion getRegion() {
		return region;
	}
	
	public void setRegion(TextureRegion region) {
		this.region = region;
	}
}
