package com.tyrfing.games.tyrlib3.view.gui.widgets;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.Viewport;
import com.tyrfing.games.tyrlib3.view.graphics.materials.TexturedMaterial;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Image2;
import com.tyrfing.games.tyrlib3.view.graphics.texture.Texture;
import com.tyrfing.games.tyrlib3.view.graphics.texture.TextureAtlas;
import com.tyrfing.games.tyrlib3.view.graphics.texture.TextureRegion;

/**
 * A basic GUI element for displaying a 2D image
 * @author Sascha
 *
 */

public class ImageBox extends Window {

	private Image2 image;
	private String atlasName;
	private String atlasRegion;
	
	public ImageBox(String name, Vector2F pos, String atlasName, String atlasRegion, Vector2F size, Vector2F repeat) {
		super(name, size);
		
		this.atlasName = atlasName;
		this.atlasRegion = atlasRegion;
		
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas(atlasName);
		TextureRegion region = atlas.getRegion(atlasRegion);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2F imageSize = new Vector2F(size.x * viewport.getWidth(), size.y * viewport.getHeight());
		image = new Image2(imageSize, atlas.getTexture(), region, repeat);
		
		addComponent(image);
		
		setRelativePos(pos);
	}
	
	protected ImageBox(String name, Vector2F pos, String atlasName, String atlasRegion, Vector2F size) {
		this(name, pos, atlasName, atlasRegion, size, null);
	}
	
	public ImageBox(String name, Vector2F pos, String texture, Vector2F size) {
		super(name, size);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2F imageSize = new Vector2F(size.x * viewport.getWidth(), size.y * viewport.getHeight());
		image = new Image2(imageSize, texture);
		
		addComponent(image);
		
		setRelativePos(pos);
	}

	public ImageBox(String name, Vector2F pos, int textureHandle, Vector2F size) {
		super(name, size);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2F imageSize = new Vector2F(size.x * viewport.getWidth(), size.y * viewport.getHeight());
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
		Vector2F imageSize = new Vector2F(getSize().x * viewport.getWidth(), getSize().y * viewport.getHeight());
		image.setSize(imageSize);
	}
}
