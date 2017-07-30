package com.tyrfing.games.tyrlib3.pc.model.files;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL3;
import javax.media.opengl.GL3bc;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.tyrfing.games.tyrlib3.model.files.IBitmap;
import com.tyrfing.games.tyrlib3.pc.view.graphics.renderer.PCGL3;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;


public class PCBitmap implements IBitmap {

	private Texture text;
	private BufferedImage tBufferedImage;
	
	public static final Map<Integer, Texture> textures = new HashMap<Integer, Texture>();
	
	public PCBitmap(String path, boolean staticBitmap) {
		try {
			tBufferedImage = ImageIO.read(new File(path));
			if (!staticBitmap) toTexture();
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PCBitmap(String path) {
		try {
			tBufferedImage = ImageIO.read(new File(path));
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PCBitmap(BufferedImage tBufferedImage) {
		toTexture(tBufferedImage);
	}

	@Override
	public void toTexture() {
		toTexture(tBufferedImage);
	}
	
	public void toTexture(BufferedImage tBufferedImage) {
		text = AWTTextureIO.newTexture(GLProfile.getDefault(), tBufferedImage, true); 
		text.setTexParameteri(PCGL3.gl, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_LINEAR_MIPMAP_LINEAR);
		text.setTexParameteri(PCGL3.gl, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_LINEAR);
		text.setTexParameterf(PCGL3.gl, TyrGL.GL_TEXTURE_WRAP_S, GL3bc.GL_REPEAT);  // Set U Wrapping
		text.setTexParameterf(PCGL3.gl, TyrGL.GL_TEXTURE_WRAP_T, GL3bc.GL_REPEAT );  // Set V Wrapping
		
		textures.put(text.getTextureObject(), text);
		
        bind();
        
        float[] maxAni = new float[1];
		PCGL3.gl.glGetFloatv(GL3.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAni, 0);
		text.setTexParameterf(PCGL3.gl, GL3.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAni[0]);
        
        TyrGL.glGenerateMipmap(TyrGL.GL_TEXTURE_2D);
	}
	
	@Override
	public int getWidth() {
		if (tBufferedImage != null) {
			return tBufferedImage.getWidth();
		} else {
			return text.getWidth();
		}
	}

	@Override
	public int getHeight() {
		if (tBufferedImage != null) {
			return tBufferedImage.getHeight();
		} else {
			return text.getHeight();
		}
	}
	
	@Override
	public int getRGB(int x, int y) {
		return tBufferedImage.getRGB(x, y);
	}

	@Override
	public void bind() {
		text.bind(PCGL3.gl);
	}

	@Override
	public void recycle() {
		text.destroy(PCGL3.gl);
	}
	
	public int getHandle() {
		return text.getTextureObject();
	}
	
	public static void bind(int handle) {
		textures.get(handle).bind(PCGL3.gl);
	}

}
