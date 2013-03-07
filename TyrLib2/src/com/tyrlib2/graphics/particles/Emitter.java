package com.tyrlib2.graphics.particles;

import java.util.Random;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.Vector3;

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
	
	// Random generator
	private Random random = new Random();
	
	public Emitter(IParticleFactory particleFactory) {
		this.particleFactory = particleFactory;
		velocity = new Vector3();
	}
	
	public Emitter(Emitter other) {
		particleFactory = other.particleFactory;
		interval = other.interval;
		amount = other.amount;
		velocity = other.velocity;
		randomVelocity = other.randomVelocity;
		randomPos = other.randomPos;
		parent = new SceneNode(other.getRelativePos());
	}
	
	@Override
	public void onUpdate(float time) {
		passedTime += time;
		
		while (passedTime >= interval) {
			passedTime -= interval;
			
			if (!pause && system.allowsMoreParticles()) {
				emit();
			}
		}
	}
	
	/**
	 * Actually emit some particles
	 */
	
	public void emit() {
		
		Vector3 rotatedVelocity = parent.getCachedAbsoluteRot().multiply(velocity);
		
		for (int i = 0; i < amount; ++i) {
			Particle particle = particleFactory.create(system.requestDeadParticle());
			particle.pos = new Vector3(parent.getCachedAbsolutePos());
			
			particle.pos.x += (0.5f-random.nextFloat()) * randomPos.x;
			particle.pos.y += (0.5f-random.nextFloat()) * randomPos.y;
			particle.pos.z += (0.5f-random.nextFloat()) * randomPos.z;
			
			Vector3 v = new Vector3(rotatedVelocity.x + (0.5f-random.nextFloat()) * randomVelocity.x,
									rotatedVelocity.y + (0.5f-random.nextFloat()) * randomVelocity.y,
									rotatedVelocity.z + (0.5f-random.nextFloat()) * randomVelocity.z);
			
			particle.velocity = v;
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
		
	
	public Emitter copy() {
		return new Emitter(this);
	}
}
