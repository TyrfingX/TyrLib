package com.tyrlib2.gui;

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

	private String normalLeftBgImage;
	private String normalMiddleBgImage;
	private String normalRightBgImage;
	private String highlightLeftBgImage;
	private String highlightMiddleBgImage;
	private String highlightRightBgImage;
	private float leftBgSize;
	private float rightBgSize;
	private Color normalTextColor;
	private Color highlightTextColor;
	
	private ImageBox bgLeft;
	private ImageBox bgMiddle;
	private ImageBox bgRight;
	private Label label;
	
	public Button(String name, Vector2 pos, Vector2 size, String text) {
		super(name, size);
		
		this.setRelativePos(pos);
		
		WindowManager windowManager = WindowManager.getInstance();
		Skin skin = windowManager.getSkin();
		
		String atlas = skin.TEXTURE_ATLAS;
		
		normalLeftBgImage = skin.BUTTON_NORMAL_LEFT;
		normalMiddleBgImage = skin.BUTTON_NORMAL_MIDDLE;
		normalRightBgImage = skin.BUTTON_NORMAL_RIGHT;
		
		highlightLeftBgImage = skin.BUTTON_HIGHLIGHT_LEFT;
		highlightMiddleBgImage = skin.BUTTON_HIGHLIGHT_MIDDLE;
		highlightRightBgImage = skin.BUTTON_HIGHLIGHT_RIGHT;
		
		normalTextColor = skin.BUTTON_NORMAL_TEXT_COLOR;
		highlightTextColor = skin.BUTTON_HIGHLIGHT_TEXT_COLOR;

		leftBgSize = skin.BUTTON_LEFT_SIZE;
		rightBgSize = skin.BUTTON_RIGHT_SIZE;
		
		bgLeft = (ImageBox) windowManager.createImageBox(name + "/BackgroundLeft", 
														 new Vector2(), 
														 atlas, 
														 normalLeftBgImage, 
														 new Vector2(leftBgSize, size.y));
		bgLeft.setReceiveTouchEvents(false);
		
		bgMiddle = (ImageBox) windowManager.createImageBox(name + "/BackgroundMiddle", 
														   new Vector2(skin.BUTTON_LEFT_SIZE, 0), 
														   atlas, 
														   normalMiddleBgImage, 
														   new Vector2(size.x-(leftBgSize+rightBgSize), size.y));
		bgMiddle.setReceiveTouchEvents(false);		
		
		bgRight = (ImageBox) windowManager.createImageBox(name + "/BackgroundMiddle", 
														  new Vector2(size.x-skin.BUTTON_RIGHT_SIZE, 0), 
														  atlas, 
														  normalRightBgImage, 
														  new Vector2(rightBgSize, size.y));
		bgRight.setReceiveTouchEvents(false);	
		
		Vector2 labelPos = new Vector2(size.x /2, -size.y /2);
	
		label = (Label) windowManager.createLabel(name +  "/Label", labelPos, text);
		label.setAlignment(ALIGNMENT.CENTER);
		label.setReceiveTouchEvents(false);
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		Font font = label.getFont();
		labelPos.y += font.glText.getCharHeight() / viewport.getHeight();
		label.setRelativePos(labelPos);
		
		this.addChild(bgLeft);
		this.addChild(bgMiddle);
		this.addChild(bgRight);
		this.addChild(label);
		
		this.setPriority(getPriority());
	}
	
	@Override
	public void onTouchEntersWindow() {
		// Start highlighting the button
		super.onTouchEntersWindow();
		label.setColor(highlightTextColor);
		bgLeft.setAtlasRegion(highlightLeftBgImage);
		bgMiddle.setAtlasRegion(highlightMiddleBgImage);
		bgRight.setAtlasRegion(highlightRightBgImage);
	}
	
	@Override 
	public void onTouchLeavesWindow() {
		// Stop highlighting the button
		super.onTouchLeavesWindow();
		label.setColor(normalTextColor);
		bgLeft.setAtlasRegion(normalLeftBgImage);
		bgMiddle.setAtlasRegion(normalMiddleBgImage);
		bgRight.setAtlasRegion(normalRightBgImage);
	}

	public String getAtlas() {
		return bgMiddle.getAtlasName();
	}

	public void setAtlas(String atlas) {
		bgLeft.setAtlas(atlas);
		bgMiddle.setAtlas(atlas);
		bgRight.setAtlas(atlas);
	}

	public String getNormalLeftBgImage() {
		return normalLeftBgImage;
	}

	public void setNormalLeftBgImage(String normalLeftBgImage) {
		this.normalLeftBgImage = normalLeftBgImage;
		if (!this.isBeingTouched()) {
			bgLeft.setAtlasRegion(normalLeftBgImage);
		}
	}

	public String getHighlightLeftBgImage() {
		return highlightLeftBgImage;
	}

	public void setHighlightLeftBgImage(String highlightLeftBgImage) {
		this.highlightLeftBgImage = highlightLeftBgImage;
		if (this.isBeingTouched()) {
			bgLeft.setAtlasRegion(highlightLeftBgImage);
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
	
	

}
