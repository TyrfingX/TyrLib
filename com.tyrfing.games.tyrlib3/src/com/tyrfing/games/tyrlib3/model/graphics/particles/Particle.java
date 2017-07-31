package com.tyrfing.games.tyrlib3.model.graphics.particles;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.game.IUpdateable;
import com.tyrfing.games.tyrlib3.model.math.Quaternion;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.model.struct.FloatArray;
import com.tyrfing.games.tyrlib3.view.graphics.Camera;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.materials.ParticleMaterial;
import com.tyrfing.games.tyrlib3.view.graphics.particles.ARenderableParticleSystem;

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
	private Vector3F acceleration = new Vector3F();
	protected Color color = null;
	private float inertia = 1;
	private float rotation = 0;
	protected float lifeTime;
	private float passedTime;
	private float size;
	protected float scaleSpeed = 1;
	
	public int dataIndex;
	public FloatArray floatArray;
	private AParticleSystem system;
	
	public Particle(float size) {
		this.setSize(size);
	}
	
	public void setScaleSpeed(float scaleSpeed) {
		this.scaleSpeed = scaleSpeed;
	}
	
	@Override
	public void onUpdate(float time) {
		setPassedTime(getPassedTime() + time);
		
		velocity.x += time * getAcceleration().x;
		velocity.y += time * getAcceleration().y;
		velocity.z += time * getAcceleration().z;
		
		pos.x += time * velocity.x;
		pos.y += time * velocity.y;
		pos.z += time * velocity.z;
		
		if (getSystem().isVisible()) {
			floatArray.buffer[dataIndex + ARenderableParticleSystem.UV_OFFSET] = material.getRegion().u2;
			floatArray.buffer[dataIndex + ARenderableParticleSystem.UV_OFFSET + 1] = material.getRegion().v2;
			
			floatArray.buffer[dataIndex + ARenderableParticleSystem.UV_OFFSET + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().u2;
			floatArray.buffer[dataIndex + ARenderableParticleSystem.UV_OFFSET + 1 + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().v1;
			
			floatArray.buffer[dataIndex + ARenderableParticleSystem.UV_OFFSET + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().u1;
			floatArray.buffer[dataIndex + ARenderableParticleSystem.UV_OFFSET + 1 + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().v2;
			
			floatArray.buffer[dataIndex + ARenderableParticleSystem.UV_OFFSET + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().u1;
			floatArray.buffer[dataIndex + ARenderableParticleSystem.UV_OFFSET + 1 + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = material.getRegion().v1;
			
			updateCorners();
		}

	}
	
	@Override
	public boolean isFinished() {
		return (lifeTime != 0 && getPassedTime() >= lifeTime);
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
	
	public float getAge() { return getPassedTime(); }
	
	public Particle copy() {
		Particle particle = new Particle(getSize());
		
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
	
	public void updateCorners() {
		Camera cam = SceneManager.getInstance().getActiveCamera();
		Vector3F camPos = cam.getAbsolutePos();
		Vector3F worldUp = cam.getWorldUpVector();
		
		if (getSystem().isScreenSpace()) {
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
		
		if (getRotation() != 0) {
			Quaternion.fromAxisAngle(deltaX, deltaY, deltaZ, getRotation(), rotationUp).multiplyNoTmp(up);
		}
		
		float scale = 1;
		if (getSystem().isScale()) {
			float distance = Vector3F.length(deltaX, deltaY, deltaZ);
			if (distance < getSystem().getMaxDistance()) {
				scale = 1 / (1.0f - distance / (getSystem().getMaxDistance()/scaleSpeed));
				scale = Math.max(scale, getSystem().getMinScale());
				scale = Math.min(scale, getSystem().getMaxScale()*scaleSpeed);
			} else {
				scale = getSystem().getMaxScale()*scaleSpeed;
			}
		}
		
		float size = this.getSize() * scale;
		
		if (getSystem().isScreenSpace()) {
			right.x = 1;
			right.y = 0;
			right.z = 0;
		} else {
			Vector3F.cross(right, deltaX, deltaY, deltaZ, up.x, up.y, up.z);
			right.normalize();	
		}
		
		int globalScale = getSystem().getGlobalScale();
		
		floatArray.buffer[dataIndex] = (pos.x - right.x * size/2 - up.x * size/2) * globalScale;
		floatArray.buffer[dataIndex + 1] = (pos.y - right.y * size/2 - up.y * size/2) * globalScale;
		floatArray.buffer[dataIndex + 2] =  (pos.z - right.z * size/2 - up.z * size/2) * globalScale;
		
		floatArray.buffer[dataIndex + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.x - right.x * size/2 + up.x * size/2) * globalScale;
		floatArray.buffer[dataIndex + 1 + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.y - right.y * size/2 + up.y * size/2) * globalScale;
		floatArray.buffer[dataIndex + 2 + ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.z - right.z * size/2 + up.z * size/2) * globalScale;
		
		floatArray.buffer[dataIndex + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.x + right.x * size/2 - up.x * size/2) * globalScale;
		floatArray.buffer[dataIndex + 1 + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.y + right.y * size/2 - up.y * size/2) * globalScale;
		floatArray.buffer[dataIndex + 2 + 2*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] =  (pos.z + right.z * size/2 - up.z * size/2) * globalScale;
		
		floatArray.buffer[dataIndex + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.x + right.x * size/2 + up.x * size/2) * globalScale;
		floatArray.buffer[dataIndex + 1 + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.y + right.y * size/2 + up.y * size/2) * globalScale;
		floatArray.buffer[dataIndex + 2 + 3*ARenderableParticleSystem.PARTICLE_DATA_SIZE/4] = (pos.z + right.z * size/2 + up.z * size/2) * globalScale;
	}

	public Vector3F getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3F velocity) {
		this.velocity.set(velocity);
	}

	public float getInertia() {
		return inertia;
	}

	public void setInertia(float inertia) {
		this.inertia = inertia;
	}

	public Vector3F getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector3F acceleration) {
		this.acceleration = acceleration;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}
	
	public Color getColor() {
		return color;
	}

	public AParticleSystem getSystem() {
		return system;
	}

	public void setSystem(AParticleSystem system) {
		this.system = system;
	}

	public float getPassedTime() {
		return passedTime;
	}

	public void setPassedTime(float passedTime) {
		this.passedTime = passedTime;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
}
