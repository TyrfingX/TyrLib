package com.tyrlib2.input;

import com.tyrlib2.math.Vector2;

public interface IMoveListener {
	public boolean onMove(Vector2 point);
	public boolean onEnterRenderWindow();
	public boolean onLeaveRenderWindow();
	public boolean onRenderWindowLoseFocus();
	public boolean onRenderWindowGainFocus();
	public boolean isEnabled();
}
