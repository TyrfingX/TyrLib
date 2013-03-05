package com.tyrlib2.input;

import com.tyrlib2.math.Vector2;

/**
 * An interface for listeners of joysticks
 * @author Sascha
 *
 */

public interface IJoystickListener {
	
	/** The user started using the joystick **/
	public void onJoystickActivated();
	
	/** The user stopped using the joystick **/
	public void onJoystickDeactivated();
	
	/** The joystick was moved. movement is the relative point to the basepoint of the joystick **/
	public void onJoystickMoved(Vector2 movement);
}
