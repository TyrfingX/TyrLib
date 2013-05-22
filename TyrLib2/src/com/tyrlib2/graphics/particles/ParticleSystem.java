package com.tyrlib2.graphics.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.materials.PointSpriteMaterial;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.util.FloatArray;

public abstract class ParticleSystem  extends BoundedSceneObject implements IUpdateable, IRenderable {
	
	protected float[] mvpMatrix = new float[16];
	protected float[] modelMatrix;
	
	protected class ParticleBatch {
		FloatArray particleData ;
		List<Particle> particles = new ArrayList<Particle>();
		PointSpriteMaterial material;
	}
	
	public final static int POSITION_OFFSET = 0;
	public final static int POSITION_SIZE = 3;
	public final static int COLOR_OFFSET = 3;
	public final static int COLOR_SIZE = 4;
	public final static int PARTICLE_DATA_SIZE = 7;
	
	public final static int DEFAULT_SIZE = 16; 
	
	protected FloatBuffer buffer;
	
	protected Stack<Particle> deadParticles;
	
	protected int maxParticles;
	
	protected int countParticles;
	
	protected int steps = 1;
	
	public int getMaxParticles() {
		return maxParticles;
	}
	
	public void setMaxParticles(int maxParticles) {
		this.maxParticles = maxParticles;
		
        ByteBuffer bb = ByteBuffer.allocateDirect(maxParticles * OpenGLRenderer.BYTES_PER_FLOAT * PARTICLE_DATA_SIZE);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        buffer = bb.asFloatBuffer();
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
	
	public abstract void addParticle(Particle particle);
	public abstract ParticleSystem copy();
	
	public int getCountParticles() {
		return countParticles;
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
	
	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	public abstract void addEmitter(Emitter emitter);
	public abstract Emitter getEmitter(int index);
	public abstract int getCountEmitters();
	public abstract void addAffector(Affector affector);
	
}
