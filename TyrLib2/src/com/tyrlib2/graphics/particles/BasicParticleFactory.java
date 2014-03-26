package com.tyrlib2.graphics.particles;

import com.tyrlib2.graphics.materials.ParticleMaterial;

public class BasicParticleFactory implements IParticleFactory {

	private float lifeTime;
	private float size;
	
	private ParticleMaterial material;

	public BasicParticleFactory(float lifeTime, float size) {
		this.lifeTime = lifeTime;
		this.size = size;
	}

	
	@Override
	public Particle create(Particle particle) {
		if (particle == null) {
			particle = new Particle(size);
		} else {
			particle.inertia = 1;
			particle.acceleration.x = 0;
			particle.acceleration.y = 0;
			particle.acceleration.z = 0;
			particle.passedTime = 0;
			particle.size = size;
			particle.rotation = 0;
			particle.floatArray = null;
		}
		
		particle.setMaterial(material);
		particle.setLifeTime(lifeTime);
		return particle;
	}
	
	public float getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(float lifeTime) {
		this.lifeTime = lifeTime;
	}

	public ParticleMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ParticleMaterial material) {
		this.material = material;
	}


}
