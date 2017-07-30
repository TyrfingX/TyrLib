package com.tyrlib2.graphics.particles;


public class RotationChanger extends Affector {

	// Size change per second
	private float rotationChange;
	
	public RotationChanger() {
		
	}
	
	public RotationChanger(float sizeChange) {
		this.rotationChange = sizeChange;
	}
	
	public RotationChanger(RotationChanger other) {
		rotationChange = other.rotationChange;
		timeMin = other.timeMin;
		timeMax = other.timeMax;
	}
	
	@Override
	public void onUpdate(Particle particle, float time) {
		particle.rotation += rotationChange * time;
	}

	@Override
	public Affector copy() {
		return new RotationChanger(this);
	}

	public float getSizeChange() {
		return rotationChange;
	}

	public void setSizeChange(float sizeChange) {
		this.rotationChange = sizeChange;
	}
	
	

}
