package com.tyrlib2.graphics.particles;

import com.tyrlib2.util.Color;

public class ColorChanger extends Affector {

	// Color change per second
	private Color colorChange;
	
	public ColorChanger(Color colorChange) {
		this.colorChange = colorChange;
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

}
