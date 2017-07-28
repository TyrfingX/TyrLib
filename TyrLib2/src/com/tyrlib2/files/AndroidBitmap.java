package com.tyrlib2.files;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.tyrlib2.graphics.renderer.TyrGL;

public class AndroidBitmap implements IBitmap {
	
	private Bitmap bitmap;
	private int handle;
	private int[] pixels;
	
	private int width;
	private int height;

	public AndroidBitmap(Bitmap bitmap, int handle) {
		this.bitmap = bitmap;
		this.handle = handle;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}
	
	public AndroidBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}
	
	private void loadPixels() {
		pixels = new int[width*height];
		bitmap.getPixels(pixels,0,width,0,0,width,height);
	}
	
	public void toTexture() {
	    final int[] textureHandle = new int[1];
	    
	    TyrGL.glGenTextures(1, textureHandle, 0);
	    handle = textureHandle[0];
	    
	    if (handle != 0)
	    {
	        // Bind to the texture in OpenGL
	        TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, handle);
	        
	        // Set filtering
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_LINEAR_MIPMAP_LINEAR);
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_LINEAR);
	        // Load the bitmap into the bound texture.
	        bind();
	        
	        String s = GLES20.glGetString(GLES20.GL_EXTENSIONS);
	        if (s.contains("GL_EXT_texture_filter_anisotropic")) {
	        	float[] maxAni = new float[1];
	    		GLES20.glGetFloatv(0x84FF, maxAni, 0);
	    		GLES20.glTexParameterf(TyrGL.GL_TEXTURE_2D, 0x84FE, maxAni[0]);
	        }
	        
	        TyrGL.glGenerateMipmap(TyrGL.GL_TEXTURE_2D);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        recycle();
	    }
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void bind() {
		GLUtils.texImage2D(TyrGL.GL_TEXTURE_2D, 0, bitmap, 0);
	}

	@Override
	public void recycle() {
		bitmap.recycle();
	}

	@Override
	public int getHandle() {
		return handle;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setHandle(int handle) {
		this.handle = handle;
	}

	@Override
	public int getRGB(int x, int y) {
		if (pixels == null) {
			loadPixels();
		}
		return pixels[x+y*width];
	}
}
