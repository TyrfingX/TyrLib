package com.tyrlib2.graphics.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrlib2.files.IBitmap;
import com.tyrlib2.main.Media;
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
		textures.clear();
	}
	
	public Texture createTexture(String name, int resourceId) {
		
		if (textures.get(name) != null) {
			return textures.get(name);
		}
		
	    final int[] textureHandle = new int[1];
	    
	    TyrGL.glGenTextures(1, textureHandle, 0);
	 
	    Vector2 size = null;
	    
	    if (textureHandle[0] != 0)
	    {
	 
	        // Read in the resource
	        final IBitmap bitmap = Media.CONTEXT.loadBitmap(resourceId, false);
	        size = new Vector2(bitmap.getWidth(), bitmap.getHeight());
	        
	        
	        // Bind to the texture in OpenGL
	        TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_LINEAR);
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_LINEAR_MIPMAP_LINEAR);
	 
	        // Load the bitmap into the bound texture.
	        bitmap.bind();
	 
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
	
	public Texture createTexture(String name, IBitmap bitmap) {
	    final int[] textureHandle = new int[1];
	    
	    TyrGL.glGenTextures(1, textureHandle, 0);
	 
	    Vector2 size = null;
	    
	    if (textureHandle[0] != 0)
	    {
	        size = new Vector2(bitmap.getWidth(), bitmap.getHeight());
	        
	        // Bind to the texture in OpenGL
	        TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_LINEAR);
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_LINEAR_MIPMAP_LINEAR);
	 
	        // Load the bitmap into the bound texture.
	        bitmap.bind();
	 
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
	    
	    return texture;
	}
	
	public Texture createTexture(String name, String source) {
		if (textures.get(name) != null) {
			return textures.get(name);
		}
			
		int id = Media.CONTEXT.getResourceID(source, "drawable");
		return createTexture(name, id);
	}
	
	public void addUnnamedTexture(Texture texture) {
		unnamedTextures.add(texture);
	}
	
	public void reloadAll() {
		for (String textureName : textures.keySet()) {
			Texture texture = textures.get(textureName);
			if (texture.resId != -1) {
				reloadTexture(texture);
				
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
				reloadTexture(texture);
			    if (texture.handle == 0)
			    {
			        throw new RuntimeException("Error loading unnamed texture.");
			    }
			} else {
				texture.handle = -1;
			}
		}
	}
	
	private void reloadTexture(Texture texture) {
	    final int[] textureHandle = new int[1];
	    
	    TyrGL.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	 
	        // Read in the resource
	        final IBitmap bitmap = Media.CONTEXT.loadBitmap(texture.resId, false);

	        // Bind to the texture in OpenGL
	        TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MIN_FILTER, TyrGL.GL_LINEAR);
	        TyrGL.glTexParameteri(TyrGL.GL_TEXTURE_2D, TyrGL.GL_TEXTURE_MAG_FILTER, TyrGL.GL_LINEAR_MIPMAP_LINEAR);
	        // Load the bitmap into the bound texture.
	        bitmap.bind();
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	    
	    texture.handle = textureHandle[0];
	}
	
	public void destroyUnnamedTexture(Texture texture) {
		unnamedTextures.remove(texture);
		int[] textureHandles = new int[1];
		textureHandles[0] = texture.handle;
		TyrGL.glDeleteTextures(1, textureHandles, 0);
	}
	
}
