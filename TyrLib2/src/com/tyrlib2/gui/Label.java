package com.tyrlib2.gui;

import com.tyrlib2.graphics.renderables.FormattedText2;
import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.graphics.scene.SceneManager;
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
	
	public Label(String name, Vector2 pos, String text) {
		super(name);
		Skin skin = WindowManager.getInstance().getSkin();
		this.text = new FormattedText2(text, 0, skin.LABEL_TEXT_COLOR, SceneManager.getInstance().getFont(skin.LABEL_FONT));
		
		Vector2 size = this.text.getSize();
		if (skin.LABEL_BG_COLOR != Color.TRANSPARENT) {
			background = new Rectangle2(size, skin.LABEL_BG_COLOR);
			addComponent(background);
		} 
		
		addComponent(this.text);
		
		setSize(new Vector2(size));
		setRelativePos(pos);
	}
	
	public String getText() {
		return text.getText();
	}
	
	public void setText(String text) {
		this.text.setText(text);
		setSize(new Vector2(this.text.getSize()));
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
	
	public Color getBgColor() {
		if (background == null) {
			return Color.TRANSPARENT;
		} else {
			return background.getColor();
		}
	}
	
	public void setBgColor(Color bgColor) {
		if (background == null) {
			background = new Rectangle2(getSize(), bgColor);
			addComponent(background, 0);
		} else {
			background.setColor(bgColor);
		}
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
		text.setBaseColor(color);
	}
	
	
	
}
