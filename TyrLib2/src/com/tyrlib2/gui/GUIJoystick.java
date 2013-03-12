package com.tyrlib2.gui;

import com.tyrlib2.input.IJoystickListener;
import com.tyrlib2.input.Joystick;
import com.tyrlib2.math.Vector2;

public class GUIJoystick implements IJoystickListener{
	
	private Joystick joystick;
	private Vector2 basePoint;
	
	public GUIJoystick(Joystick joystick) {
		this.joystick = joystick;
	}

	@Override
	public void onJoystickActivated() {
		basePoint = joystick.getBasePoint();
	}

	@Override
	public void onJoystickDeactivated() {
	}

	@Override
	public void onJoystickMoved(Vector2 movement) {
		
	}
}
