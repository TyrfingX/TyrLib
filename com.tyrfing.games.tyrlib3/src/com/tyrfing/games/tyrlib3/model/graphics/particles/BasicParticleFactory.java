package com.tyrfing.games.tyrlib3.model.graphics.particles;

import com.tyrfing.games.tyrlib3.view.graphics.materials.ParticleMaterial;

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
			particle.setInertia(1);
			particle.getAcceleration().x = 0;
			particle.getAcceleration().y = 0;
			particle.getAcceleration().z = 0;
			particle.setPassedTime(0);
			particle.setSize(size);
			particle.setRotation(0);
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


	@Override
	public IParticleFactory oopy() {
		BasicParticleFactory f = new BasicParticleFactory(this.lifeTime, this.size);
		f.setMaterial(material);
		return f;
	}


}
