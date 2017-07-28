package com.tyrfing.games.tyrlib3.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.input.IJoystickListener;
import com.tyrfing.games.tyrlib3.input.IMotionEvent;
import com.tyrfing.games.tyrlib3.input.ITouchListener;
import com.tyrfing.games.tyrlib3.math.Vector2F;

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
	private Vector2F basePoint;
	
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
	public boolean onTouchDown(Vector2F point, IMotionEvent event, int fingerId) {
		if (!active) {
			active = true;
			basePoint = point;
			
			this.fingerId = fingerId;
			
			for (int i = 0; i < listeners.size(); ++i) {
				listeners.get(i).onJoystickActivated();
			}
			
			return true;
		}
		
		return false;
		
	}

	/**	The user stopped using the joystick, assuming this is the 
	 * 	right touch stopping (considering multi touch)
	 */
	@Override
	public boolean onTouchUp(Vector2F point, IMotionEvent event, int fingerId) {
		if (active && fingerId == this.fingerId) {
			active = false;
			for (int i = 0; i < listeners.size(); ++i) {
				listeners.get(i).onJoystickDeactivated();
			}
			
			return false;
		}
		
		return false;
	}
	
	public void deactivate() {
		active = false;
		for (int i = 0; i < listeners.size(); ++i) {
			listeners.get(i).onJoystickDeactivated();
		}
	}

	
	/**	Use the joystick
	 * 	if its currently active
	 */
	@Override
	public boolean onTouchMove(Vector2F point, IMotionEvent event, int fingerId) {
		if (active && fingerId == this.fingerId) {
			
			Vector2F movement = basePoint.vectorTo(point);
			float distance = movement.normalize();
			distance = Math.min(distance, maxDistance);
			movement = movement.multiply(distance);
			
			for (int i = 0; i < listeners.size(); ++i) {
				listeners.get(i).onJoystickMoved(movement);
			}
			
			return true;
		}
		
		return false;
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

	public Vector2F getBasePoint() {
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
