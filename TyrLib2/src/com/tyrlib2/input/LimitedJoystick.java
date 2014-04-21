package com.tyrlib2.input;


import com.tyrlib2.math.Vector2;



public class LimitedJoystick extends Joystick {

	private IJoystickLimitation limitation;
	
	public LimitedJoystick(float maxDistance, IJoystickLimitation limitation) {
		super(maxDistance);
		this.limitation = limitation;
	}
	
	@Override
	public boolean onTouchDown(Vector2 point, IMotionEvent event, int fingerId) {
		if (limitation.touchDownLimitation(point, event, fingerId)) {
			return super.onTouchDown(point, event, fingerId);
		}
	
		return false;
	}


	@Override
	public boolean onTouchUp(Vector2 point, IMotionEvent event, int fingerId) {
		if (limitation.touchUpLimitation(point, event, fingerId)) {
			return super.onTouchUp(point, event, fingerId);
		}
	
		return false;
	}


	@Override
	public boolean onTouchMove(Vector2 point, IMotionEvent event, int fingerId) {
		if (limitation.touchMoveLimitation(point, event, fingerId)) {
			return super.onTouchMove(point, event, fingerId);
		}

		return false;
	}

}
