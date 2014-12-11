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
		
		IBitmap bitmap = Media.CONTEXT.loadBitmap(resourceId, false);

	 
	    if (bitmap.getHandle() == 0)
	    {
	        throw new RuntimeException("Error loading texture " + name + ".");
	    }
	    
	    Texture texture = new Texture(bitmap.getHandle());
	    texture.size = new Vector2(bitmap.getWidth(), bitmap.getHeight());
	    

	    textures.put(name, texture);
	    
	    texture.resId = resourceId;
	 
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
		
		int textureHandle = Media.CONTEXT.loadBitmap(texture.resId, false).getHandle();
	    texture.handle = textureHandle;
	}
	
	public void destroyUnnamedTexture(Texture texture) {
		unnamedTextures.remove(texture);
		int[] textureHandles = new int[1];
		textureHandles[0] = texture.handle;
		TyrGL.glDeleteTextures(1, textureHandles, 0);
	}
	
	public void destroyTexture(Texture texture) {
		textures.remove(texture);
		int[] textureHandles = new int[1];
		textureHandles[0] = texture.handle;
		TyrGL.glDeleteTextures(1, textureHandles, 0);
	}
	
	public Texture createTexture(String name, IBitmap bitmap) {
		 
		Media.CONTEXT.loadBitmap(bitmap);
		
	    if (bitmap.getHandle() == 0)
	    {
	        throw new RuntimeException("Error loading texture " + name + ".");
	    }
	    
	    Texture texture = new Texture(bitmap.getHandle());
	    texture.size = new Vector2(bitmap.getWidth(), bitmap.getHeight());
	    textures.put(name, texture);
	    
	    return texture;
	}
	
}
