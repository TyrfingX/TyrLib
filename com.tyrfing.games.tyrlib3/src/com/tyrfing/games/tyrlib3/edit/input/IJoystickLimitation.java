package com.tyrfing.games.tyrlib3.edit.input;


import com.tyrfing.games.tyrlib3.model.math.Vector2F;

public interface IJoystickLimitation {
	public boolean touchDownLimitation(Vector2F point, IMotionEvent event, int fingerId);
	public boolean touchUpLimitation(Vector2F point, IMotionEvent event, int fingerId);
	public boolean touchMoveLimitation(Vector2F point, IMotionEvent event, int fingerId);
}
