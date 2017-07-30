package com.tyrlib2.gui;

import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * A framed window for displaying some kind of progress
 * @author Sascha
 *
 */

public class ProgressBar extends Window {

	private Window bg;
	private Overlay bar;
	
	private float progress;
	private float maxProgress;
	
	public ProgressBar(String name, Vector2 pos, Vector2 size, float maxProgress) {
		super(name, size);
		
		setRelativePos(pos);
		
		this.maxProgress = maxProgress;
		
		Skin skin = WindowManager.getInstance().getSkin();
		bg = WindowManager.getInstance().createRectWindow(name + "/Frame", new Vector2(), size, skin.PROGRESS_BAR_BG_PAINT);
		bg.setReceiveTouchEvents(false);
		
		bar = (Overlay) WindowManager.getInstance().createOverlay(	name + "/Bar", 
																	new Vector2(0, 0), 
																	new Vector2(size.x, size.y), 
																	skin.PROGRESS_BAR_COLOR);
		bar.setReceiveTouchEvents(false);
	
		this.addChild(bg);
		this.addChild(bar);
	}
	
	/**
	 * Set the progress of this bar
	 * @param progress	The progress. Should be between 0 and maxProgress.
	 */
	
	public void setProgress(float progress) {
		this.progress = progress;
	
		Vector2 frameSize = getSize();
		Vector2 size = new Vector2(frameSize.x, frameSize.y);
		size.x = size.x * progress/maxProgress;
		bar.setSize(size);
	}
	
	public float getProgress() {
		return progress;
	}
	
	public void setBarColor(Color color) {
		bar.setColor(color);
	}
	
	public Color getBarColor() {
		return bar.getColor();
	}
	
	public Overlay getBar() {
		return bar;
	}
	
	@Override
	public float getAlpha() {
		return bar.getAlpha();
	}
	
	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		Rectangle2 rect = (Rectangle2) bg.getComponent(0);
		rect.setAlpha(alpha);
		
		rect = (Rectangle2) bg.getComponent(1);
		rect.setAlpha(alpha);
	}

}
