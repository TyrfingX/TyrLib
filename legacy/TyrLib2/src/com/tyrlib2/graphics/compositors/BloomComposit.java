package com.tyrlib2.graphics.compositors;

import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.Media;

public class BloomComposit extends Composit {

	private int bloomLevels;
	
	public BloomComposit(int bloomLevels, int downscale) {
		
		ProgramManager.getInstance().createProgram(
			"BRIGHT_PASS", 
			 Media.CONTEXT.getResourceID("postprocessing_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("bright_pass_fs", "raw"), 
			 new String[]{"a_Position"}
		);
		
		this.bloomLevels = bloomLevels;
		
		String[] shaders = new String[2*bloomLevels+1];
		
		shaders[0] = "BRIGHT_PASS";
		
		for (int i = 1; i < shaders.length-1; ++i) {
			if (i % 2 == 1) {
				shaders[i] = "GAUSS_X";
			} else {
				shaders[i] = "GAUSS_Y";
			}
		}
		
		shaders[shaders.length-1] = "BLIT";

		// Main Buffer
		// BrightPass Buffer
		// Gaussian BufferX_1, BufferY_1
		// ... 
		// Gaussian BufferX_bloomlevels-1, BufferY_bloomlevels-1
		// Target Buffer
		
		int countBuffers = bloomLevels*2+2;
		init(new int[countBuffers], new int[countBuffers], new int[countBuffers], new int[countBuffers], shaders, "bgl_RenderedTexture");
		
		TyrGL.glGenFramebuffers(countBuffers, buffers, 0);
		TyrGL.glGenTextures(countBuffers, colorTextures, 0);

		int width = SceneManager.getInstance().getViewportWidth() / downscale;
		int height = SceneManager.getInstance().getViewportHeight() / downscale;
		
		int buffer = 0;
		
		for (int i = 0; i < bloomLevels+1; ++i) {
			
			for (int j = 0; j < 2; ++j) {

				setupBufferWithTexture(width, height, buffer);
	
				buffer++;
				
				if (buffer == 1) {
					break;
				}
			}
			
			if (buffer != 1) {
				width /= 2;
				height /= 2;
			}
		}
		
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
		
		int error = TyrGL.glGetError();
		if (error != 0) {
			throw new RuntimeException("OpenGL: Failed to create Bloom Compositor!");
		}
	}
	
	public int getBloomLevels() {
		return bloomLevels;
	}

	@Override
	public void compose(int srcTexture) {
		TyrGL.glUniform1i(uTextureHandle[shaders.length-1], 0);
		Program.blendDisable();
		
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, srcTexture);
		quad.render();
		
		Program.blendEnable(TyrGL.GL_ONE, TyrGL.GL_ONE_MINUS_SRC_COLOR);
		
		for (int i = 2; i < buffers.length-1; ++i) {
			if (i % 2 == 0) {
				TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
				TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, colorTextures[i]);
				
				quad.render();
			}
		}
		
		Program.blendDisable();
	}

}
