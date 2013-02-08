package com.tyrlib2.input;

import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Prioritizable;

public interface ITouchListener extends Prioritizable{
	public boolean onTouchDown(Vector2 point);
	public boolean onTouchUp(Vector2 point);
	public boolean onTouchMove(Vector2 point);
	public boolean isEnabled();
}
