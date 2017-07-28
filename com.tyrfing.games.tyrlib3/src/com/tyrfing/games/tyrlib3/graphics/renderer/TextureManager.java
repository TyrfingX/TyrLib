package com.tyrfing.games.tyrlib3.graphics.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrfing.games.tyrlib3.files.IBitmap;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.main.BackgroundWorker;
import com.tyrfing.games.tyrlib3.main.Media;
import com.tyrfing.games.tyrlib3.math.Vector2F;

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
		bitmap.toTexture();
	 
	    if (bitmap.getHandle() == 0)
	    {
	        throw new RuntimeException("Error loading texture " + name + ".");
	    }
	    
	    Texture texture = new Texture(bitmap.getHandle());
	    texture.size = new Vector2F(bitmap.getWidth(), bitmap.getHeight());
	    

	    textures.put(name, texture);
	    
	    texture.resId = resourceId;
	 
	    return texture;
	}
	
	public Texture backgroundCreateTexture(final String name, final int resourceId) {
		if (textures.get(name) != null) {
			return textures.get(name);
		}
		
	    final Texture texture = new Texture();
	    texture.resId = resourceId;
	    textures.put(name, texture);
		
		BackgroundWorker.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				final IBitmap bitmap = Media.CONTEXT.loadBitmap(resourceId, false);
				texture.size = new Vector2F(bitmap.getWidth(), bitmap.getHeight());
				SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
					@Override
					public void run() {
						bitmap.toTexture();
						
					    if (bitmap.getHandle() == 0)
					    {
					        throw new RuntimeException("Error loading texture " + name + ".");
					    }
					    
						texture.handle = bitmap.getHandle();
						System.out.println("Background loaded texture: " + name);
					}
				});
			}
		});
		
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
		IBitmap bitmap = Media.CONTEXT.loadBitmap(texture.resId, false);
		bitmap.toTexture();
		int textureHandle = bitmap.getHandle();
	    texture.handle = textureHandle;
	}
	
	public void destroyUnnamedTexture(Texture texture) {
		unnamedTextures.remove(texture);
		int[] textureHandles = new int[1];
		textureHandles[0] = texture.handle;
		TyrGL.glDeleteTextures(1, textureHandles, 0);
	}
	
	public void destroyTexture(String textureName) {
		Texture texture = textures.get(textureName);
		textures.remove(textureName);
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
	    texture.size = new Vector2F(bitmap.getWidth(), bitmap.getHeight());
	    textures.put(name, texture);
	    
	    return texture;
	}

	public void addTexture(String textureName, Texture tex) {
		textures.put(textureName, tex);
	}
	
}
