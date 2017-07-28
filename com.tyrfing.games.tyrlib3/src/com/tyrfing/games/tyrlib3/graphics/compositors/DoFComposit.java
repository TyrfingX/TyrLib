package com.tyrfing.games.tyrlib3.graphics.compositors;

import com.tyrfing.games.tyrlib3.graphics.renderer.ProgramManager;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.main.Media;

public class DoFComposit extends Composit {

	private int depthTextureHandle;
	private int textureWidthHandle;
	private int textureHeightHandle;
	
	public DoFComposit() {
		ProgramManager.getInstance().createProgram(
			"DOF", 
			 Media.CONTEXT.getResourceID("postprocessing_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("dof_fs", "raw"), 
			 new String[]{"a_Position"}
		);
		
		int countBuffers = 1;
		
		String[] shaders = { "DOF" };
		
		init(new int[countBuffers], new int[countBuffers], new int[countBuffers], new int[countBuffers], shaders, "bgl_RenderedTexture");
	
		depthTextureHandle = TyrGL.glGetUniformLocation(this.shaders[0].handle, "bgl_DepthTexture");
		textureWidthHandle = TyrGL.glGetUniformLocation(this.shaders[0].handle, "bgl_RenderedTextureWidth");
		textureHeightHandle = TyrGL.glGetUniformLocation(this.shaders[0].handle, "bgl_RenderedTextureHeight");
	
		widths[0] = SceneManager.getInstance().getViewportWidth();
		heights[0] = SceneManager.getInstance().getViewportHeight();
	}
	
	@Override
	public void compose(int srcTexture) {
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, srcTexture);
		TyrGL.glUniform1i(uTextureHandle[shaders.length-1], 0);
		
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE1);
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, depthTexture);
		TyrGL.glUniform1i(depthTextureHandle, 1);
		
		TyrGL.glUniform1f(textureWidthHandle, widths[0]);
		TyrGL.glUniform1f(textureHeightHandle, heights[0]);
		
		quad.render();
	}

}
