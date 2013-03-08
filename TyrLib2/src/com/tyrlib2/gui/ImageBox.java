package com.tyrlib2.gui;

import com.tyrlib2.graphics.renderables.Image2;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.math.Vector2;

/**
 * A basic GUI element for displaying a 2D image
 * @author Sascha
 *
 */

public class ImageBox extends Window {

	private Image2 image;
	private String atlasName;
	private String atlasRegion;
	
	protected ImageBox(String name, Vector2 pos, String atlasName, String atlasRegion, Vector2 size) {
		super(name, size);
		
		this.atlasName = atlasName;
		this.atlasRegion = atlasRegion;
		
		TextureAtlas atlas = WindowManager.getInstance().getTextureAtlas(atlasName);
		TextureRegion region = atlas.getRegion(atlasRegion);
		image = new Image2(size, atlas.getTexture(), region);
		
		addComponent(image);
		
		setRelativePos(pos);
	}
	
	@Override
	public float getAlpha() {
		return image.getAlpha();
	}
	
	@Override
	public void setAlpha(float alpha) {
		image.setAlpha(alpha);
	}

	public String getAtlasName() {
		return atlasName;
	}
	
	public String getAtlasRegion() {
		return atlasRegion;
	}
}
