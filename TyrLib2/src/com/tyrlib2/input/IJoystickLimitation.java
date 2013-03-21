package com.tyrlib2.input;

import android.view.MotionEvent;

import com.tyrlib2.math.Vector2;

public interface IJoystickLimitation {
	public boolean touchDownLimitation(Vector2 point, MotionEvent event, int fingerId);
	public boolean touchUpLimitation(Vector2 point, MotionEvent event, int fingerId);
	public boolean touchMoveLimitation(Vector2 point, MotionEvent event, int fingerId);
}
