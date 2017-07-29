package com.tyrfing.games.tyrlib3.edit.input;


import com.tyrfing.games.tyrlib3.model.math.Vector2F;

public abstract class ATouchListener implements ITouchListener {
	@Override
	public long getPriority() {
		return 0;
	}

	@Override
	public boolean onTouchDown(Vector2F point, IMotionEvent event, int fingerId) {
		return false;
	}

	@Override
	public boolean onTouchUp(Vector2F point, IMotionEvent event, int fingerId) {
		return false;
	}

	@Override
	public boolean onTouchMove(Vector2F point, IMotionEvent event, int fingerId) {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
