package com.tyrfing.games.tyrlib3.graphics.particles;


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
		timeMin = other.timeMin;
		timeMax = other.timeMax;
	}
	
	@Override
	public void onUpdate(Particle particle, float time) {
		particle.size += sizeChange * time;
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
