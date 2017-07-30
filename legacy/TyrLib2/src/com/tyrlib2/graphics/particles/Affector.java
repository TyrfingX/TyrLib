package com.tyrlib2.graphics.particles;

import com.tyrlib2.graphics.scene.SceneObject;

/**
 * This class takes care of affecting a particle
 * ie changing color, speed, etc
 * @author Sascha
 */

public abstract class Affector extends SceneObject {

	// Particle system this affector belongs to
	protected ParticleSystem system;

	protected float timeMin;
	protected float timeMax;
	
	public Affector() {
		timeMax = Float.MAX_VALUE;
	}

	public void setParticleSystem(ParticleSystem system) { 
		if (system.getParent() != null) {
			this.attachTo(system.getParent());
		}
		this.system = system; 
	};
	
	public abstract void onUpdate(Particle particle, float time);
	
	public void setLifeTimeMin(float timeMin) {
		this.timeMin = timeMin;
	}
	
	public void setLifeTimeMax(float timeMax) {
		this.timeMax = timeMax;
	}
	
	public float getLifeTimeMin() {
		return timeMin;
	}
	
	public float getLifeTimeMax() {
		return timeMax;
	}
	
	public ParticleSystem getParticleSystem() { return system; };
	
	public abstract Affector copy();

}
