package com.tyrlib2.bitmap;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.tyrlib2.files.AndroidBitmap;
import com.tyrlib2.files.IBitmap;
import com.tyrlib2.graphics.renderer.TyrGL;

public class AndroidDrawableBitmap implements IDrawableBitmap {
	private Bitmap bitmap;
	
	public AndroidDrawableBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	@Override
	public int getWidth() {
		return bitmap.getWidth();
	}

	@Override
	public int getHeight() {
		return bitmap.getHeight();
	}

	@Override
	public void recycle() {
		bitmap.recycle();
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	@Override
	public void eraseColor(int color) {
		bitmap.eraseColor(color);
	}

	@Override
	public int toTexture() {
	    final int[] textureHandle = new int[1];
		 
	    TyrGL.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	 
	        // Bind to the texture in OpenGL
	    	TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_NEAREST);
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_LINEAR);
	        TyrGL.glTexParameterf(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_WRAP_S, TyrGL.GL_CLAMP_TO_EDGE );  // Set U Wrapping
	        TyrGL.glTexParameterf(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_WRAP_T, TyrGL.GL_CLAMP_TO_EDGE );  // Set V Wrapping

	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(TyrGL.GL_TEXTURE_2D, 0, bitmap, 0);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	 
	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture.");
	    }
	 
	    return textureHandle[0];
	}

	@Override
	public IBitmap toBitmap() {
		return new AndroidBitmap(bitmap);
	}
}
