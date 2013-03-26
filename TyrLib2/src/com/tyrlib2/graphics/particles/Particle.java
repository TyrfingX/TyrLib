package com.tyrlib2.graphics.particles;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.materials.PointSpriteMaterial;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.FloatArray;

/**
 * This represents a single particle
 * @author Sascha
 *
 */

public class Particle implements IUpdateable {
	
	protected PointSpriteMaterial material;
	protected Vector3 pos = new Vector3();
	protected Vector3 velocity;
	protected Vector3 acceleration = new Vector3();
	protected Color color = null;
	protected float inertia = 1;
	protected float lifeTime;
	protected float passedTime;
	
	protected int dataIndex;
	protected FloatArray floatArray;
	protected ParticleSystem system;
	
	public Particle() {
	}
	
	@Override
	public void onUpdate(float time) {
		passedTime += time;
		
		velocity.x += time * acceleration.x;
		velocity.y += time * acceleration.y;
		velocity.z += time * acceleration.z;
		
		floatArray.buffer[dataIndex] += time * velocity.x;
		floatArray.buffer[dataIndex + 1] += time * velocity.y;
		floatArray.buffer[dataIndex + 2] +=  time * velocity.z;

	}
	
	@Override
	public boolean isFinished() {
		return (lifeTime != 0 && passedTime >= lifeTime);
	}
	
	public void setMaterial(PointSpriteMaterial material) {
		this.material = material;
		if (color == null) {
			color = material.getColor().copy();
		} else {
			color.r = material.getColor().r;
			color.g = material.getColor().g;
			color.b = material.getColor().b;
			color.a = material.getColor().a;
		}
	}
	
	public PointSpriteMaterial getMaterial() {
		return material;
	}
	
	public float getLifeTime() { return lifeTime; };
	public void setLifeTime(float lifeTime) { this.lifeTime = lifeTime; };
	
	public float getAge() { return passedTime; }
	
	public Particle copy() {
		Particle particle = new Particle();
		
		particle.setMaterial(material);
		particle.setLifeTime(lifeTime);
		
		return particle;
	}

	public Vector3 getPos() {
		pos.x = floatArray.buffer[dataIndex];
		pos.y = floatArray.buffer[dataIndex + 1];
		pos.z = floatArray.buffer[dataIndex + 2];
		return pos;
	}

	public void setPos(Vector3 pos) {
		this.pos = pos;
		
		if (floatArray != null) {
			floatArray.buffer[dataIndex] = pos.x;
			floatArray.buffer[dataIndex + 1] = pos.y;
			floatArray.buffer[dataIndex + 2] =  pos.z;
		}
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3 velocity) {
		this.velocity = velocity;
	}
	
	
	
}
