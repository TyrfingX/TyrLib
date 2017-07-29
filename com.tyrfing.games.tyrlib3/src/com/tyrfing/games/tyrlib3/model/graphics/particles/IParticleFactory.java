package com.tyrfing.games.tyrlib3.model.graphics.particles;

public interface IParticleFactory {
	public Particle create(Particle particle);
	public IParticleFactory oopy();
}
