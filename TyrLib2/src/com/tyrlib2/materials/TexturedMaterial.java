package com.tyrlib2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.OpenGLRenderer;
import com.tyrlib2.renderer.ProgramManager;
import com.tyrlib2.renderer.Texture;
import com.tyrlib2.renderer.TextureManager;

/**
 * A material which only supports texturing
 * @author Sascha
 *
 */

public class TexturedMaterial extends Material implements IBlendableMaterial  {

	private float alpha;
	private String textureName;
	private Texture texture;
	private int uvDataSize = 2;
	private int uvOffset = 3;
	
	public TexturedMaterial(String textureName) {
		this.textureName = textureName;
		this.texture = TextureManager.getInstance().getTexture(textureName);
		
		program = ProgramManager.getInstance().getProgram("TEXTURED");
		
		init(5,0,3, "u_MVPMatrix", "a_Position");
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
	    GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_SRC_ALPHA);
	    
	}

}
