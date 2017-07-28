package com.tyrlib2.graphics.compositors;

import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.util.Color;

public class SSAOComposit extends Composit {

	private Texture randomTexture;
	private int uRandomTextureHandle;
	private int uSSAOTextureHandle;
	private int uAmbientHandle;
	private Color ambient;
	
	public SSAOComposit(Color ambient) {
		
		this.ambient = ambient;
		
		ProgramManager.getInstance().createProgram(
			"SSAO", 
			 Media.CONTEXT.getResourceID("postprocessing_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("ssao_fs", "raw"), 
			 new String[]{"a_Position"}
		);
		
		ProgramManager.getInstance().createProgram(
			"SSAO_ADD", 
			 Media.CONTEXT.getResourceID("postprocessing_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("ssao_add_fs", "raw"), 
			 new String[]{"a_Position"}
		);
		
		randomTexture = TextureManager.getInstance().createTexture("NOISE", Media.CONTEXT.getResourceID("noise", "drawable"));
		
		String[] shaders = { "SSAO", "GAUSS_X", "GAUSS_Y", "SSAO_ADD" };
		
		int countBuffers = 4;
		init(new int[countBuffers], new int[countBuffers], new int[countBuffers], new int[countBuffers], shaders, "bgl_RenderedTexture");
		
		uRandomTextureHandle = TyrGL.glGetUniformLocation(this.shaders[0].handle, "bgl_RandomTexture");
		uSSAOTextureHandle = TyrGL.glGetUniformLocation(this.shaders[3].handle, "bgl_SSAOTexture");
		uAmbientHandle = TyrGL.glGetUniformLocation(this.shaders[3].handle, "u_Ambient");
		
		widths[0] = SceneManager.getInstance().getViewportWidth()/2;
		heights[0] = SceneManager.getInstance().getViewportHeight()/2;

		TyrGL.glGenFramebuffers(3, buffers, 0);
		TyrGL.glGenTextures(3, colorTextures, 0);
		
		setupBufferWithTexture(widths[0], heights[0], 0);
		setupBufferWithTexture(widths[0] / 2, heights[0] / 2, 1);
		setupBufferWithTexture(widths[0] / 2, heights[0] / 2, 2);
		
	}
	
	@Override
	public void bindTexture(int i, int srcTexture) {
		if (i-1 >= 0 ) {
			super.bindTexture(i, srcTexture); 
		} else{
			TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, depthTexture);
			TyrGL.glUniform1i(uTextureHandle[i], 0);
			TyrGL.glActiveTexture(TyrGL.GL_TEXTURE1);
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, randomTexture.getHandle());
			TyrGL.glUniform1i(uRandomTextureHandle, 1);
		}
	}
	
	@Override
	public void compose(int srcTexture) {
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, srcTexture);
		TyrGL.glUniform1i(uTextureHandle[shaders.length-1], 0);
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE1);
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, colorTextures[2]);
		TyrGL.glUniform1i(uSSAOTextureHandle, 1);
		
		TyrGL.glUniform3f(uAmbientHandle, ambient.r, ambient.g, ambient.b);
		quad.render();
	}
}
