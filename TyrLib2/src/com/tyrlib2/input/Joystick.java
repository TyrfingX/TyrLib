package com.tyrlib2.input;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;

import com.tyrlib2.math.Vector2;

/**
 * A basic joystick for managing user input
 * @author Sascha
 *
 */


public class Joystick implements ITouchListener {

	/** Enable the joystick per default **/
	private boolean enabled = true;
	
	/** Set the joysticks priority to the lowest level by default **/
	private long priority = 0;
	
	/** Whether or not the joystick is currently being used or not **/
	private boolean active = false;
	
	/** The center of the joystick **/
	private Vector2 basePoint;
	
	/** The listeners of this joystick **/
	private List<IJoystickListener> listeners;
	
	/** The id of the finger which is currently controlling this joystick **/
	private int fingerId;
	
	/** Maximum offset **/
	private float maxDistance;
	
	public Joystick(float maxDistance) {
		listeners = new ArrayList<IJoystickListener>();
		this.maxDistance = maxDistance;
	}
	
	@Override
	public long getPriority() {
		return priority;
	}


	/** If the user starts touching an point then initialize the joystick with
	 * 	the touched position as base state
	 */
	@Override
	public boolean onTouchDown(Vector2 point, MotionEvent event) {
		if (!active) {
			active = true;
			basePoint = point;
			fingerId = event.getActionIndex();
			
			for (int i = 0; i < listeners.size(); ++i) {
				listeners.get(i).onJoystickActivated();
			}
		}
		return true;
	}

	/**	The user stopped using the joystick, assuming this is the 
	 * 	right touch stopping (considering multi touch)
	 */
	@Override
	public boolean onTouchUp(Vector2 point, MotionEvent event) {
		if (active && event.getActionIndex() == fingerId) {
			active = false;
			for (int i = 0; i < listeners.size(); ++i) {
				listeners.get(i).onJoystickDeactivated();
			}
		}
		return true;
	}

	
	/**	Use the joystick
	 * 	if its currently active
	 */
	@Override
	public boolean onTouchMove(Vector2 point, MotionEvent event) {
		if (active && event.getActionIndex() == fingerId) {
			
			Vector2 movement = basePoint.vectorTo(point);
			float distance = movement.normalize();
			distance = Math.min(distance, maxDistance);
			movement = movement.multiply(distance);
			
			for (int i = 0; i < listeners.size(); ++i) {
				listeners.get(i).onJoystickMoved(movement);
			}
		}
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public void addListener(IJoystickListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IJoystickListener listener) {
		listeners.remove(listener);
	}

	public Vector2 getBasePoint() {
		return basePoint;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}
	
	public float getMaxDistance() {
		return maxDistance;
	}

}
