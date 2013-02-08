package com.tyrlib2.graphics.renderer;

public interface IFrameListener {
	public void onSurfaceCreated();
	public void onSurfaceChanged();
	
	/**
	 * Called after a frame has been rendererd
	 * @param time	The time since the last frame has been rendered
	 */
	public void onFrameRendered(float time);
}
