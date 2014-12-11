package com.tyrlib2.graphics.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Stack;

import com.tyrlib2.graphics.materials.ParticleMaterial;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.util.FloatArray;

/**
 * A 1 Emitter and 1 Affector which is always applicable particle system, 
 * it therefore also only has exactly 1 particle batch
 * @author Sascha
 *
 */

public class SimpleParticleSystem extends ParticleSystem {
	
	private Affector affector;
	private Emitter emitter;
	
	private ParticleBatch particleBatch;
	
	private AABB boundingBox;
	
	private int colorHandle;
	private int insertionID;
	
	public SimpleParticleSystem() {
		modelMatrix = SceneManager.getInstance().getRootSceneNode().getModelMatrix();
		deadParticles = new Stack<Particle>();
		boundingBox = new AABB();
	}
	
	public SimpleParticleSystem(int maxParticles) {
		this();
		this.maxParticles = maxParticles;
		
        ByteBuffer bb = ByteBuffer.allocateDirect(maxParticles * OpenGLRenderer.BYTES_PER_FLOAT * PARTICLE_DATA_SIZE);
        
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        buffer = bb.asFloatBuffer();
	}
	
	@Override
	public void onUpdate(float time) {
		
		time /= steps;
		
		boundingBox.min.x = Float.MAX_VALUE;
		boundingBox.min.y = Float.MAX_VALUE;
		boundingBox.min.z = Float.MAX_VALUE;
		
		boundingBox.max.x = -Float.MAX_VALUE;
		boundingBox.max.y = -Float.MAX_VALUE;
		boundingBox.max.z = -Float.MAX_VALUE;
		
		// Update everything
		
		for (int l = 0; l < steps; ++l) {

			for (int i = 0; i < countParticles; ++i) {
				
				ParticleMaterial material = particleBatch.material;
				List<Particle> particles = particleBatch.particles;
				
				Particle particle = particles.get(i);
				particle.onUpdate(time);
				
				particle.acceleration.x = 0;
				particle.acceleration.y = 0;
				particle.acceleration.z = 0;
				
				if (particle.isFinished()) {
					removeParticle(i, material);
					--i;
				} else {
					checkBoundingBox(particle);
					affector.onUpdate(particle, time);
				}
			}
			
			emitter.onUpdate(time);
		
		}
		
		if (isBoundingBoxVisible()) {
			updateBoundingBox();
		}
	}
	
	
	private void checkBoundingBox(Particle particle) {
		if (particle.pos.x > boundingBox.max.x) boundingBox.max.x = particle.pos.x;
		if (particle.pos.y > boundingBox.max.y) boundingBox.max.y = particle.pos.y;
		if (particle.pos.z > boundingBox.max.z) boundingBox.max.z = particle.pos.z;
		
		if (particle.pos.x < boundingBox.min.x) boundingBox.min.x = particle.pos.x;
		if (particle.pos.y < boundingBox.min.y) boundingBox.min.y = particle.pos.y;
		if (particle.pos.z < boundingBox.min.z) boundingBox.min.z = particle.pos.z;
	}
	
	public void addParticle(Particle particle) {
		
		ParticleMaterial material = particle.getMaterial();
		
		if (particleBatch == null) {
			particleBatch = new ParticleBatch();
			particleBatch.material = material;
			particleBatch.particleData = new FloatArray(maxParticles);
		}
		
		particle.system = this;

		particleBatch.particles.add(particle);
		
		FloatArray particleData = particleBatch.particleData;
		particle.dataIndex = particleData.getSize();
		particle.floatArray = particleData;
		
		particleData.pushBack(particle.pos.x);
		particleData.pushBack(particle.pos.y);
		particleData.pushBack(particle.pos.z);
		particleData.pushBack(particle.color.r);
		particleData.pushBack(particle.color.g);
		particleData.pushBack(particle.color.b);
		particleData.pushBack(particle.color.a);
		
		countParticles++;
	}
	
	private void removeParticle(int index, ParticleMaterial material) {
			
		List<Particle> particles = particleBatch.particles;
		Particle particle = particles.get(index);
		deadParticles.push(particle);
		
		int indexLastParticle = particles.size() - 1;
		Particle lastParticle = particles.get(indexLastParticle);
		
		FloatArray particleData = particleBatch.particleData;
		
		System.arraycopy(particleData.buffer, lastParticle.dataIndex, 
						 particleData.buffer, particle.dataIndex, 
						 PARTICLE_DATA_SIZE); 
		particleData.popBack(PARTICLE_DATA_SIZE);
		
		lastParticle.dataIndex = particle.dataIndex;
		particles.set(index, lastParticle);
		particles.remove(indexLastParticle);
		
		countParticles--;
		
	}
	
	public void addEmitter(Emitter emitter) {
		this.emitter = emitter;
		emitter.setParticleSystem(this);
	}
	
	public Emitter getEmitter(int index) {
		return emitter;
	}
	
	public int getCountEmitters() {
		return 1;
	}
	
	public void addAffector(Affector affector) {
		this.affector = affector;
		affector.setParticleSystem(this);
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		
		parent.attachChild(emitter.getParent());
		affector.attachTo(node);
		
	}
	

	@Override
	public SceneNode detach() {

		emitter.detach();
		affector.detach();

		return super.detach();	
	}
	
	@Override
	public void renderShadow(float[] vpMatrix) {
		
	}
	
	@Override
	public void render(float[] vpMatrix) {
		
		if (parent == null) {
			return;
		}
			
		if (countParticles > 0) {
			
			ParticleMaterial material = particleBatch.material;
			FloatArray particleData = particleBatch.particleData;

			int size = particleData.getSize();
			
	        buffer.clear();
	        buffer.put(particleData.buffer, 0, size);
	        buffer.position(0);
	        				
			material.getProgram().use();
			
			material.render(null, vpMatrix);
			
			// Pass in the position.
			TyrGL.glVertexAttribPointer(material.getPositionHandle(), POSITION_SIZE, TyrGL.GL_FLOAT, false,
		    							 PARTICLE_DATA_SIZE * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
			
			TyrGL.glEnableVertexAttribArray(material.getPositionHandle());  
			
	        colorHandle = TyrGL.glGetAttribLocation(material.getProgram().handle, "a_Color");
	        
			// Pass in the color.
	        buffer.position(COLOR_OFFSET);
	        TyrGL.glVertexAttribPointer(colorHandle, COLOR_SIZE, TyrGL.GL_FLOAT, false,
		    							 PARTICLE_DATA_SIZE * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
	        TyrGL.glEnableVertexAttribArray(colorHandle);  
	        
	        // Apply the projection and view transformation
			Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
	        
			// Pass in the transformation matrix.
			TyrGL.glUniformMatrix4fv(material.getMVPMatrixHandle(), 1, false, mvpMatrix, 0);
			
			TyrGL.glDepthMask(false);
			
			// Draw the point.
			TyrGL.glDrawArrays(TyrGL.GL_POINTS, 0, particleData.getSize() / PARTICLE_DATA_SIZE);
		
			TyrGL.glDepthMask(true);

		}
		
	}
	
	public ParticleSystem copy() {
		SimpleParticleSystem other = new SimpleParticleSystem(maxParticles);
		
		other.affector = affector.copy();
		other.addEmitter(emitter.copy());
		
		return other;
	}

	@Override
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}
}
