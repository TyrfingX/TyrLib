package com.tyrlib2.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.view.Display;
import android.view.WindowManager;

import com.tyrlib2.bitmap.AndroidCanvas;
import com.tyrlib2.bitmap.AndroidDrawableBitmap;
import com.tyrlib2.bitmap.AndroidPaint;
import com.tyrlib2.bitmap.AndroidTypeface;
import com.tyrlib2.bitmap.ICanvas;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.bitmap.IPaint;
import com.tyrlib2.bitmap.ITypeface;
import com.tyrlib2.files.AndroidBitmap;
import com.tyrlib2.files.FileReader;
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
	public IBitmap loadStaticBitmap(int resID, boolean prescaling) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = prescaling;   // No pre-scaling
		return new AndroidBitmap(BitmapFactory.decodeResource(context.getResources(), resID, options));
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

	@Override
	public IDrawableBitmap createBitmap(int width, int height) {
		return new AndroidDrawableBitmap(Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 ));
	}

	@Override
	public ICanvas createCanvas(IDrawableBitmap bitmap) {
		AndroidDrawableBitmap b = (AndroidDrawableBitmap) bitmap;
		return new AndroidCanvas(new Canvas(b.getBitmap()));
	}

	@Override
	public void loadBitmap(IBitmap bitmap) {
	    final int[] textureHandle = new int[1];
	    
	    TyrGL.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
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
	        
	        ((AndroidBitmap)bitmap).setHanlde(textureHandle[0]);
	    }
	}

	@Override
	public void serializeTo(Serializable s, String target, String fileName) {
		try {
			FileOutputStream fileOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(s);
			out.close();
			fileOut.close();
			System.out.printf("Saved successful to " + target);
		}catch(IOException i) {
			i.printStackTrace();
		}
	}

	@Override
	public Object deserializeFrom(String target, String fileName) {
		InputStream fis = null;
		Object result = null;
		
		try
		{
		  
		  fis = Media.CONTEXT.openFileInput(fileName);

		  ObjectInputStream o = new ObjectInputStream( fis );
		  result = o.readObject();
		  o.close();
		  
		}
		catch ( IOException e ) { e.printStackTrace(); }
		catch ( ClassNotFoundException e ) { e.printStackTrace(); }
		finally { try { fis.close(); } catch ( Exception e ) { e.printStackTrace(); } }
		
		return result;
	}
	
	public void showKeyboard() {
		OpenGLSurfaceView.instance.post(new Runnable() {
			@Override
			public void run() {

				context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				OpenGLSurfaceView.instance.imm.showSoftInput(OpenGLSurfaceView.instance,0);
			}
		});
	}
	
	public void hideKeyboard() {
		OpenGLSurfaceView.instance.post(new Runnable() {
			@Override
			public void run() {
				context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				OpenGLSurfaceView.instance.imm.hideSoftInputFromWindow(OpenGLSurfaceView.instance.getWindowToken(),0);
			}
		});
	}

	@Override
	public String getClipboard() {
		return "";
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean fileExists(String target, String fileName) {
		return FileReader.fileExists(context, fileName);
	}
	
	
}
