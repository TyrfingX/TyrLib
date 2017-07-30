package com.tyrfing.games.tyrlib3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.tyrfing.games.tyrlib3.Media;
import com.tyrfing.games.tyrlib3.model.files.AndroidBitmap;
import com.tyrfing.games.tyrlib3.model.files.AndroidFileReader;
import com.tyrfing.games.tyrlib3.model.files.IBitmap;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.model.sound.AndroidMusic;
import com.tyrfing.games.tyrlib3.model.sound.AndroidSound;
import com.tyrfing.games.tyrlib3.model.sound.IMusic;
import com.tyrfing.games.tyrlib3.model.sound.ISound;
import com.tyrfing.games.tyrlib3.view.bitmap.AndroidCanvas;
import com.tyrfing.games.tyrlib3.view.bitmap.AndroidDrawableBitmap;
import com.tyrfing.games.tyrlib3.view.bitmap.AndroidPaint;
import com.tyrfing.games.tyrlib3.view.bitmap.AndroidTypeface;
import com.tyrfing.games.tyrlib3.view.bitmap.ICanvas;
import com.tyrfing.games.tyrlib3.view.bitmap.IDrawableBitmap;
import com.tyrfing.games.tyrlib3.view.bitmap.IPaint;
import com.tyrfing.games.tyrlib3.view.bitmap.ITypeface;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.compositors.Precision;
import com.tyrfing.games.tyrlib3.view.graphics.text.GLText;
import com.tyrfing.games.tyrlib3.view.graphics.text.IGLText;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.view.Display;
import android.view.WindowManager;

public class AndroidMedia extends Media {
	
	private AndroidOpenGLActivity context;
	private Map<String, String> resourceEndings = new HashMap<String, String>();
	
	public AndroidMedia(AndroidOpenGLActivity context) {
		this.context = context;
		listAssetFiles("sound");
		listAssetFiles("music");
		
		options.put(Media.PRECISION, Precision.LOW);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			options.put(Media.DEPTH_TEXTURES_ENABLED, true);
		} else {
			options.put(Media.DEPTH_TEXTURES_ENABLED, false);
		}
	}
	
	private boolean listAssetFiles(String path) {
	    String [] list;
	    try {
	        list = context.getAssets().list(path);
	        if (list.length > 0) {
	            // This is a folder
	            for (String file : list) {
	                if (!listAssetFiles(path + "/" + file))
	                    return false;
	            }
	        } else {
	            // This is a file
	        	String[] splitPath = path.split("/");
	        	path = splitPath[splitPath.length-1];
				String[] split = path.split("\\.");
				
				if (split.length > 1) {
					resourceEndings.put(split[0], split[1]) ;
				} 
	        }
	    } catch (IOException e) {
	        return false;
	    }

	    return true; 
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
		glText.load( fontSource, size, 0, 0 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
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
		
        // Read in the resource        
    	final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = prescaling;   // No pre-scaling
		return new AndroidBitmap(BitmapFactory.decodeResource(context.getResources(), resID, options));
		
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
	public Vector2F getScreenSize() {
		Display d = context.getWindowManager().getDefaultDisplay();
		Point outSize = new Point();
		d.getSize(outSize);
		return new Vector2F(outSize.x, outSize.y);
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
	        
	        ((AndroidBitmap)bitmap).setHandle(textureHandle[0]);
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
 	   context.close();
	}

	@Override
	public boolean fileExists(String target, String fileName) {
		return AndroidFileReader.fileExists(context, fileName);
	}

	@Override
	public ISound createSound(String res) {
		try {
	        AssetFileDescriptor afd = context.getAssets().openFd("sound/" + res + "." + resourceEndings.get(res));
	        
	        SoundPool sp = new SoundPool(5,AudioManager.STREAM_MUSIC,0);
	        int soundID = sp.load(afd, 1);
	        return new AndroidSound(sp, soundID);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public IMusic createMusic(String res) {
		try {
	        AssetFileDescriptor afd = context.getAssets().openFd("music/" + res + "." + resourceEndings.get(res));
	        MediaPlayer mp = new MediaPlayer();
	        return new AndroidMusic(mp, afd);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
