package com.tyrlib2.ai.steering;

import com.tyrlib2.input.IJoystickListener;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;

public class JoystickInput implements IPattern, IJoystickListener {

	private float distanceToForceMapping;
	private Vector2 joystickDisplacement = new Vector2();
	private boolean joystickActive = false;
	
	public JoystickInput(float distanceToForceMapping) {
		this.distanceToForceMapping = distanceToForceMapping;
	}
	
	@Override
	public void onJoystickActivated() {
		joystickActive = true;
		joystickDisplacement = new Vector2();
	}

	@Override
	public void onJoystickDeactivated() {
		joystickActive = false;
	}

	@Override
	public void onJoystickMoved(Vector2 movement) {
		joystickDisplacement = movement;
	}

	@Override
	public Vector3 apply(IVehicle vehicle) {
		Vector3 steering = new Vector3();
		
		if (joystickActive) {
			Vector3 rotatedUp = vehicle.getUp();
			Vector3 forward = vehicle.getForward();
			
			rotatedUp.normalize();
			forward.normalize();
			
			Vector3 rotatedLeft = rotatedUp.cross(forward);
			rotatedLeft.normalize();
			
			if (joystickDisplacement != null) {
				steering = steering.add(rotatedLeft.multiply(-joystickDisplacement.x * distanceToForceMapping));
				steering = steering.add(rotatedUp.multiply(-joystickDisplacement.y * distanceToForceMapping));
			}
		}
		
		return steering;
	}

}
