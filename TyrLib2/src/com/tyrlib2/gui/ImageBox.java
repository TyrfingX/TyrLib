package com.tyrlib2.gui;

import com.tyrlib2.graphics.materials.TexturedMaterial;
import com.tyrlib2.graphics.renderables.Image2;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * A basic GUI element for displaying a 2D image
 * @author Sascha
 *
 */

public class ImageBox extends Window {

	private Image2 image;
	private String atlasName;
	private String atlasRegion;
	
	public ImageBox(String name, Vector2 pos, String atlasName, String atlasRegion, Vector2 size, Vector2 repeat) {
		super(name, size);
		
		this.atlasName = atlasName;
		this.atlasRegion = atlasRegion;
		
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas(atlasName);
		TextureRegion region = atlas.getRegion(atlasRegion);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2 imageSize = new Vector2(size.x * viewport.getWidth(), size.y * viewport.getHeight());
		image = new Image2(imageSize, atlas.getTexture(), region, repeat);
		
		addComponent(image);
		
		setRelativePos(pos);
	}
	
	protected ImageBox(String name, Vector2 pos, String atlasName, String atlasRegion, Vector2 size) {
		this(name, pos, atlasName, atlasRegion, size, null);
	}
	
	public ImageBox(String name, Vector2 pos, String texture, Vector2 size) {
		super(name, size);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2 imageSize = new Vector2(size.x * viewport.getWidth(), size.y * viewport.getHeight());
		image = new Image2(imageSize, texture);
		
		addComponent(image);
		
		setRelativePos(pos);
	}

	protected ImageBox(String name, Vector2 pos, int textureHandle, Vector2 size) {
		super(name, size);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2 imageSize = new Vector2(size.x * viewport.getWidth(), size.y * viewport.getHeight());
		image = new Image2(imageSize, new Texture(textureHandle));
		
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
		super.setAlpha(alpha);
	}
	
	public void setColor(Color color) {
		image.setColor(color);
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
	
	public void setTexture(Texture texture) {
		image.setTexture(texture);
	}
	
	public void setAtlas(String atlasName) {
		this.atlasName = atlasName;
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas(atlasName);
		image.setTexture(atlas.getTexture());
	}

	public TexturedMaterial getMaterial() {
		return (TexturedMaterial) image.getMaterial();
	}
	
	public Image2 getImage() {
		return image;
	}
	
	@Override
	public void setSize(float x, float y) {
		super.setSize(x, y);
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2 imageSize = new Vector2(getSize().x * viewport.getWidth(), getSize().y * viewport.getHeight());
		image.setSize(imageSize);
	}
}
