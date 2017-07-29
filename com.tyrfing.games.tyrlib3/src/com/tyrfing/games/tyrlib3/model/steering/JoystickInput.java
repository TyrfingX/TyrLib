package com.tyrfing.games.tyrlib3.model.steering;

import com.tyrfing.games.tyrlib3.edit.input.IJoystickListener;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;

public class JoystickInput implements IPattern, IJoystickListener {

	private float distanceToForceMapping;
	private Vector2F joystickDisplacement = new Vector2F();
	private boolean joystickActive = false;
	
	public JoystickInput(float distanceToForceMapping) {
		this.distanceToForceMapping = distanceToForceMapping;
	}
	
	@Override
	public void onJoystickActivated() {
		joystickActive = true;
		joystickDisplacement = new Vector2F();
	}

	@Override
	public void onJoystickDeactivated() {
		joystickActive = false;
	}

	@Override
	public void onJoystickMoved(Vector2F movement) {
		joystickDisplacement = movement;
	}

	@Override
	public Vector3F apply(IVehicle vehicle) {
		Vector3F steering = new Vector3F();
		
		if (joystickActive) {
			Vector3F rotatedUp = vehicle.getUp();
			Vector3F forward = vehicle.getForward();
			
			rotatedUp.normalize();
			forward.normalize();
			
			Vector3F rotatedLeft = rotatedUp.cross(forward);
			rotatedLeft.normalize();
			
			if (joystickDisplacement != null) {
				steering = steering.add(rotatedLeft.multiply(-joystickDisplacement.x * distanceToForceMapping));
				steering = steering.add(rotatedUp.multiply(-joystickDisplacement.y * distanceToForceMapping));
			}
		}
		
		return steering;
	}

}
