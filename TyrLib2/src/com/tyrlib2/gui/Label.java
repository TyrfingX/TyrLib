package com.tyrlib2.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.game.LinkManager;
import com.tyrlib2.graphics.renderables.FormattedText2;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.renderables.FormattedText2.TextSection;
import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.text.Font;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
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
	private List<Window> linkRegions = new ArrayList<Window>();
	private List<Window> imgRegions = new ArrayList<Window>();
	
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
	
		addLinkRegions();
		addImgRegions();
	}
	
	private void addLinkRegions() {
		
		for (int i = 0; i < text.textSections.size(); ++i) {
			final TextSection s = text.textSections.get(i);
			if (s.link != null && !s.link.equals("")) {
				Window linkRegion = WindowManager.getInstance().createWindow(this.getName()+"/links/"+s.link, s.size);
				this.addChild(linkRegion);
				linkRegion.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						LinkManager.getInstance().call(s.link);
					}
				});
				linkRegion.setReceiveTouchEvents(true);
				linkRegion.setRelativePos(s.pos);
				linkRegions.add(linkRegion);
			}
		}
	}
	
	private void clearLinkRegions() {
		for (int i = 0; i < linkRegions.size(); ++i) {
			WindowManager.getInstance().destroyWindow(linkRegions.get(i));
		}
		linkRegions.clear();
	}
	
	private void addImgRegions() {
		for (int i = 0; i < text.textSections.size(); ++i) {
			final TextSection s = text.textSections.get(i);
			if (s.atlasName != null) {
				Window imgRegion = WindowManager.getInstance().createImageBox(this.getName()+"/img/"+s.regionName, s.pos, s.atlasName, s.regionName, s.size);
				this.addChild(imgRegion);
				imgRegion.setRelativePos(s.pos);
				imgRegions.add(imgRegion);
			}
		}
	}
	
	private void clearImgRegions() {
		for (int i = 0; i < imgRegions.size(); ++i) {
			WindowManager.getInstance().destroyWindow(imgRegions.get(i));
		}
		imgRegions.clear();
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
			
			Viewport viewport = SceneManager.getInstance().getViewport();
			Vector2 size = new Vector2(this.text.getSize());
			size.x /= viewport.getWidth();
			size.y /= viewport.getHeight();
			
			setSize(size);
			
			if (background != null) {
				background.setSize(new Vector2(this.text.getSize()));
			}
			
			clearImgRegions();
			addImgRegions();
			
			clearLinkRegions();
			addLinkRegions();
		}
	}

	public FormattedText2.ALIGNMENT getAlignment() {
		return text.getAlignment();
	}

	public void setAlignment(FormattedText2.ALIGNMENT alignment) {
		text.setAligment(alignment);
		clearImgRegions();
		addImgRegions();
		clearLinkRegions();
		addLinkRegions();
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
	
	@Override
	public Vector2 getAbsolutePos() {
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector3 pos = node.getCachedAbsolutePosVector();
		float xOffset = 0;
		if (getAlignment() == ALIGNMENT.RIGHT) {
			xOffset = this.getSize().x;
		} else if (getAlignment() == ALIGNMENT.CENTER) {
			xOffset = this.getSize().x/2;
		}
			
		return new Vector2(pos.x/viewport.getWidth() - xOffset, pos.y/viewport.getHeight());
	}
	
	
}
