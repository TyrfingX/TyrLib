package com.tyrfing.games.tyrlib3.view.gui.widgets;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.Viewport;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Rectangle2;

/**
 * A window displaying a colored rectangle
 * @author Sascha
 *
 */

public class Overlay extends Window {

	private Rectangle2 bg;
	
	public Overlay(String name, Vector2F pos, Vector2F size, Color color) {
		super(name, size);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2F rectSize = new Vector2F(viewport.getWidth()*size.x, viewport.getHeight()*size.y);
		
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
	public void setSize(Vector2F size) {
		super.setSize(size);
		if (bg != null) {
			Viewport viewport = SceneManager.getInstance().getViewport();
			bg.setSize(new Vector2F(size.x * viewport.getWidth(), size.y * viewport.getHeight()));
		}
	}

}
