package com.tyrfing.games.tyrlib3.gui;

import com.tyrfing.games.tyrlib3.graphics.renderables.Rectangle2;
import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.util.Color;

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
	
	public ProgressBar(String name, Vector2F pos, Vector2F size, float maxProgress) {
		super(name, size);
		
		setRelativePos(pos);
		
		this.maxProgress = maxProgress;
		
		Skin skin = WindowManager.getInstance().getSkin();
		bg = WindowManager.getInstance().createRectWindow(name + "/Frame", new Vector2F(), size, skin.PROGRESS_BAR_BG_PAINT);
		bg.setReceiveTouchEvents(false);
		
		bar = (Overlay) WindowManager.getInstance().createOverlay(	name + "/Bar", 
																	new Vector2F(0, 0), 
																	new Vector2F(size.x, size.y), 
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
	
		Vector2F frameSize = getSize();
		Vector2F size = new Vector2F(frameSize.x, frameSize.y);
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
