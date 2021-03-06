package com.tyrfing.games.tyrlib3.edit.input;

import com.tyrfing.games.tyrlib3.model.math.Vector2F;

public interface IMoveListener {
	public boolean onMove(Vector2F point);
	public boolean onEnterRenderWindow();
	public boolean onLeaveRenderWindow();
	public boolean onRenderWindowLoseFocus();
	public boolean onRenderWindowGainFocus();
	public boolean isEnabled();
}
