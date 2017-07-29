package com.tyrfing.games.tyrlib3.model.graphics.particles;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;

public class ForceAffector extends Affector {

	private Vector3F force;
	private float radialDependency = 0;
	private float power = 0;
	
	public ForceAffector() {
		
	}
	
	public ForceAffector(Vector3F force) {
		this.force = force;
	}
	
	public ForceAffector(float radialDependency, float power) {
		this.radialDependency = radialDependency;
		this.power = power;
	}
	
	
	public ForceAffector(ForceAffector other) {
		force = new Vector3F(other.force);
		radialDependency = other.radialDependency;
		power = other.power;
		setTimeMin(other.getTimeMin());
		setTimeMax(other.getTimeMax());
	}
	
	@Override
	public void onUpdate(Particle particle, float time) {
		
		if (radialDependency != 0) {
			
			Vector3F absolutePos = this.getAbsolutePos();
			float x = particle.floatArray.buffer[particle.dataIndex] - absolutePos.x;
			float y = particle.floatArray.buffer[particle.dataIndex+1] - absolutePos.y;
			float z = particle.floatArray.buffer[particle.dataIndex+2] - absolutePos.z;
			float distance = (float) Math.sqrt(x*x+y*y+z*z);
			
			float factor = (float) (1/(particle.getInertia() * distance*Math.pow(distance, radialDependency)));
			
			
			particle.getAcceleration().x += factor * power * x;
			particle.getAcceleration().y += factor * power * y;
			particle.getAcceleration().z += factor * power * z;
		} else {
			particle.getAcceleration().x += force.x / particle.getInertia();
			particle.getAcceleration().y += force.y / particle.getInertia();
			particle.getAcceleration().z += force.z / particle.getInertia();
		}
		

	}

	@Override
	public Affector copy() {
		return new ForceAffector(this);
	}

	public Vector3F getForce() {
		return force;
	}

	public void setForce(Vector3F force) {
		this.force = force;
	}

	public float getRadialDependency() {
		return radialDependency;
	}

	public void setRadialDependency(float radialDependency) {
		this.radialDependency = radialDependency;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}
	
	

}
