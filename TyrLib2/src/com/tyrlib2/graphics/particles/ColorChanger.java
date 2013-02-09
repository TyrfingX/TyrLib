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
		Color color = particle.color;
		color.r += colorChange.r * time;
		color.g += colorChange.g * time;
		color.b += colorChange.b * time;
		color.a += colorChange.a * time;
		
		color.clamp();
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
