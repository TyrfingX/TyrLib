package com.tyrlib2.graphics.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.tyrlib2.graphics.renderer.TyrGL;

public class TextureHelper {
	public static int loadTexture(final Context context, final int resourceId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false; // No pre-scaling
		final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		
		return loadTexture(bitmap);
	}
	public static int loadTexture(Bitmap bitmap)
	{
	    final int[] textureHandle = new int[1];
	 
	    TyrGL.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
//	        final BitmapFactory.Options options = new BitmapFactory.Options();
//	        options.inScaled = false;   // No pre-scaling
	 
	        // Read in the resource
//	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
	 
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
}
