package com.tyrlib2.input;

import android.view.MotionEvent;

import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Prioritizable;

public interface ITouchListener extends Prioritizable{
	public boolean onTouchDown(Vector2 point, MotionEvent event);
	public boolean onTouchUp(Vector2 point, MotionEvent event);
	public boolean onTouchMove(Vector2 point, MotionEvent event);
	public boolean isEnabled();
}
