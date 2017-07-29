package com.tyrfing.games.tyrlib3.model.graphics.particles;

import java.util.Stack;

import com.tyrfing.games.tyrlib3.model.game.IUpdateable;
import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;

public abstract class AParticleSystem extends BoundedSceneObject implements IUpdateable {
	
	protected int maxParticles;
	protected int countParticles;
	protected int globalScale = 1;
	protected boolean screenSpace;
	
	protected boolean scale;
	protected float minScale;
	protected float maxScale;
	protected float maxDistance;
	
	protected boolean visible;
	
	protected Stack<Particle> deadParticles;
	
	public abstract void addParticle(Particle particle);
	
	public abstract void addEmitter(Emitter emitter);
	public abstract Emitter getEmitter(int index);
	public abstract int getCountEmitters();
	public abstract void addAffector(Affector affector);
	
	public void setMaxParticles(int maxParticles) {
		this.maxParticles = maxParticles;
	}
	
	public int getMaxParticles() {
		return maxParticles;
	}
	
	public int getCountParticles() {
		return countParticles;
	}
	
	public boolean allowsMoreParticles() {
		if (maxParticles == 0) return true;
		return (maxParticles > countParticles);
	}
	
	protected Particle requestDeadParticle() {
		if (!deadParticles.empty()) {
			return deadParticles.pop();
		}
		
		return null;
	}
	
	public void setScreenSpace(boolean screenSpace) {
		this.screenSpace = screenSpace;
	}
	
	public boolean isScreenSpace() {
		return screenSpace;
	}

	public void setGlobalScale(int scale) {
		this.globalScale = scale;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean state)  {
		this.visible = state;
	}
	
	public boolean isScale() {
		return scale;
	}
	
	public float getMaxDistance() {
		return maxDistance;
	}

	public float getMaxScale() {
		return maxScale;
	}

	public float getMinScale() {
		return minScale;
	}

	public int getGlobalScale() {
		return globalScale;
	}
	
}
