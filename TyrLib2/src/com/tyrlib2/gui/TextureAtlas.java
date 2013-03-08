package com.tyrlib2.gui;

import java.util.HashMap;
import java.util.Map;

import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureRegion;

public class TextureAtlas {
	
	private Texture texture;
	private Map<String, TextureRegion> regions;
	
	public TextureAtlas(Texture texture) {
		this.texture = texture;
		regions = new HashMap<String, TextureRegion>();
	}
	
	public void addRegion(String name, TextureRegion region) {
		regions.put(name, region);
	}
	
	public TextureRegion getRegion(String name) {
		return regions.get(name);
	}

	public Texture getTexture() {
		return texture;
	}
}
