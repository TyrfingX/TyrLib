package com.tyrfing.games.tyrlib3.model.graphics.particles;

import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneObject;

/**
 * This class takes care of affecting a particle
 * ie changing color, speed, etc
 * @author Sascha
 */

public abstract class Affector extends SceneObject {

	// Particle system this affector belongs to
	protected AParticleSystem system;

	private float timeMin;
	private float timeMax;
	
	public Affector() {
		setTimeMax(Float.MAX_VALUE);
	}

	public void setParticleSystem(AParticleSystem system) { 
		if (system.getParent() != null) {
			this.attachTo(system.getParent());
		}
		this.system = system; 
	};
	
	public abstract void onUpdate(Particle particle, float time);
	
	public void setLifeTimeMin(float timeMin) {
		this.setTimeMin(timeMin);
	}
	
	public void setLifeTimeMax(float timeMax) {
		this.setTimeMax(timeMax);
	}
	
	public float getLifeTimeMin() {
		return getTimeMin();
	}
	
	public float getLifeTimeMax() {
		return getTimeMax();
	}
	
	public abstract Affector copy();

	public float getTimeMin() {
		return timeMin;
	}

	public void setTimeMin(float timeMin) {
		this.timeMin = timeMin;
	}

	public float getTimeMax() {
		return timeMax;
	}

	public void setTimeMax(float timeMax) {
		this.timeMax = timeMax;
	}

}
