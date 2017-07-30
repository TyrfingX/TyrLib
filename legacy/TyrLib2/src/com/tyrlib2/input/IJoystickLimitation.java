package com.tyrlib2.input;


import com.tyrlib2.math.Vector2;

public interface IJoystickLimitation {
	public boolean touchDownLimitation(Vector2 point, IMotionEvent event, int fingerId);
	public boolean touchUpLimitation(Vector2 point, IMotionEvent event, int fingerId);
	public boolean touchMoveLimitation(Vector2 point, IMotionEvent event, int fingerId);
}
