package com.tyrlib2.graphics.particles;

import com.tyrlib2.graphics.materials.PointSpriteMaterial;
import com.tyrlib2.util.Color;

public class BasicParticleFactory implements IParticleFactory {

	private float lifeTime;
	
	private PointSpriteMaterial material;

	public BasicParticleFactory(float lifeTime) {
		this.lifeTime = lifeTime;
	}

	
	@Override
	public Particle create(Particle particle) {
		if (particle == null) {
			particle = new Particle();
		} else {
			particle.inertia = 1;
			particle.acceleration.x = 0;
			particle.acceleration.y = 0;
			particle.acceleration.z = 0;
			particle.color = Color.WHITE;
			particle.passedTime = 0;
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

	public PointSpriteMaterial getMaterial() {
		return material;
	}

	public void setMaterial(PointSpriteMaterial material) {
		this.material = material;
	}


}
