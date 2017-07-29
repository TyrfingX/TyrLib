package com.tyrfing.games.tyrlib3.view.graphics.materials;


import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.view.graphics.Mesh;
import com.tyrfing.games.tyrlib3.view.graphics.Program;
import com.tyrfing.games.tyrlib3.view.graphics.ProgramManager;
import com.tyrfing.games.tyrlib3.view.graphics.TextureManager;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.IBlendable;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;
import com.tyrfing.games.tyrlib3.view.graphics.texture.Texture;
import com.tyrfing.games.tyrlib3.view.graphics.texture.TextureRegion;

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
	private int blendSrc = TyrGL.GL_SRC_ALPHA;
	private int blendDst = TyrGL.GL_ONE;
	
	public ParticleMaterial(String textureName, Color color) {
		program = ProgramManager.getInstance().getProgram("PARTICLE");
		
		this.textureName = textureName;
		this.color = color;
		this.region = new TextureRegion();
				
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		init(0,3, "u_MVPMatrix", "a_Position");
	}
	
	public ParticleMaterial(String textureName, TextureRegion region, Color color) {
		program = ProgramManager.getInstance().getProgram("PARTICLE");
		
		this.textureName = textureName;
		this.color = color;
		this.region = region;
				
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		init(0,3, "u_MVPMatrix", "a_Position");
	}
	
	public void render(Mesh mesh, float[] modelMatrix) {
        super.render(mesh, modelMatrix);
		
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
		    
		    OpenGLRenderer.setTextureFails(OpenGLRenderer.getTextureFails() + 1);
		    
		}
	    
		if (blending) {
			Program.blendEnable(blendSrc , blendDst);
		} else {
			Program.blendDisable();
		}
	}
	
	public void setBlending(boolean blending) {
		this.blending = blending;
	}
	
	public void setBlendModeSrc(int blendSrc) {
		this.blendSrc = blendSrc;
	}
	
	public void setBlendModeDst(int blendDst) {
		this.blendDst = blendDst;
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
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public TextureRegion getRegion() {
		return region;
	}
	
	public void setRegion(TextureRegion region) {
		this.region = region;
	}
}
