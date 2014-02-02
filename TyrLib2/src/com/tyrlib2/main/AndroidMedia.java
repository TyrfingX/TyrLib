package com.tyrlib2.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.tyrlib2.files.AndroidBitmap;
import com.tyrlib2.files.IBitmap;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.text.GLText;
import com.tyrlib2.graphics.text.IGLText;

public class AndroidMedia extends Media {
	
	private Context context;
	
	public AndroidMedia(Context context) {
		this.context = context;
	}
	
	@Override
	public InputStream openAsset(String fileName) throws IOException {
		return context.getResources().getAssets().open(fileName);
	}

	@Override
	public FileInputStream openFileInput(String fileName) throws IOException {
		return context.openFileInput(fileName);
	}

	@Override
	public InputStream openRawResource(int id) throws IOException {
		return context.getResources().openRawResource(id);
	}
	
	@Override
	public int getResourceID(String source, String resType) {
		return context.getResources().getIdentifier(source, resType, context.getPackageName());
	}

	@Override
	public IGLText createTextRenderer(String fontSource, int size) {
		IGLText glText = new GLText(context.getAssets());
		glText.load( fontSource, size, 2, 2 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
		glText.setScale(1);
		return glText;
	}

	@Override
	public IBitmap loadBitmap(int resID, boolean prescaling) {
		
		IBitmap bitmap = null;
		
	    final int[] textureHandle = new int[1];
	    
	    TyrGL.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	    	
	        // Read in the resource        
	    	final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = prescaling;   // No pre-scaling
			bitmap  = new AndroidBitmap(BitmapFactory.decodeResource(context.getResources(), resID, options), textureHandle[0]);

	        // Bind to the texture in OpenGL
	        TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_LINEAR);
	        //TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_LINEAR_MIPMAP_LINEAR);
	        // Load the bitmap into the bound texture.
	        bitmap.bind();
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	    
		return bitmap;
	}
	
	
}
