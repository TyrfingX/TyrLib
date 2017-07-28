package com.tyrfing.games.tyrlib3.graphics.particles;

import com.tyrfing.games.tyrlib3.util.Color;

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
		timeMin = other.timeMin;
		timeMax = other.timeMax;
	}
	
	@Override
	public void onUpdate(Particle particle, float time) {
		
		particle.floatArray.buffer[particle.dataIndex + 3] += colorChange.r * time;
		particle.floatArray.buffer[particle.dataIndex + 4] += colorChange.g * time;
		particle.floatArray.buffer[particle.dataIndex + 5] += colorChange.b * time;
		particle.floatArray.buffer[particle.dataIndex + 6] += colorChange.a * time;
		
		particle.floatArray.buffer[particle.dataIndex + 3 + ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
		particle.floatArray.buffer[particle.dataIndex + 4 + ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
		particle.floatArray.buffer[particle.dataIndex + 5 + ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
		particle.floatArray.buffer[particle.dataIndex + 6 + ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
		
		particle.floatArray.buffer[particle.dataIndex + 3 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
		particle.floatArray.buffer[particle.dataIndex + 4 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
		particle.floatArray.buffer[particle.dataIndex + 5 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
		particle.floatArray.buffer[particle.dataIndex + 6 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
		
		particle.floatArray.buffer[particle.dataIndex + 3 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 3];
		particle.floatArray.buffer[particle.dataIndex + 4 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 4];
		particle.floatArray.buffer[particle.dataIndex + 5 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 5];
		particle.floatArray.buffer[particle.dataIndex + 6 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = particle.floatArray.buffer[particle.dataIndex + 6];
		
		//color.clamp();
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
