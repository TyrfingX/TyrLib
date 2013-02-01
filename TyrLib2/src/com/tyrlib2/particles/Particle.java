package com.tyrlib2.particles;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.materials.PointSpriteMaterial;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

/**
 * This represents a single particle
 * @author Sascha
 *
 */

public class Particle implements IUpdateable {
	
	protected PointSpriteMaterial material;
	protected Vector3 pos;
	protected Vector3 velocity;
	protected Vector3 acceleration = new Vector3();
	protected Color color = new Color(1, 1, 1, 1);
	protected float inertia = 1;
	protected float lifeTime;
	protected float passedTime;
	
	protected int dataIndex;
	protected ParticleSystem system;
	
	
	public Particle() {
	}
	
	@Override
	public void onUpdate(float time) {
		passedTime += time;
		
		velocity.x += time * acceleration.x;
		velocity.y += time * acceleration.y;
		velocity.z += time * acceleration.z;
		
		pos.x += time * velocity.x;
		pos.y += time * velocity.y;
		pos.z += time * velocity.z;
		
	}
	
	@Override
	public boolean isFinished() {
		return (lifeTime != 0 && passedTime >= lifeTime);
	}
	
	public void setMaterial(PointSpriteMaterial material) {
		this.material = material;
		color = material.getColor().copy();
	}
	
	public PointSpriteMaterial getMaterial() {
		return material;
	}
	
	public float getLifeTime() { return lifeTime; };
	public void setLifeTime(float lifeTime) { this.lifeTime = lifeTime; };
	
	public float getAge() { return passedTime; }
	
}
