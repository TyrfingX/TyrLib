package com.tyrfing.games.tyrlib3.model.graphics.particles;

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
		setTimeMin(other.getTimeMin());
		setTimeMax(other.getTimeMax());
	}
	
	@Override
	public void onUpdate(Particle particle, float time) {
		particle.setRotation(particle.getRotation() + rotationChange * time);
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
