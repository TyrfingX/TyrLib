package com.tyrfing.games.tyrlib3.view.graphics.compositors;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;

public class Compositor {
	
	List<Composit> composits = new ArrayList<Composit>();
	List<Integer> tmpBuffers = new ArrayList<Integer>();
	List<Integer> tmpTextures = new ArrayList<Integer>();
	
	private int[] offscreenBuffer = new int[1];
	
	private int width = SceneManager.getInstance().getViewportWidth();
	private int height = SceneManager.getInstance().getViewportHeight();
	
	/**
	 * 0: Color Texture Multisample resolved
	 * 1: Depth Texture Multisample resolved
	 */
	private int[] texture = new int[2];
	
	private Precision precision;
	
	public Compositor(Precision precision) {
		this.precision = precision;
		
		TyrGL.glGenFramebuffers(1, offscreenBuffer, 0);
		TyrGL.glGenTextures(2, texture, 0);
		
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, offscreenBuffer[0]);
		
		// Color Texture
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, texture[0]);
		
		boolean android = TyrGL.TARGET == TyrGL.ANDROID_TARGET;
		
		Buffer texBuffer = android ? ByteBuffer.allocateDirect(precision.getDataSize() * 3 * width * height).order(ByteOrder.nativeOrder()) : null;
		
		TyrGL.glTexImage2D(	
			TyrGL.GL_TEXTURE_2D, 0, precision.getColorMode(), 
			width,height, 0, precision.getColorMode(), 
			precision.getDataPrecision(), texBuffer
		);
		
		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_WRAP_S, TyrGL.GL_CLAMP_TO_EDGE);
		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_WRAP_T, TyrGL.GL_CLAMP_TO_EDGE);
		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, precision.getTextureMode());
		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, precision.getTextureMode());
		TyrGL.glFramebufferTexture2D(TyrGL.GL_FRAMEBUFFER, TyrGL.GL_COLOR_ATTACHMENT0, TyrGL.GL_TEXTURE_2D, texture[0], 0);
		TyrGL.glCheckFramebufferStatus(TyrGL.GL_FRAMEBUFFER);
		
		// Depth texture
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, texture[1]);
		
		texBuffer = android ? ByteBuffer.allocateDirect(2 * width * height).order(ByteOrder.nativeOrder()).asShortBuffer() : null;
		
		TyrGL.glTexImage2D(	
			TyrGL.GL_TEXTURE_2D, 0, TyrGL.GL_DEPTH_COMPONENT16,
			width,height, 0, TyrGL.GL_DEPTH_COMPONENT, 
			TyrGL.GL_UNSIGNED_SHORT, texBuffer
		);

		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_WRAP_S, TyrGL.GL_CLAMP_TO_EDGE);
		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_WRAP_T, TyrGL.GL_CLAMP_TO_EDGE);
		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, precision.getTextureMode());
		TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, precision.getTextureMode());
		TyrGL.glFramebufferTexture2D(TyrGL.GL_FRAMEBUFFER, TyrGL.GL_DEPTH_ATTACHMENT, TyrGL.GL_TEXTURE_2D, texture[1], 0);
		TyrGL.glCheckFramebufferStatus(TyrGL.GL_FRAMEBUFFER);
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
		TyrGL.glGetError();

	}
	
	public void addComposit(Composit composit) {
		composits.add(composit);
	}
	
	public Precision getPrecision() {
		return precision;
	}
	
	public void bind() {
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, offscreenBuffer[0]);
		TyrGL.glViewport(0, 0, SceneManager.getInstance().getViewportWidth(), SceneManager.getInstance().getViewportHeight());
	}
	
	public void apply() {

		for (int i = 0; i < composits.size(); ++i) {
			Composit composit = composits.get(i);
			if (i < composits.size() -1) {
				composit.setDstBuffer(tmpBuffers.get(i));
			} else {
				composit.setDstBuffer(0);
			}
			
			if (i == 0) {
				composit.apply(texture[0]);
			} else {
				composit.apply(tmpTextures.get(i-1));
			}
			
		}
	}

	public void add(Composit composit) {
		if (composits.size() > 0) {
			int width = SceneManager.getInstance().getViewportWidth();
			int height = SceneManager.getInstance().getViewportHeight();
			
			int[] buffer = new int[1];
			int[] texture = new int[1];
			
			TyrGL.glGenFramebuffers(1, buffer, 0);
			TyrGL.glGenTextures(1, texture, 0);
			
			TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, buffer[0]);
			
			// Color Texture
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, texture[0]);
			TyrGL.glTexImage2D(	
				TyrGL.GL_TEXTURE_2D, 0, TyrGL.GL_RGB, 
				width,height, 0, TyrGL.GL_RGB, 
				precision.getDataPrecision(), null
			);
			TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, precision.getTextureMode());
			TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, precision.getTextureMode());
			TyrGL.glFramebufferTexture2D(TyrGL.GL_FRAMEBUFFER, TyrGL.GL_COLOR_ATTACHMENT0, TyrGL.GL_TEXTURE_2D, texture[0], 0);
	
			tmpBuffers.add(buffer[0]);
			tmpTextures.add(texture[0]);
			
			int error = TyrGL.glGetError();
			if (error != 0) {
				throw new RuntimeException("OpenGL: Failed to create Compositor!");
			}
		}
		
		composit.setDepthTexture(texture[1]);
		composits.add(composit);
	}

	public Composit get(int index) {
		return composits.get(index);
	}
}
