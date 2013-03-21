package com.tyrlib2.input;

import android.view.MotionEvent;

import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.IPrioritizable;

public interface ITouchListener extends IPrioritizable{
	public boolean onTouchDown(Vector2 point, MotionEvent event, int fingerId);
	public boolean onTouchUp(Vector2 point, MotionEvent event, int fingerId);
	public boolean onTouchMove(Vector2 point, MotionEvent event, int fingerId);
	public boolean isEnabled();
}
