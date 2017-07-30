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
	
	// Maximum amount of particles this emitter is allowed to emit (0 if no restriction)
	protected int max;
	
	// Number of particles that have been emitted by this emitter
	protected int count;
	
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
	
	// The velocity with which this emitter is being moved. Not affected by parent node rotation
	private Vector3 movementVelocity = new Vector3();
	
	// The emitter is pausing
	private boolean pause;
	
	
	private Vector3 emitterPos = new Vector3();
	
	private Vector3 rotatedVelocity = new Vector3();
	
	private Vector3 rotatedRandomVelocity = new Vector3();
	
	// Random generator
	private Random random = new Random();
	
	private Vector3 oldParentPos = null;
	
	private boolean firstEmit = true;
	
	private float passedTime = 0;
	
	public Emitter(IParticleFactory particleFactory) {
		this.particleFactory = particleFactory;
		velocity = new Vector3();
	}
	
	public Emitter(Emitter other) {
		particleFactory = other.particleFactory.oopy();
		interval = other.interval;
		amount = other.amount;
		velocity = new Vector3( other.velocity );
		randomVelocity = new Vector3( other.randomVelocity );
		randomPos = new Vector3( other.randomPos );
		max = other.max;
		parent = new SceneNode(other.getRelativePos());
	}
	
	@Override
	public void onUpdate(float time) {

		passedTime += time;
		
		while (passedTime >= interval) {
			if (!pause && system.allowsMoreParticles() && !firstEmit) {
				emit();
			}
			
			passedTime -= interval;
			
			firstEmit = false;
		}

	}
	
	/**
	 * Actually emit some particles
	 */
	
	public void emit() {
		if (parent != null) {
			parent.getCachedAbsoluteRot().multiply(velocity, rotatedVelocity);
			Vector3.add(rotatedVelocity, movementVelocity, rotatedVelocity);
			
			parent.getCachedAbsoluteRot().multiply(randomVelocity, rotatedRandomVelocity);
			
			Vector3 parentPos = parent.getCachedAbsolutePosVector();
			emitterPos.x = parentPos.x / system.globalScale;
			emitterPos.y = parentPos.y / system.globalScale;
			emitterPos.z = parentPos.z / system.globalScale;
			
			if (oldParentPos != null) {
				float dX = emitterPos.x - oldParentPos.x / system.globalScale;
				float dY = emitterPos.y - oldParentPos.y / system.globalScale;
				float dZ = emitterPos.z - oldParentPos.z / system.globalScale;
				
				emitterPos.x += dX / 2;
				emitterPos.y += dY / 2;
				emitterPos.z += dZ / 2;
			}
			
			for (int i = 0; i < amount && system.allowsMoreParticles() && (max == 0 || count < max); ++i) {
				Particle particle = particleFactory.create(system.requestDeadParticle());
				Vector3 particlePos = particle.getPos();
				particlePos.x = emitterPos.x;
				particlePos.y = emitterPos.y;
				particlePos.z = emitterPos.z;
	
				particlePos.x += (0.5f-random.nextFloat()) * randomPos.x;
				particlePos.y += (0.5f-random.nextFloat()) * randomPos.y;
				particlePos.z += (0.5f-random.nextFloat()) * randomPos.z;
				
				particle.setPos(particlePos);
				
				particle.velocity.x = rotatedVelocity.x + (0.5f-random.nextFloat()) * rotatedRandomVelocity.x;
				particle.velocity.y = rotatedVelocity.y + (0.5f-random.nextFloat()) * rotatedRandomVelocity.y;
				particle.velocity.z = rotatedVelocity.z + (0.5f-random.nextFloat()) * rotatedRandomVelocity.z;
				system.addParticle(particle);
				++count;
			}
			
			if (oldParentPos == null) {
				oldParentPos = parent.getCachedAbsolutePos();
			} else {
				oldParentPos.x = parent.getCachedAbsolutePosVector().x;
				oldParentPos.y = parent.getCachedAbsolutePosVector().y;
				oldParentPos.z = parent.getCachedAbsolutePosVector().z;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return max != 0 && count >= max;
	}
	
	
	public void setParticleSystem(ParticleSystem system) { 
		parent.setRelativePos(parent.getRelativePos().multiply(system.globalScale));
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
	
	public IParticleFactory getFactory() { return particleFactory; }
	
	public void setMovementVelocity(Vector3 movementVelocity) {
		this.movementVelocity = movementVelocity;
	}
	
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

	public void setMax(int max) {
		this.max = max;
	}
}
