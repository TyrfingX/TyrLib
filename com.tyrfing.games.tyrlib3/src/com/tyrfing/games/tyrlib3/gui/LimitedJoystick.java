package com.tyrfing.games.tyrlib3.gui;


import com.tyrfing.games.tyrlib3.input.IJoystickLimitation;
import com.tyrfing.games.tyrlib3.input.IMotionEvent;
import com.tyrfing.games.tyrlib3.math.Vector2F;



public class LimitedJoystick extends Joystick {

	private IJoystickLimitation limitation;
	
	public LimitedJoystick(float maxDistance, IJoystickLimitation limitation) {
		super(maxDistance);
		this.limitation = limitation;
	}
	
	@Override
	public boolean onTouchDown(Vector2F point, IMotionEvent event, int fingerId) {
		if (limitation.touchDownLimitation(point, event, fingerId)) {
			return super.onTouchDown(point, event, fingerId);
		}
	
		return false;
	}


	@Override
	public boolean onTouchUp(Vector2F point, IMotionEvent event, int fingerId) {
		if (limitation.touchUpLimitation(point, event, fingerId)) {
			return super.onTouchUp(point, event, fingerId);
		}
	
		return false;
	}


	@Override
	public boolean onTouchMove(Vector2F point, IMotionEvent event, int fingerId) {
		if (limitation.touchMoveLimitation(point, event, fingerId)) {
			return super.onTouchMove(point, event, fingerId);
		}

		return false;
	}

}
