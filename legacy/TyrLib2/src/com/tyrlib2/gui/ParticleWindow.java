package com.tyrlib2.gui;

import com.tyrlib2.graphics.particles.ComplexParticleSystem;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector2;

public class ParticleWindow extends Window {

	private ComplexParticleSystem system;
	
	public ParticleWindow(String name, Vector2 pos, String source) {
		super(name, new Vector2());
		
		Viewport viewport = SceneManager.getInstance().getViewport();
		
		system = (ComplexParticleSystem) SceneManager.getInstance().createParticleSystem(source);
		addComponent(system);
		
		setRelativePos(pos);
		system.setGlobalScale(viewport.getHeight());
	}
	
	@Override
	public void onUpdate(float time) {
		super.onUpdate(time);
		system.onUpdate(time);
		
		AABB bb = system.getBoundingBox();
		Vector2 size = new Vector2(bb.max.x - bb.min.x, bb.max.y - bb.min.y);
		Viewport viewport = SceneManager.getInstance().getViewport();
		size.x /= viewport.getWidth();
		size.y /= viewport.getHeight();
		
		setSize(size);
	}

	public boolean isSaturated() {
		return system.isSaturated();
	}
	
}
