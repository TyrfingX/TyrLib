package com.tyrfing.games.tyrlib3.graphics.particles;

public interface IParticleFactory {
	public Particle create(Particle particle);
	public IParticleFactory oopy();
}
