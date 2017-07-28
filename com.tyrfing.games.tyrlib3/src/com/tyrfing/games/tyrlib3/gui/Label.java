package com.tyrfing.games.tyrlib3.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.game.LinkManager;
import com.tyrfing.games.tyrlib3.graphics.renderables.FormattedText2;
import com.tyrfing.games.tyrlib3.graphics.renderables.Rectangle2;
import com.tyrfing.games.tyrlib3.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrfing.games.tyrlib3.graphics.renderables.FormattedText2.TextSection;
import com.tyrfing.games.tyrlib3.graphics.renderer.Viewport;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.graphics.text.Font;
import com.tyrfing.games.tyrlib3.gui.WindowEvent.WindowEventType;
import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.math.Vector3F;
import com.tyrfing.games.tyrlib3.util.Color;

/**
 * A basic label for displaying text
 * @author Sascha
 *
 */

public class Label extends Window{
	
	private FormattedText2 text;
	private Rectangle2 background;
	private List<Window> linkRegions = new ArrayList<Window>();
	private List<Window> imgRegions = new ArrayList<Window>();
	private String targetText;
	private float displaySpeed;
	private float displayAmount;
	
	public Label(String name, Vector2F pos, String text) {
		super(name);
		Skin skin = WindowManager.getInstance().getSkin();
		this.text = new FormattedText2(text, 0, skin.LABEL_TEXT_COLOR.copy(), SceneManager.getInstance().getFont(skin.LABEL_FONT));
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2F size = this.text.getSize();
		
		if (skin.LABEL_BG_COLOR != Color.TRANSPARENT) {
			background = new Rectangle2(size, skin.LABEL_BG_COLOR.copy());
			addComponent(background);
		} 
		
		size = new Vector2F(size);
		size.x /= viewport.getWidth();
		size.y /= viewport.getHeight();
		
		addComponent(this.text);
		
		setSize(new Vector2F(size));
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
				imgRegion.setRelativePos(s.pos.x, s.pos.y+s.size.y);
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
	
	public String getText() {
		return text.getText();
	}
	
	public void setText(String text) {
		if (!this.text.getText().equals(text)) {
			this.text.setText(text);
			
			Viewport viewport = SceneManager.getInstance().getViewport();
			Vector2F size = new Vector2F(this.text.getSize());
			size.x /= viewport.getWidth();
			size.y /= viewport.getHeight();
			
			setSize(size);
			
			if (background != null) {
				background.setSize(new Vector2F(this.text.getSize()));
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
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector2F size = new Vector2F(this.text.getSize());
		size.x /= viewport.getWidth();
		size.y /= viewport.getHeight();
		
		setSize(size);
		
		if (background != null) {
			background.setSize(new Vector2F(this.text.getSize()));
		}
		
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
			Vector2F size = new Vector2F(getSize());
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
		String txt = text.getText();
		this.setText("");
		this.setText(txt);
	}
	
	public FormattedText2 getFormattedText() {
		return text;
	}
	
	@Override
	public long getPriority() {
		return priority*4;
	}
	
	@Override
	public void setSize(Vector2F size) {
		super.setSize(size);
		fireEvent(new WindowEvent(this, WindowEventType.SIZE_CHANGED));
	}
	
	@Override
	public Vector2F getAbsolutePos() {
		Viewport viewport = SceneManager.getInstance().getViewport();
		Vector3F pos = node.getCachedAbsolutePosVector();
		float xOffset = 0;
		if (getAlignment() == ALIGNMENT.RIGHT) {
			xOffset = this.getSize().x;
		} else if (getAlignment() == ALIGNMENT.CENTER) {
			xOffset = this.getSize().x/2;
		}
			
		return new Vector2F(pos.x/viewport.getWidth() - xOffset, pos.y/viewport.getHeight());
	}
	
	public void displayText(String targetText, float time) {
		setText("");
		this.displayAmount = 0;
		this.targetText = targetText;
		displaySpeed = targetText.length() / time;
	}
	
	public void finishDisplaying() {
		text.setText(targetText);
		targetText = null;
		displayAmount = 0;
	}
	
	public boolean isDisplaying() {
		return targetText != null;
	}
	
	@Override
	public void onUpdate(float time) {
		super.onUpdate(time);
		
		while(targetText != null) {
			displayAmount += time * displaySpeed;
			int amount = (int) displayAmount;
			
			if (amount >= targetText.length()) {
				finishDisplaying();
				break;
			} else {
				try {
					text.setText(targetText.substring(0, amount));
					break;
				} catch (Exception e) {
				}
			}
		}
	}
	
	public void setMaxWidth(float maxWidth) {
		text.setMaxWidth(maxWidth);
	}
	
	
}
