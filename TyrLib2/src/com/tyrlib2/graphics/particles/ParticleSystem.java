package com.tyrlib2.graphics.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.materials.ParticleMaterial;
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
		ParticleMaterial material;
	}
	
	public final static int POSITION_OFFSET = 0;
	public final static int POSITION_SIZE = 3;
	public final static int COLOR_OFFSET = 3;
	public final static int COLOR_SIZE = 4;
	public final static int UV_OFFSET = 7;
	public final static int UV_SIZE = 2;
	public final static int PARTICLE_DATA_SIZE = 36;
	public final static short[] DRAW_ORDER = {0,1,2,2,1,3};
	
	public final static int DEFAULT_SIZE = PARTICLE_DATA_SIZE; 
	
	protected FloatBuffer buffer;
	protected ShortBuffer buffer2;
	
	protected Stack<Particle> deadParticles;
	
	protected int maxParticles;
	
	protected int countParticles;
	
	protected int steps = 1;
	
	protected boolean visible;
	
	public int getMaxParticles() {
		return maxParticles;
	}
	
	public void setMaxParticles(int maxParticles) {
		this.maxParticles = maxParticles;
		
        ByteBuffer bb = ByteBuffer.allocateDirect(maxParticles * OpenGLRenderer.BYTES_PER_FLOAT * PARTICLE_DATA_SIZE);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        buffer = bb.asFloatBuffer();
        
        ByteBuffer bb2 = ByteBuffer.allocateDirect(DRAW_ORDER.length * maxParticles * 2);
        
        // use the device hardware's native byte order
        bb2.order(ByteOrder.nativeOrder());
        buffer2 = bb2.asShortBuffer();
        
        for (int i = 0; i < maxParticles; ++i) {
        	short[] drawOrder = { 	(short) (DRAW_ORDER[0]+4*i), 
        							(short) (DRAW_ORDER[1]+4*i),
        							(short) (DRAW_ORDER[2]+4*i),
        							(short) (DRAW_ORDER[3]+4*i),
        							(short) (DRAW_ORDER[4]+4*i),
        							(short) (DRAW_ORDER[5]+4*i) };
        	buffer2.put(drawOrder);
        }
        
        buffer2.position(0);
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
