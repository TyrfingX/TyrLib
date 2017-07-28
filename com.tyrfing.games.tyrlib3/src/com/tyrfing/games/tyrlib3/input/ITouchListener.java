package com.tyrfing.games.tyrlib3.input;


import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.util.IPrioritizable;

public interface ITouchListener extends IPrioritizable{
	public boolean onTouchDown(Vector2F point, IMotionEvent event, int fingerId);
	public boolean onTouchUp(Vector2F point, IMotionEvent event, int fingerId);
	public boolean onTouchMove(Vector2F point, IMotionEvent event, int fingerId);
	public boolean isEnabled();
}
