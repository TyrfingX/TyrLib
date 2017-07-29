package com.tyrfing.games.tyrlib3.view.gui;

import com.tyrfing.games.tyrlib3.graphics.particles.ComplexParticleSystem;
import com.tyrfing.games.tyrlib3.graphics.renderer.Viewport;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.math.AABB;
import com.tyrfing.games.tyrlib3.math.Vector2F;

public class ParticleWindow extends Window {

	private ComplexParticleSystem system;
	
	public ParticleWindow(String name, Vector2F pos, String source) {
		super(name, new Vector2F());
		
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
		Vector2F size = new Vector2F(bb.max.x - bb.min.x, bb.max.y - bb.min.y);
		Viewport viewport = SceneManager.getInstance().getViewport();
		size.x /= viewport.getWidth();
		size.y /= viewport.getHeight();
		
		setSize(size);
	}

	public boolean isSaturated() {
		return system.isSaturated();
	}
	
}
