package com.tyrlib2.graphics.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.tyrlib2.math.Vector2;

public class TextureManager {
	private static TextureManager instance;
	
	private Map<String, Texture> textures;
	private List<Texture> unnamedTextures;
	
	public TextureManager() {
		textures = new HashMap<String, Texture>();
		unnamedTextures = new ArrayList<Texture>();
	}
	
	public static TextureManager getInstance() {
		if (instance == null) {
			instance = new TextureManager();
		}
	
		return instance;
	}
	
	public Texture getTexture(String name) {
		return textures.get(name);
	}
	
	public void destroy() {
		instance = null;
	}
	
	public Texture createTexture(String name, Context context, int resourceId) {
	    final int[] textureHandle = new int[1];
	    
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    Vector2 size = null;
	    
	    if (textureHandle[0] != 0)
	    {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	 
	        // Read in the resource
	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
	        size = new Vector2(bitmap.getWidth(), bitmap.getHeight());
	        
	        
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	 
	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	 
	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture " + name + ".");
	    }
	    
	    Texture texture = new Texture(textureHandle[0]);
	    texture.size = size;
	    textures.put(name, texture);
	    texture.resId = resourceId;
	 
	    return texture;
	}
	
	public void addUnnamedTexture(Texture texture) {
		unnamedTextures.add(texture);
	}
	
	public void reloadAll(Context context) {
		for (String textureName : textures.keySet()) {
			Texture texture = textures.get(textureName);
			if (texture.resId != -1) {
				reloadTexture(texture, context);
				
			    if (texture.handle == 0)
			    {
			        throw new RuntimeException("Error loading texture " + textureName + ".");
			    }
			} else {
				texture.handle = -1;
			}
		}
		
		for (Texture texture : unnamedTextures) {
			if (texture.resId != -1) {
				reloadTexture(texture, context);
			    if (texture.handle == 0)
			    {
			        throw new RuntimeException("Error loading unnamed texture.");
			    }
			} else {
				texture.handle = -1;
			}
		}
	}
	
	private void reloadTexture(Texture texture, Context context) {
	    final int[] textureHandle = new int[1];
	    
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	 
	        // Read in the resource
	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), texture.resId, options);
	 
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	 
	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	    
	    texture.handle = textureHandle[0];
	}
	
	public void destroyUnnamedTexture(Texture texture) {
		unnamedTextures.remove(texture);
		int[] textureHandles = new int[1];
		textureHandles[0] = texture.handle;
		GLES20.glDeleteTextures(1, textureHandles, 0);
	}
	
}
