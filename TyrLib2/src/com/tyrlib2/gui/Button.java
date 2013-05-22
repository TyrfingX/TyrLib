package com.tyrlib2.gui;

import java.util.HashMap;
import java.util.Map;

import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.text.Font;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * A basic button
 * @author Sascha
 *
 */

public class Button extends Window {

	public enum ButtonImagePosition {
		LEFT,
		MIDDLE,
		RIGHT
	}
	
	private Map<ButtonImagePosition, String> normalBgImages;
	private Map<ButtonImagePosition, String> highlightBgImages;
	private Map<ButtonImagePosition, ImageBox> bgImageBoxes;
	
	private float leftBgSize;
	private float rightBgSize;
	private Color normalTextColor;
	private Color highlightTextColor;
	
	private float alpha;
	private Label label;
	
	public Button(String name, Vector2 pos, Vector2 size, String text) {
		super(name, size);
		
		this.setRelativePos(pos);
		
		WindowManager windowManager = WindowManager.getInstance();
		Skin skin = windowManager.getSkin();
		
		normalBgImages = new HashMap<ButtonImagePosition, String>();
		highlightBgImages = new HashMap<ButtonImagePosition, String>();
		bgImageBoxes = new HashMap<ButtonImagePosition, ImageBox>(); 
		
		String atlas = skin.TEXTURE_ATLAS;
		
		normalBgImages.put(ButtonImagePosition.LEFT, skin.BUTTON_NORMAL_LEFT);
		normalBgImages.put(ButtonImagePosition.MIDDLE, skin.BUTTON_NORMAL_MIDDLE);
		normalBgImages.put(ButtonImagePosition.RIGHT, skin.BUTTON_NORMAL_RIGHT);
		
		highlightBgImages.put(ButtonImagePosition.LEFT, skin.BUTTON_HIGHLIGHT_LEFT);
		highlightBgImages.put(ButtonImagePosition.MIDDLE, skin.BUTTON_HIGHLIGHT_MIDDLE);
		highlightBgImages.put(ButtonImagePosition.RIGHT, skin.BUTTON_HIGHLIGHT_RIGHT);
		
		normalTextColor = skin.BUTTON_NORMAL_TEXT_COLOR;
		highlightTextColor = skin.BUTTON_HIGHLIGHT_TEXT_COLOR;

		leftBgSize = skin.BUTTON_LEFT_SIZE;
		rightBgSize = skin.BUTTON_RIGHT_SIZE;
		
		ImageBox bg;
		
		bg = (ImageBox) windowManager.createImageBox(name + "/BackgroundLeft", 
												     new Vector2(), 
												     atlas, 
													 normalBgImages.get(ButtonImagePosition.LEFT), 
													 new Vector2(leftBgSize, size.y));
		bgImageBoxes.put(ButtonImagePosition.LEFT, bg);
		
		bg = (ImageBox) windowManager.createImageBox(name + "/BackgroundMiddle", 
														   new Vector2(skin.BUTTON_LEFT_SIZE, 0), 
														   atlas, 
														   normalBgImages.get(ButtonImagePosition.MIDDLE), 
														   new Vector2(size.x-(leftBgSize+rightBgSize), size.y));	
		bgImageBoxes.put(ButtonImagePosition.MIDDLE, bg);
		
		bg = (ImageBox) windowManager.createImageBox(name + "/BackgroundMiddle", 
														  new Vector2(size.x-skin.BUTTON_RIGHT_SIZE, 0), 
														  atlas, 
														  normalBgImages.get(ButtonImagePosition.RIGHT), 
														  new Vector2(rightBgSize, size.y));
		bgImageBoxes.put(ButtonImagePosition.RIGHT, bg);
		
		Vector2 labelPos = new Vector2(size.x /2, size.y /2);
	
		label = (Label) windowManager.createLabel(name +  "/Label", labelPos, text);
		label.setAlignment(ALIGNMENT.CENTER);
		label.setColor(normalTextColor);
		label.setReceiveTouchEvents(false);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Font font = label.getFont();
		labelPos.y -= font.glText.getCharHeight() / viewport.getHeight();
		label.setRelativePos(labelPos);
		
		for (ButtonImagePosition position : ButtonImagePosition.values()) {
			bgImageBoxes.get(position).setReceiveTouchEvents(false);
			this.addChild(bgImageBoxes.get(position));
		}
		
		this.addChild(label);
		
		this.setPriority(getPriority());
	}
	
	@Override
	public void onTouchEntersWindow() {
		// Start highlighting the button
		super.onTouchEntersWindow();
		label.setColor(highlightTextColor);
		for (ButtonImagePosition position : ButtonImagePosition.values()) {
			bgImageBoxes.get(position).setAtlasRegion(highlightBgImages.get(position));
		}
	}
	
	@Override 
	public void onTouchLeavesWindow() {
		// Stop highlighting the button
		super.onTouchLeavesWindow();
		label.setColor(normalTextColor);
		for (ButtonImagePosition position : ButtonImagePosition.values()) {
			bgImageBoxes.get(position).setAtlasRegion(normalBgImages.get(position));
		}
	}

	public String getAtlas() {
		return bgImageBoxes.get(ButtonImagePosition.MIDDLE).getAtlasName();
	}

	public void setAtlas(String atlas) {
		for (ButtonImagePosition position : ButtonImagePosition.values()) {
			bgImageBoxes.get(position).setAtlas(atlas);
		}
	}

	public String getNormalBgImage(ButtonImagePosition position) {
		return normalBgImages.get(position);
	}
	
	public void setNormalBgImage(String normalBgImage, ButtonImagePosition position) {
		normalBgImages.put(position, normalBgImage);
		if (!this.isBeingTouched()) {
			bgImageBoxes.get(position).setAtlasRegion(normalBgImage);
		}
	}

	public String getHighlightBgImage(ButtonImagePosition position) {
		return highlightBgImages.get(position);
	}

	public void setHighlightBgImage(String highlightBgImage, ButtonImagePosition position) {
		highlightBgImages.put(position, highlightBgImage);
		if (this.isBeingTouched()) {
			bgImageBoxes.get(position).setAtlasRegion(highlightBgImage);
		}
	}

	public Color getNormalTextColor() {
		return normalTextColor;
	}

	public void setNormalTextColor(Color normalTextColor) {
		this.normalTextColor = normalTextColor;
		if (!this.isBeingTouched()) {
			label.setColor(normalTextColor);
		}
	}

	public Color getHighlightTextColor() {
		return highlightTextColor;
	}

	public void setHighlightTextColor(Color highlightTextColor) {
		this.highlightTextColor = highlightTextColor;
		if (this.isBeingTouched()) {
			label.setColor(highlightTextColor);
		}
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		this.alpha = alpha;
	}
	
	

}
