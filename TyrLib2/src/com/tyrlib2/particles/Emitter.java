package com.tyrlib2.particles;

import java.util.Random;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.scene.SceneObject;

/**
 * This represents an emitter of particles
 * @author Sascha
 *
 */

public class Emitter extends SceneObject implements IUpdateable {

	// The interval in which new particles are emitted
	protected float interval;
	
	// The amount of particles emitted in each step
	protected int amount;
	
	// Accumulated time
	private float passedTime;
	
	// A factory to create the particles
	private IParticleFactory particleFactory;
	
	// The Particle system this emitter belongs to
	private ParticleSystem system;
	
	// The initial velocity of the particles shot out by this emitter
	private Vector3 velocity = new Vector3();
	
	// Random velocity added to the particle on creation
	private Vector3 randomVelocity = new Vector3();
	
	// Random position added to the particle on creation
	private Vector3 randomPos = new Vector3();
	
	// The emitter is pausing
	private boolean pause;
	
	public Emitter(IParticleFactory particleFactory) {
		this.particleFactory = particleFactory;
		velocity = new Vector3();
	}
	
	@Override
	public void onUpdate(float time) {
		passedTime += time;
		
		while (passedTime >= interval) {
			passedTime -= interval;
			
			if (!pause && system.getMaxParticles() > system.getCountParticles()) {
				emit();
			}
		}
	}
	
	/**
	 * Actually emit some particles
	 */
	
	public void emit() {
		for (int i = 0; i < amount; ++i) {
			Particle particle = particleFactory.create();
			particle.pos = new Vector3(parent.getAbsolutePos());
			
			Random random = new Random();
			
			particle.pos.x += (float) (random.nextGaussian() * randomPos.x);
			particle.pos.y += (float) (random.nextGaussian() * randomPos.y);
			particle.pos.z += (float) (random.nextGaussian() * randomPos.z);
			
			Vector3 v = new Vector3(velocity);
			v.x += (float) (random.nextGaussian() * randomVelocity.x);
			v.y += (float) (random.nextGaussian() * randomVelocity.y);
			v.z += (float) (random.nextGaussian() * randomVelocity.z);
			
			particle.velocity = parent.getAbsoluteRot().multiply(v);
			system.addParticle(particle);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	
	public void setParticleSystem(ParticleSystem system) { 
		if (system.getParent() != null) {
			if (parent != null) {
				system.getParent().attachChild(parent);
			}
		}
		this.system = system; 
	};
	
	public ParticleSystem getParticleSystem() { return system; };
	
	public void setInterval(float interval) { this.interval = interval; };
	public float getInterval() { return interval; };

	public void setAmount(int amount) { this.amount = amount; };
	public int getAmount() { return amount; };
	
	public void setVelocity(Vector3 velocity) { this.velocity = velocity; };
	public Vector3 getVelocity() { return velocity; };
	
	public void setRandomVelocity(Vector3 randomVelocity) { this.randomVelocity = randomVelocity; };
	public Vector3 getRandomVelocity() { return randomVelocity; };

	public void setRandomPos(Vector3 randomPos) { this.randomPos = randomPos; };
	public Vector3 getRandomPos() { return randomPos; };
	
	public void pause() {
		pause = true;
	}
	
	public void unPause() {
		pause = false;
	}
	
	public boolean isPaused() {
		return pause;
	}
		
}
