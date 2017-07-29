package com.tyrfing.games.tyrlib3.model.graphics.particles;

public class SizeChanger extends Affector {

	// Size change per second
	private float sizeChange;
	
	public SizeChanger() {
		
	}
	
	public SizeChanger(float sizeChange) {
		this.sizeChange = sizeChange;
	}
	
	public SizeChanger(SizeChanger other) {
		sizeChange = other.sizeChange;
		setTimeMin(other.getTimeMin());
		setTimeMax(other.getTimeMax());
	}
	
	@Override
	public void onUpdate(Particle particle, float time) {
		particle.setSize(particle.getSize() + sizeChange * time);
	}

	@Override
	public Affector copy() {
		return new SizeChanger(this);
	}

	public float getSizeChange() {
		return sizeChange;
	}

	public void setSizeChange(float sizeChange) {
		this.sizeChange = sizeChange;
	}
	
	

}
