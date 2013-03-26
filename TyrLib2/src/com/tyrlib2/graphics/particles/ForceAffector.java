package com.tyrlib2.graphics.particles;

import android.util.FloatMath;

import com.tyrlib2.math.Vector3;

public class ForceAffector extends Affector {

	private Vector3 force;
	private float radialDependency = 0;
	private float power = 0;
	
	public ForceAffector() {
		
	}
	
	public ForceAffector(Vector3 force) {
		this.force = force;
	}
	
	public ForceAffector(float radialDependency, float power) {
		this.radialDependency = radialDependency;
		this.power = power;
	}
	
	
	public ForceAffector(ForceAffector other) {
		force = other.force;
		radialDependency = other.radialDependency;
		power = other.power;
		timeMin = other.timeMin;
		timeMax = other.timeMax;
	}
	
	@Override
	public void onUpdate(Particle particle, float time) {
		
		if (radialDependency != 0) {
			
			Vector3 absolutePos = this.getAbsolutePos();
			float x = particle.floatArray.buffer[particle.dataIndex] - absolutePos.x;
			float y = particle.floatArray.buffer[particle.dataIndex+1] - absolutePos.y;
			float z = particle.floatArray.buffer[particle.dataIndex+2] - absolutePos.z;
			float distance = FloatMath.sqrt(x*x+y*y+z*z);
			
			float factor = (float) (1/(particle.inertia * distance*Math.pow(distance, radialDependency)));
			
			
			particle.acceleration.x += factor * power * x;
			particle.acceleration.y += factor * power * y;
			particle.acceleration.z += factor * power * z;
		} else {
			particle.acceleration.x += force.x / particle.inertia;
			particle.acceleration.y += force.y / particle.inertia;
			particle.acceleration.z += force.z / particle.inertia;
		}
		

	}

	@Override
	public Affector copy() {
		return new ForceAffector(this);
	}

	public Vector3 getForce() {
		return force;
	}

	public void setForce(Vector3 force) {
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
