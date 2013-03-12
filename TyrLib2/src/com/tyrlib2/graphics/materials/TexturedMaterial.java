package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.graphics.renderer.IBlendable;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;

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
	
	public TexturedMaterial() {
		
	}
	
	public TexturedMaterial(Texture texture, Program program) {
		this.texture = texture;
		this.program = program;
		init(5,0,3, "u_MVPMatrix", "a_Position");
	}
	
	public TexturedMaterial(Texture texture) {
		this(texture, ProgramManager.getInstance().getProgram("TEXTURED"));
	}
	
	
	public TexturedMaterial(String textureName) {
		this(TextureManager.getInstance().getTexture(textureName));
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
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
        super.render(vertexBuffer, modelMatrix);
        
		int alphaHandle = GLES20.glGetUniformLocation(program.handle, "u_Alpha");
		GLES20.glUniform1f(alphaHandle, alpha);
		
		int textureCoordinateHandle = GLES20.glGetAttribLocation(program.handle, "a_TexCoordinate");
		
        // Pass in the texture coordinate information
        vertexBuffer.position(uvOffset);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, uvDataSize, GLES20.GL_FLOAT, false, 
        		strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
        
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
		
		int textureHandle = texture.getHandle();
		int textureUniformHandle = GLES20.glGetUniformLocation(program.handle, "u_Texture");
		
	    // Set the active texture unit to texture unit 0.
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	 
	    // Bind the texture to this unit.
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
	    GLES20.glUniform1i(textureUniformHandle, 0);
	    

	    GLES20.glEnable( GLES20.GL_BLEND );
	    GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );

	}
	
	public void setProgram(Program program) {
		this.program = program;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

}
