package com.tyrlib2.graphics.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.graphics.materials.PointSpriteMaterial;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;
import com.tyrlib2.util.FloatArray;

/**
 * Manages a system of particles.
 * This includes particles, emitters, affectors, etc
 * @author Sascha
 *
 */

public class ComplexParticleSystem extends ParticleSystem {
	
	public static final int MIN_AFFECTORS_SIZE = 5;
	private Affector[] affectors;
	private int countAffectors;
	
	private List<Emitter> emitters;

	private List<ParticleBatch> particleBatches;
	
	private AABB boundingBox;
	
	private int colorHandle;
	
	public ComplexParticleSystem() {
		affectors = new Affector[MIN_AFFECTORS_SIZE];
		emitters = new ArrayList<Emitter>();
		particleBatches = new ArrayList<ParticleBatch>();
		modelMatrix = SceneManager.getInstance().getRootSceneNode().getModelMatrix();
		deadParticles = new Stack<Particle>();
		boundingBox = new AABB();
	}
	
	public ComplexParticleSystem(int maxParticles) {
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
		
			int countMaterials = particleBatches.size();
			for (int j = 0; j < countMaterials; ++j) {
				
				PointSpriteMaterial material = particleBatches.get(j).material;
				List<Particle> particles = particleBatches.get(j).particles;
				int countParticles = particles.size();
				for (int i = 0; i < countParticles; ++i) {
					
					Particle particle = particles.get(i);
					particle.onUpdate(time);
					
					particle.acceleration.x = 0;
					particle.acceleration.y = 0;
					particle.acceleration.z = 0;
					
					if (particle.isFinished()) {
						removeParticle(i, material);
						--i;
						--countParticles;
					} else {
						checkBoundingBox(particle);
						useAffectors(particle, time);
					}
				}
			}
			
			for (int i = 0; i < emitters.size(); ++i) {
				emitters.get(i).onUpdate(time);
			}
		
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
	
	private void useAffectors(Particle particle, float time) {
		for (int k = countAffectors - 1; k >= 0; --k) {
			if (affectors[k].isApplicable(particle, time)) {
				affectors[k].onUpdate(particle, time);
			}
		}
	}
	
	public void addParticle(Particle particle) {
		
		PointSpriteMaterial material = particle.getMaterial();
		
		ParticleBatch batch = null;
		
		for (int i = 0; i < particleBatches.size(); ++i) {
			if (particleBatches.get(i).material == material) {
				batch = particleBatches.get(i);
				break;
			}
		}
		
		if (batch == null) {
			batch = new ParticleBatch();
			batch.material = material;
			batch.particleData = new FloatArray(DEFAULT_SIZE);
			particleBatches.add(batch);
		}
		
		particle.system = this;
		
		batch.particles.add(particle);
	
		FloatArray particleData = batch.particleData;
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
	
	private void removeParticle(int index, PointSpriteMaterial material) {
		
		for (int i = 0; i < particleBatches.size(); ++i) {
		
			ParticleBatch batch = particleBatches.get(i);
			
			if (batch.material == material) {
				
				List<Particle> particles = batch.particles;
				Particle particle = particles.get(index);
				deadParticles.push(particle);
				
				int indexLastParticle = particles.size() - 1;
				Particle lastParticle = particles.get(indexLastParticle);
				
				FloatArray particleData = batch.particleData;
				
				System.arraycopy(particleData.buffer, lastParticle.dataIndex, 
								 particleData.buffer, particle.dataIndex, 
								 PARTICLE_DATA_SIZE); 
				particleData.popBack(PARTICLE_DATA_SIZE);
				
				lastParticle.dataIndex = particle.dataIndex;
				particles.set(index, lastParticle);
				particles.remove(indexLastParticle);
				
				countParticles--;
				
				break;
			}
		}
		
	}
	
	public void addEmitter(Emitter emitter) {
		emitters.add(emitter);
		emitter.setParticleSystem(this);
	}
	
	public Emitter getEmitter(int index) {
		return emitters.get(index);
	}
	
	public int getCountEmitters() {
		return emitters.size();
	}
	
	public void addAffector(Affector affector) {
		if (countAffectors == affectors.length) {
			Affector[] newAffectors = new Affector[countAffectors * 2];
			System.arraycopy(affectors, 0, newAffectors, 0, countAffectors);
			affectors = newAffectors;
		}
		
		affectors[countAffectors++] = affector;
		
		affector.setParticleSystem(this);
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		for (Emitter emitter : emitters) {
			parent.attachChild(emitter.getParent());
		}
		for (int i = 0; i < countAffectors; ++i) {
			affectors[i].attachTo(node);
		}
	}
	

	@Override
	public SceneNode detach() {
		for (Emitter emitter : emitters) {
			emitter.detach();
		}
		for (int i = 0; i < countAffectors; ++i) {
			affectors[i].detach();
		}
		return super.detach();	
	}
	
	@Override
	public void render(float[] vpMatrix) {
		
		if (parent == null) {
			return;
		}
		
		int countBatches = particleBatches.size();
		
		for (int i = 0; i < countBatches; ++i) {
			
			ParticleBatch batch = particleBatches.get(i);
			PointSpriteMaterial material = batch.material;
			FloatArray particleData = batch.particleData;
			
			int size = particleData.getSize();
			
			if (size > 0) {

		        buffer.clear();
		        buffer.put(particleData.buffer, 0, size);
		        buffer.position(0);
		        				
				material.getProgram().use();
				
				material.render(null, vpMatrix);
				
				// Pass in the position.
			    GLES20.glVertexAttribPointer(material.getPositionHandle(), POSITION_SIZE, GLES20.GL_FLOAT, false,
			    							 PARTICLE_DATA_SIZE * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
				
		        GLES20.glEnableVertexAttribArray(material.getPositionHandle());  
				
		        colorHandle = GLES20.glGetAttribLocation(material.getProgram().handle, "a_Color");
		        
				// Pass in the color.
		        buffer.position(COLOR_OFFSET);
			    GLES20.glVertexAttribPointer(colorHandle, COLOR_SIZE, GLES20.GL_FLOAT, false,
			    							 PARTICLE_DATA_SIZE * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
		        GLES20.glEnableVertexAttribArray(colorHandle);  
		        
		        // Apply the projection and view transformation
				Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
		        
				// Pass in the transformation matrix.
				GLES20.glUniformMatrix4fv(material.getMVPMatrixHandle(), 1, false, mvpMatrix, 0);
				
				GLES20.glDepthMask(false);
				
				// Draw the point.
				GLES20.glDrawArrays(GLES20.GL_POINTS, 0, particleData.getSize() / PARTICLE_DATA_SIZE);
			
				GLES20.glDepthMask(true);

			}
		}
		
	}
	
	public ParticleSystem copy() {
		ComplexParticleSystem other = new ComplexParticleSystem(maxParticles);
		
		other.affectors = new Affector[countAffectors];
		other.countAffectors = countAffectors;
		
		for (int i = 0; i < countAffectors; ++i) {
			other.affectors[i] = affectors[i].copy();
		}

		for (int i = 0; i < emitters.size(); ++i) {
			other.addEmitter(emitters.get(i).copy());
		}
		
		return other;
	}

	@Override
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
}
