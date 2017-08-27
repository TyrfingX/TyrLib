package com.tyrlib2.graphics.rtt;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.compositors.Precision;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.RenderableSceneObject;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.gui.Window;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector2;

public class RenderToTexture {
	
	public final int width, height;
	
	private int[] offscreenBuffer = new int[1];
	private int[] texture = new int[1];
	
	private List<IRenderable> renderables = new ArrayList<IRenderable>();
	private SceneNode root = new SceneNode();
	private Texture tex;
	
	public RenderToTexture(Vector2 size, Precision precision) {
		this.width = (int) size.x;
		this.height = (int) size.y;
		
		TyrGL.glGenFramebuffers(1, offscreenBuffer, 0);
		TyrGL.glGenTextures(1, texture, 0);
		
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
		
        TyrGL.glGenerateMipmap(TyrGL.GL_TEXTURE_2D);
		
		TyrGL.glFramebufferTexture2D(TyrGL.GL_FRAMEBUFFER, TyrGL.GL_COLOR_ATTACHMENT0, TyrGL.GL_TEXTURE_2D, texture[0], 0);
		TyrGL.glCheckFramebufferStatus(TyrGL.GL_FRAMEBUFFER);
		
		TyrGL.glCheckFramebufferStatus(TyrGL.GL_FRAMEBUFFER);
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
	}
	
	public void addWindow(Window window) {
		renderables.add(window);
		root.attachChild(window.getNode());
	}
	
	public void addRenderable(Renderable r) {
		renderables.add(r);
		root.attachSceneObject(r);
	}
	
	public void addRenderableSceneObject(RenderableSceneObject r) {
		renderables.add(r);
		
		if (r.getParent() == null) {
			root.attachSceneObject(r);
		}
	}
	
	public Texture render(String textureName) {
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, offscreenBuffer[0]);
		TyrGL.glViewport(0, 0, width, height);
		
		root.update();
		
		float[] vpMatrix = new float[16];
		Matrix.orthoM(vpMatrix, 0, 0, width, 0, height, -1, 1);
		
		for (IRenderable r : renderables) {
			r.render(vpMatrix);
		}
		
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
		
		if (tex == null) {
			tex = new Texture(texture[0]);
			tex.size = new Vector2(width, height);
			TextureManager.getInstance().addTexture(textureName, tex);
		}
		
		return tex;
	}
	
	public int getTexture() {
		return texture[0];
	}
	
	public SceneNode getNode() {
		return root;
	}
}
