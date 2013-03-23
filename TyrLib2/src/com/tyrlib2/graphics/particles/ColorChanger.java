package com.tyrlib2.graphics.particles;

import com.tyrlib2.util.Color;

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
