package com.tyrfing.games.tyrlib3.graphics.particles;

import com.tyrfing.games.tyrlib3.game.IUpdateable;
import com.tyrfing.games.tyrlib3.graphics.materials.ParticleMaterial;
import com.tyrfing.games.tyrlib3.graphics.renderer.Camera;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.math.Quaternion;
import com.tyrfing.games.tyrlib3.math.Vector3F;
import com.tyrfing.games.tyrlib3.util.Color;
import com.tyrfing.games.tyrlib3.util.FloatArray;

/**
 * This represents a single particle
 * @author Sascha
 *
 */

public class Particle implements IUpdateable {
	
	protected ParticleMaterial material;
	protected Vector3F pos = new Vector3F();
	protected Vector3F up = new Vector3F();
	protected Vector3F right = new Vector3F();
	protected Quaternion rotationUp = new Quaternion();
	protected Vector3F velocity = new Vector3F();
	protected Vector3F acceleration = new Vector3F();
	protected Color color = null;
	protected float inertia = 1;
	protected float rotation = 0;
	protected float lifeTime;
	protected float passedTime;
	protected float size;
	protected float scaleSpeed = 1;
	
	public int dataIndex;
	public FloatArray floatArray;
	protected ParticleSystem system;
	
	public Particle(float size) {
		this.size = size;
	}
	
	public void setScaleSpeed(float scaleSpeed) {
		this.scaleSpeed = scaleSpeed;
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
		
		if (system.visible) {
			floatArray.buffer[dataIndex + ParticleSystem.UV_OFFSET] = material.getRegion().u2;
			floatArray.buffer[dataIndex + ParticleSystem.UV_OFFSET + 1] = material.getRegion().v2;
			
			floatArray.buffer[dataIndex + ParticleSystem.UV_OFFSET + ParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().u2;
			floatArray.buffer[dataIndex + ParticleSystem.UV_OFFSET + 1 + ParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().v1;
			
			floatArray.buffer[dataIndex + ParticleSystem.UV_OFFSET + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().u1;
			floatArray.buffer[dataIndex + ParticleSystem.UV_OFFSET + 1 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().v2;
			
			floatArray.buffer[dataIndex + ParticleSystem.UV_OFFSET + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().u1;
			floatArray.buffer[dataIndex + ParticleSystem.UV_OFFSET + 1 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().v1;
			
			updateCorners();
		}

	}
	
	@Override
	public boolean isFinished() {
		return (lifeTime != 0 && passedTime >= lifeTime);
	}
	
	public void setMaterial(ParticleMaterial material) {
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
	
	public ParticleMaterial getMaterial() {
		return material;
	}
	
	public float getLifeTime() { return lifeTime; };
	public void setLifeTime(float lifeTime) { this.lifeTime = lifeTime; };
	
	public float getAge() { return passedTime; }
	
	public Particle copy() {
		Particle particle = new Particle(size);
		
		particle.setMaterial(material);
		particle.setLifeTime(lifeTime);
		
		return particle;
	}

	public Vector3F getPos() {
		return pos;
	}

	public void setPos(Vector3F pos) {
		this.pos.x = pos.x;
		this.pos.y = pos.y;
		this.pos.z = pos.z;
		
		if (floatArray != null) {
			updateCorners();
		}
	}
	
	protected void updateCorners() {
		Camera cam = SceneManager.getInstance().getActiveCamera();
		Vector3F camPos = cam.getAbsolutePos();
		Vector3F worldUp = cam.getWorldUpVector();
		
		if (system.isScreenSpace()) {
			up.x = 0;
			up.y = 1;
			up.z = 0;
		} else {
			up.x = worldUp.x;
			up.y = worldUp.y;
			up.z = worldUp.z;
		}
		
		float deltaX = camPos.x - pos.x;
		float deltaY = camPos.y - pos.y;
		float deltaZ = camPos.z - pos.z;
		
		if (rotation != 0) {
			Quaternion.fromAxisAngle(deltaX, deltaY, deltaZ, rotation, rotationUp).multiplyNoTmp(up);
		}
		
		
		float scale = 1;
		if (system.scale) {
			float distance = Vector3F.length(deltaX, deltaY, deltaZ);
			if (distance < system.maxDistance) {
				scale = 1 / (1.0f - distance / (system.maxDistance/scaleSpeed));
				scale = Math.max(scale, system.minScale);
				scale = Math.min(scale, system.maxScale*scaleSpeed);
			} else {
				scale = system.maxScale*scaleSpeed;
			}
		}
		
		float size = this.size * scale;
		
		if (system.isScreenSpace()) {
			right.x = 1;
			right.y = 0;
			right.z = 0;
		} else {
			Vector3F.cross(right, deltaX, deltaY, deltaZ, up.x, up.y, up.z);
			right.normalize();	
		}
		
		int globalScale = system.globalScale;
		
		floatArray.buffer[dataIndex] = (pos.x - right.x * size/2 - up.x * size/2) * globalScale;
		floatArray.buffer[dataIndex + 1] = (pos.y - right.y * size/2 - up.y * size/2) * globalScale;
		floatArray.buffer[dataIndex + 2] =  (pos.z - right.z * size/2 - up.z * size/2) * globalScale;
		
		floatArray.buffer[dataIndex + ParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.x - right.x * size/2 + up.x * size/2) * globalScale;
		floatArray.buffer[dataIndex + 1 + ParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.y - right.y * size/2 + up.y * size/2) * globalScale;
		floatArray.buffer[dataIndex + 2 + ParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.z - right.z * size/2 + up.z * size/2) * globalScale;
		
		floatArray.buffer[dataIndex + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.x + right.x * size/2 - up.x * size/2) * globalScale;
		floatArray.buffer[dataIndex + 1 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.y + right.y * size/2 - up.y * size/2) * globalScale;
		floatArray.buffer[dataIndex + 2 + 2*ParticleSystem.PARTICLE_DATA_SIZE/4] =  (pos.z + right.z * size/2 - up.z * size/2) * globalScale;
		
		floatArray.buffer[dataIndex + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.x + right.x * size/2 + up.x * size/2) * globalScale;
		floatArray.buffer[dataIndex + 1 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.y + right.y * size/2 + up.y * size/2) * globalScale;
		floatArray.buffer[dataIndex + 2 + 3*ParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.z + right.z * size/2 + up.z * size/2) * globalScale;
	}

	public Vector3F getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3F velocity) {
		this.velocity.set(velocity);
	}
	
	
	
}
