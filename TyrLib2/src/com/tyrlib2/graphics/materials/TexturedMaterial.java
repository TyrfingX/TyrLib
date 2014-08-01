package com.tyrlib2.graphics.materials;


import com.tyrlib2.graphics.renderer.IBlendable;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * A material which only supports texturing
 * @author Sascha
 *
 */

public class TexturedMaterial extends Material implements IBlendable  {

	private float alpha = 1;
	private String textureName;
	private Texture texture;
	private int uvDataSize = 2;
	private int uvOffset = 3;
	private Color color = Color.WHITE.copy();
	private TextureRegion texRegion;
	private int sizeHandle;
	private int minHandle;
	
	int alphaHandle;
	int textureCoordinateHandle;
	int colorHandle;
	int textureUniformHandle;
	
	public TexturedMaterial(Texture texture, TextureRegion texRegion, Program program) {
		this.texture = texture;
		this.program = program;
		this.texRegion = texRegion;
		init(5,0,3, "u_MVPMatrix", "a_Position");
		
		alphaHandle = TyrGL.glGetUniformLocation(program.handle, "u_Alpha");
		textureCoordinateHandle = TyrGL.glGetAttribLocation(program.handle, "a_TexCoordinate");
		colorHandle = TyrGL.glGetUniformLocation(program.handle, "u_Color");
		textureUniformHandle = TyrGL.glGetUniformLocation(program.handle, "u_Texture");
		minHandle = TyrGL.glGetUniformLocation(program.handle, "u_Min");
		sizeHandle = TyrGL.glGetUniformLocation(program.handle, "u_Size");
	}
	
	public TexturedMaterial(Texture texture, TextureRegion texRegion) {
		this(texture, texRegion, ProgramManager.getInstance().getProgram("TEXTURED"));
	}
	
	
	public TexturedMaterial(String textureName, TextureRegion texRegion) {
		this(TextureManager.getInstance().getTexture(textureName), texRegion);
		this.textureName = textureName;
	}
	
	@Override
	public float getAlpha() {
		return alpha;
	}

	@Override
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	public void render(Mesh mesh, float[] modelMatrix) {
        
		TyrGL.glUniform1f(alphaHandle, alpha);
		TyrGL.glUniform3f(colorHandle, color.r, color.g, color.b);
		
		TyrGL.glUniform2f(minHandle, texRegion.u1, texRegion.v1);
		TyrGL.glUniform2f(sizeHandle, texRegion.u2 - texRegion.u1, texRegion.v2 - texRegion.v1);
		
		if (TyrGL.GL_USE_VBO == 1) {
	        // Pass in the texture coordinate information
	        TyrGL.glVertexAttribPointer(textureCoordinateHandle, uvDataSize, TyrGL.GL_FLOAT, false, 
	        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, uvOffset * OpenGLRenderer.BYTES_PER_FLOAT);
		} else {
	        // Pass in the texture coordinate information
	        mesh.getVertexBuffer().position(uvOffset);
	        TyrGL.glVertexAttribPointer(textureCoordinateHandle, uvDataSize, TyrGL.GL_FLOAT, false, 
	        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, mesh.getVertexBuffer());
	        TyrGL.glEnableVertexAttribArray(textureCoordinateHandle);
		}
		
		int textureHandle = texture.getHandle();
		
		if (program.textureHandle != textureHandle) {
			
		    // Set the active texture unit to texture unit 0.
			TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
		 
		    // Bind the texture to this unit.
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle);
		 
		    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
			TyrGL.glUniform1i(textureUniformHandle, 0);
		    
		    program.textureHandle = textureHandle;
		    
		    OpenGLRenderer.setTextureFails(OpenGLRenderer.getTextureFails() + 1);
		}
	    
		Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void setProgram(Program program) {
		this.program = program;
	}
	
	public void setTexture(Texture texture, TextureRegion texRegion) {
		this.texture = texture;
		this.texRegion = texRegion;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public TextureRegion getTextureRegion() {
		return texRegion;
	}

}
