package com.tyrfing.games.tyrlib3.model.graphics.particles;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.view.graphics.particles.ARenderableParticleSystem;

public class ColorChanger extends Affector {

	// Color change per second
	private Color colorChange;
	
	public ColorChanger() {
		
	}
	
	public ColorChanger(Color colorChange) {
		this.colorChange = colorChange;
	}
	
	public ColorChanger(ColorChanger other) {
		colorChange = other.colorChange.copy();
		setTimeMin(other.getTimeMin());
		setTimeMax(other.getTimeMax());
	}
	
	@Override
	public void onUpdate(Particle particle, float time) {
		
		particle.floatArray.buffer[particle.dataIndex + 3] += colorChange.r * time;
		particle.floatArray.buffer[particle.dataIndex + 4] += colorChange.g * time;
		particle.floatArray.buffer[particle.dataIndex + 5] += colorChange.b * time;
		particle.floatArray.buffer[particle.dataIndex + 6] += colorChange.a * time;
		
		particle.floatArray.buffer[particle.dataIndex + 3 + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
		particle.floatArray.buffer[particle.dataIndex + 4 + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
		particle.floatArray.buffer[particle.dataIndex + 5 + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
		particle.floatArray.buffer[particle.dataIndex + 6 + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
		
		particle.floatArray.buffer[particle.dataIndex + 3 + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
		particle.floatArray.buffer[particle.dataIndex + 4 + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
		particle.floatArray.buffer[particle.dataIndex + 5 + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
		particle.floatArray.buffer[particle.dataIndex + 6 + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
		
		particle.floatArray.buffer[particle.dataIndex + 3 + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
		particle.floatArray.buffer[particle.dataIndex + 4 + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
		particle.floatArray.buffer[particle.dataIndex + 5 + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
		particle.floatArray.buffer[particle.dataIndex + 6 + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
	}

	@Override
	public Affector copy() {
		return new ColorChanger(this);
	}

	public Color getColorChange() {
		return colorChange;
	}

	public void setColorChange(Color colorChange) {
		this.colorChange = colorChange;
	}
	
	

}
