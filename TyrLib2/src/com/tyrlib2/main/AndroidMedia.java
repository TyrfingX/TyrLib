package com.tyrlib2.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.view.Display;

import com.tyrlib2.bitmap.AndroidCanvas;
import com.tyrlib2.bitmap.AndroidDrawableBitmap;
import com.tyrlib2.bitmap.AndroidPaint;
import com.tyrlib2.bitmap.AndroidTypeface;
import com.tyrlib2.bitmap.ICanvas;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.bitmap.IPaint;
import com.tyrlib2.bitmap.ITypeface;
import com.tyrlib2.files.AndroidBitmap;
import com.tyrlib2.files.IBitmap;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.text.GLText;
import com.tyrlib2.graphics.text.IGLText;
import com.tyrlib2.math.Vector2;

public class AndroidMedia extends Media {
	
	private AndroidOpenGLActivity context;
	
	public AndroidMedia(AndroidOpenGLActivity context) {
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
		IGLText glText = new GLText();
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
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_LINEAR_MIPMAP_LINEAR);
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_LINEAR);
	        // Load the bitmap into the bound texture.
	        bitmap.bind();
	        
	        String s = GLES20.glGetString(GLES20.GL_EXTENSIONS);
	        if (s.contains("GL_EXT_texture_filter_anisotropic")) {
	        	float[] maxAni = new float[1];
	    		GLES20.glGetFloatv(0x84FF, maxAni, 0);
	    		GLES20.glTexParameterf(TyrGL.GL_TEXTURE_2D, 0x84FE, maxAni[0]);
	        }
	        
	        TyrGL.glGenerateMipmap(TyrGL.GL_TEXTURE_2D);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	    
		return bitmap;
	}

	@Override
	public ITypeface createFromAsset(String file) {
		return new AndroidTypeface(Typeface.createFromAsset( context.getAssets(), file ));
	}

	@Override
	public IPaint createPaint(ICanvas canvas) {
		return new AndroidPaint();
	}

	@Override
	public IDrawableBitmap createAlphaBitmap(int width, int height) {
		return new AndroidDrawableBitmap(Bitmap.createBitmap( width, height, Bitmap.Config.ALPHA_8 )) ;
	}

	@Override
	public ICanvas createCanvas() {
		return new AndroidCanvas(new Canvas());
	}

	@Override
	public Vector2 getScreenSize() {
		Display d = context.getWindowManager().getDefaultDisplay();
		return new Vector2(d.getWidth(), d.getHeight());
	}
	
	
}
