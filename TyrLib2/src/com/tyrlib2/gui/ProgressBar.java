package com.tyrlib2.gui;

import com.tyrlib2.gui.Frame.FrameImagePosition;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * A framed window for displaying some kind of progress
 * @author Sascha
 *
 */

public class ProgressBar extends Window {

	private Frame frame;
	private Overlay bar;
	
	private float progress;
	private float maxProgress;
	
	public ProgressBar(String name, Vector2 pos, Vector2 size, float maxProgress) {
		super(name, size);
		
		setRelativePos(pos);
		
		this.maxProgress = maxProgress;
		
		frame = WindowManager.getInstance().createFrame(name + "/Frame", new Vector2(), size);
		
		Skin skin = WindowManager.getInstance().getSkin();
		frame.setBgRegion(FrameImagePosition.MIDDLE, skin.PROGRESS_BAR_BG);
		frame.setReceiveTouchEvents(false);
		this.addChild(frame);
		
		float frameBorderSize = frame.getBorderSize();
		bar = (Overlay) WindowManager.getInstance().createOverlay(	name + "/Bar", 
																	new Vector2(frameBorderSize, frameBorderSize), 
																	new Vector2(size.x - 2 * frameBorderSize, size.y - 2 * frameBorderSize), 
																	skin.PROGRESS_BAR_COLOR);
		bar.setReceiveTouchEvents(false);
		this.addChild(bar);
		
		
		
	}
	
	/**
	 * Set the progress of this bar
	 * @param progress	The progress. Should be between 0 and maxProgress.
	 */
	
	public void setProgress(float progress) {
		this.progress = progress;
	
		Vector2 frameSize = getSize();
		Vector2 size = new Vector2(frameSize.x - 2 * frame.getBorderSize(), frameSize.y - 2 * frame.getBorderSize());
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

}
