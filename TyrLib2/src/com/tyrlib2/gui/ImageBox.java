package com.tyrlib2.gui;

import com.tyrlib2.graphics.renderables.Image2;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
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
		
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas(atlasName);
		TextureRegion region = atlas.getRegion(atlasRegion);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2 imageSize = new Vector2(size.x * viewport.getWidth(), size.y * viewport.getHeight());
		image = new Image2(imageSize, atlas.getTexture(), region);
		
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
	
	public void setAtlasRegion(String regionName) {
		this.atlasRegion = regionName;
		
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas(atlasName);
		TextureRegion region = atlas.getRegion(atlasRegion);
		image.setTextureRegion(region);
	}
	
	public void setAtlas(String atlasName) {
		this.atlasName = atlasName;
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas(atlasName);
		image.setTexture(atlas.getTexture());
	}
}
