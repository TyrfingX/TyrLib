package com.tyrlib2.input;


import com.tyrlib2.math.Vector2;

public abstract class TouchListener implements ITouchListener {
	@Override
	public long getPriority() {
		return 0;
	}

	@Override
	public boolean onTouchDown(Vector2 point, IMotionEvent event, int fingerId) {
		return false;
	}

	@Override
	public boolean onTouchUp(Vector2 point, IMotionEvent event, int fingerId) {
		return false;
	}

	@Override
	public boolean onTouchMove(Vector2 point, IMotionEvent event, int fingerId) {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
