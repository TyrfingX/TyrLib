package com.tyrlib2.gui;

import com.tyrlib2.graphics.renderables.FormattedText2;
import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.text.Font;
import com.tyrlib2.math.Rectangle;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * A basic label for displaying text
 * @author Sascha
 *
 */

public class Label extends Window{
	
	private FormattedText2 text;
	private Rectangle2 background;
	private int layer = 100;
	
	public Label(String name, Vector2 pos, String text) {
		super(name);
		Skin skin = WindowManager.getInstance().getSkin();
		this.text = new FormattedText2(text, 0, skin.LABEL_TEXT_COLOR.copy(), SceneManager.getInstance().getFont(skin.LABEL_FONT));
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2 size = this.text.getSize();
		
		if (skin.LABEL_BG_COLOR != Color.TRANSPARENT) {
			background = new Rectangle2(size, skin.LABEL_BG_COLOR.copy());
			addComponent(background);
		} 
		
		size = new Vector2(size);
		size.x /= viewport.getWidth();
		size.y /= viewport.getHeight();
		
		addComponent(this.text);
		
		setSize(new Vector2(size));
		setRelativePos(pos);
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public String getText() {
		return text.getText();
	}
	
	public void setText(String text) {
		if (!this.text.getText().equals(text)) {
			this.text.setText(text);
			setSize(new Vector2(this.text.getSize()));
			
			if (background != null) {
				Vector2 size = new Vector2(getSize());
				background.setSize(size);
			}
		}
	}

	public FormattedText2.ALIGNMENT getAlignment() {
		return text.getAlignment();
	}

	public void setAlignment(FormattedText2.ALIGNMENT alignment) {
		text.setAligment(alignment);
	}
	
	public Color getColor() {
		return text.getBaseColor();
	}
	
	public void setColor(Color color) {
		text.setBaseColor(color);
	}
	
	@Override
	public void setSize(Vector2 size) {
		super.setSize(size);
	}
	
	public Color getBgColor() {
		if (background == null) {
			return Color.TRANSPARENT;
		} else {
			return background.getColor();
		}
	}
	
	public void setBgColor(Color bgColor) {
		if (background == null) {
			Viewport viewport = SceneManager.getInstance().getViewport();
			Vector2 size = new Vector2(getSize());
			size.x *= viewport.getWidth();
			size.y *= viewport.getHeight();
			
			background = new Rectangle2(size, bgColor);
			addComponent(background, 0);
		} else {
			background.setColor(bgColor);
		}
	}
	
	public Rectangle2 getBackground() {
		return background;
	}
	
	@Override
	public float getAlpha() {
		return text.getBaseColor().a;
	}
	
	@Override
	public void setAlpha(float alpha) {
		if (background != null) {
			background.setAlpha(alpha);
		}
		
		Color color = text.getBaseColor();
		color.a = alpha;
		
		super.setAlpha(alpha);
	}
	
	
	public Font getFont() {
		return text.getFont();
	}
	
	public void setFont(Font font) {
		text.setFont(font);
	}
	
	public FormattedText2 getFormattedText() {
		return text;
	}
	
	@Override
	public long getPriority() {
		return priority*4;
	}
	
	
}
