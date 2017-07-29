package com.tyrfing.games.tyrlib3.view.graphics.compositors;

import com.tyrfing.games.tyrlib3.main.Media;
import com.tyrfing.games.tyrlib3.view.graphics.ProgramManager;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;

public class DeferredLighting extends Composit {

	private int depthTextureHandle;
	
	public DeferredLighting() {
		ProgramManager.getInstance().createProgram(
			"DEFERRED", 
			 Media.CONTEXT.getResourceID("postprocessing_vs", "raw"), 
		  	 Media.CONTEXT.getResourceID("deferred_fs", "raw"), 
			 new String[]{"a_Position"}
		);
		
		int countBuffers = 1;
		
		String[] shaders = { "DEFERRED" };
		
		init(new int[countBuffers], new int[countBuffers], new int[countBuffers], new int[countBuffers], shaders, "bgl_RenderedTexture");
	
		depthTextureHandle = TyrGL.glGetUniformLocation(this.shaders[0].handle, "bgl_DepthTexture");
	
		widths[0] = SceneManager.getInstance().getViewportWidth();
		heights[0] = SceneManager.getInstance().getViewportHeight();
	}
	
	@Override
	public void compose(int srcTexture) {
		
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, depthTexture);
		TyrGL.glUniform1i(depthTextureHandle, 0);
		
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE1);
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, srcTexture);
		TyrGL.glUniform1i(uTextureHandle[shaders.length-1], 1);
		
		quad.render();
	}

}
