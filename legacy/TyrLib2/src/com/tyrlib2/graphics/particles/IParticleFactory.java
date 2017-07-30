package com.tyrlib2.graphics.particles;

public interface IParticleFactory {
	public Particle create(Particle particle);
	public IParticleFactory oopy();
}
