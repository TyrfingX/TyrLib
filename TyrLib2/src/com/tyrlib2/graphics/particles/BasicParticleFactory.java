package com.tyrlib2.graphics.particles;

import com.tyrlib2.graphics.materials.PointSpriteMaterial;

public class BasicParticleFactory implements IParticleFactory {

	private float lifeTime;
	
	private PointSpriteMaterial material;

	public BasicParticleFactory(float lifeTime) {
		this.lifeTime = lifeTime;
	}

	
	@Override
	public Particle create() {
		Particle particle = new Particle();
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
