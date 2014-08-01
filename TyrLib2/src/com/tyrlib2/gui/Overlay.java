package com.tyrlib2.gui;

import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * A window displaying a colored rectangle
 * @author Sascha
 *
 */

public class Overlay extends Window {

	private Rectangle2 bg;
	
	public Overlay(String name, Vector2 pos, Vector2 size, Color color) {
		super(name, size);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2 rectSize = new Vector2(viewport.getWidth()*size.x, viewport.getHeight()*size.y);
		
		bg = new Rectangle2(rectSize,color.copy());
		addComponent(bg);
		
		setRelativePos(pos);
	}
	
	public void setColor(Color color) {
		bg.setColor(color.copy());
	}
	
	public Color getColor() {
		return bg.getColor();
	}
	
	@Override
	public float getAlpha() {
		return bg.getAlpha();
	}
	
	@Override
	public void setAlpha(float alpha) {
		bg.setAlpha(alpha);
		super.setAlpha(alpha);
	}
	
	@Override
	public void setSize(Vector2 size) {
		super.setSize(size);
		if (bg != null) {
			Viewport viewport = SceneManager.getInstance().getViewport();
			bg.setSize(new Vector2(size.x * viewport.getWidth(), size.y * viewport.getHeight()));
		}
	}

}
