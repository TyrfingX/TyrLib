package com.tyrlib2.graphics.compositors;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;

public abstract class Composit {
	
	protected int[] colorTextures;
	protected int[] buffers;
	protected String[] shadersStr;
	protected Program[] shaders;
	protected String uTextureParam;
	protected int[] uTextureHandle;
	
	protected FullQuad quad = new FullQuad();
	protected int[] widths;
	protected int[] heights;
	protected int depthTexture;
	
	public Composit() {
		
	}
	
	/**
	 * Executes a series of render passes. Source image is in texture[0] in framebuffer
	 * buffers[0]. Rendering happens as follows:
	 * buffers[0] -> render using shaders[0] to buffers[1]
	 * Continue.
	 * @param textures Textures containing the inbetween renders
	 * @param buffers Intermediate buffers used to store the inbetween generated textures
	 * @param shaders Shaders used for the rendering
	 * @param uTextureParam Name of the texture parameter in all shaders[0]...shaders[n-1]
	 */
	
	protected void init(int[] textures, int[] buffers, int[] widths, int[] heights, String[] shaders, String uTextureParam) {
		this.colorTextures = textures;
		this.buffers = buffers;
		this.widths = widths;
		this.heights = heights;
		this.shadersStr = shaders;
		this.uTextureParam = uTextureParam;
		
		this.shaders = new Program[shaders.length];
		this.uTextureHandle = new int[shaders.length];
		
		initShaders();
	}
	
	public void initShaders(){
		for (int i = 0; i < shaders.length; ++i) {
			this.shaders[i] = ProgramManager.getInstance().getProgram(shadersStr[i]);
			this.uTextureHandle[i] = TyrGL.glGetUniformLocation(this.shaders[i].handle, uTextureParam);
		}
	}

	public void setDepthTexture(int depthTexture) {
		this.depthTexture = depthTexture;
	}
	
	public void apply(int srcTexture) {
		
		Program.blendDisable();
		
		quad.positionHandle = TyrGL.glGetAttribLocation(shaders[0].handle, "a_Position");
		
		quad.bind();
		
		for (int i = 0; i < buffers.length-1; ++i) {
			TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, buffers[i]);
			TyrGL.glViewport(0, 0,widths[i],heights[i]);
			
			// Draw from buffer i-1 to buffer i using shader[i]
			
			shaders[i].use();
			
			bindTexture(i, srcTexture);
			
			quad.render();
		}
		
		int dstBuffer = getDstBuffer();
		
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, dstBuffer);
		TyrGL.glViewport(0, 0, SceneManager.getInstance().getViewportWidth(), SceneManager.getInstance().getViewportHeight());
		
		shaders[shaders.length-1].use();
		quad.bind();
		
		// Accumulate all composites into main target
		
		compose(srcTexture);
		
		quad.unbind();
		
	}
	
	protected void bindTexture(int i, int srcTexture) {
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
		if (i-1 >= 0) {
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, colorTextures[i-1]);
		} else {
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, srcTexture);
		}
		TyrGL.glUniform1i(uTextureHandle[i], 0);
	}
	
	public void setDstBuffer(int buffer) {
		buffers[buffers.length-1] = buffer;
	}
	
	public int getDstBuffer() {
		return buffers[buffers.length-1];
	}
	
	public abstract void compose(int srcTexture);
	
	protected void setupBufferWithTexture(int width, int height, int index) {
		widths[index] = width;
		heights[index] = height;
		
		Precision precision = SceneManager.getInstance().getRenderer().getCompositor().getPrecision();
		
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, buffers[index]);
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, colorTextures[index]);
		
		boolean android = TyrGL.TARGET == TyrGL.ANDROID_TARGET;
		Buffer texBuffer = android ? ByteBuffer.allocateDirect(precision.getDataSize() * 3 * width * height).order(ByteOrder.nativeOrder()) : null;
		
		TyrGL.glTexImage2D(	
			TyrGL.GL_TEXTURE_2D, 0, precision.getColorMode(), 
			width,height, 0, precision.getColorMode(), 
			precision.getDataPrecision(), texBuffer
		);

		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, precision.getTextureMode());
		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, precision.getTextureMode());
		TyrGL.glFramebufferTexture2D(TyrGL.GL_FRAMEBUFFER, TyrGL.GL_COLOR_ATTACHMENT0, TyrGL.GL_TEXTURE_2D, colorTextures[index], 0);
	}
}
