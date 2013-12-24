package com.tyrlib2.movement;

import com.tyrlib2.game.IUpdateable;

/**
 * Manages the speed of an object
 * @author Sascha
 *
 */

public class Speed implements IUpdateable {
	public float speed;
	public float acceleration;
	
	public Speed(float speed) {
		this.speed = speed;
	}
	
	/**
	 * Accelerates the object.
	 * @param acceleration
	 * @param maxSpeed
	 */
	
	public void accelerate(float acceleration) {
		this.acceleration = acceleration;
	}

	@Override
	public void onUpdate(float time) {
		speed += acceleration * time;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}