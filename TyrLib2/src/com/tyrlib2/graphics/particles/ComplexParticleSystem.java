package com.tyrlib2.graphics.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.opengl.GLES20;

import com.tyrlib2.graphics.materials.ParticleMaterial;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Matrix;
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
	
	private boolean disableZWriting = true;
	
	private int colorHandle;
	private int texHandle;
	
	private int[] buffers = new int[2];
	
	private boolean dynamic = true;
	
	public ComplexParticleSystem() {
		affectors = new Affector[MIN_AFFECTORS_SIZE];
		emitters = new ArrayList<Emitter>();
		particleBatches = new ArrayList<ParticleBatch>();
		modelMatrix = SceneManager.getInstance().getRootSceneNode().getModelMatrix();
		deadParticles = new Stack<Particle>();
		boundingBox = new AABB();
		
		boundingBox.min.x = Float.MAX_VALUE;
		boundingBox.min.y = Float.MAX_VALUE;
		boundingBox.min.z = Float.MAX_VALUE;
		
		boundingBox.max.x = -Float.MAX_VALUE;
		boundingBox.max.y = -Float.MAX_VALUE;
		boundingBox.max.z = -Float.MAX_VALUE;
	}
	
	public ComplexParticleSystem(int maxParticles) {
		this();
		this.maxParticles = maxParticles;
		
        ByteBuffer bb = ByteBuffer.allocateDirect(maxParticles * OpenGLRenderer.BYTES_PER_FLOAT * PARTICLE_DATA_SIZE);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        buffer = bb.asFloatBuffer();
        
        ByteBuffer bb2 = ByteBuffer.allocateDirect(DRAW_ORDER.length * maxParticles * 2);
        
        // use the device hardware's native byte order
        bb2.order(ByteOrder.nativeOrder());
        buffer2 = bb2.asShortBuffer();
        
        for (int i = 0; i < maxParticles; ++i) {
        	short[] drawOrder = { 	(short) (DRAW_ORDER[0]+4*i), 
									(short) (DRAW_ORDER[1]+4*i),
									(short) (DRAW_ORDER[2]+4*i),
									(short) (DRAW_ORDER[3]+4*i),
									(short) (DRAW_ORDER[4]+4*i),
									(short) (DRAW_ORDER[5]+4*i) };
        	buffer2.put(drawOrder);
        }
        
        buffer2.position(0);
        
        if (TyrGL.GL_USE_VBO == 1) {
			TyrGL.glGenBuffers(2, buffers, 0); // Get A Valid Name
			TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, buffers[0]); // Bind The Buffer
	        // Load The Data
	        TyrGL.glBufferData(TyrGL.GL_ARRAY_BUFFER,  maxParticles * OpenGLRenderer.BYTES_PER_FLOAT * PARTICLE_DATA_SIZE, buffer, TyrGL.GL_STREAM_DRAW);
	        
			TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, buffers[1]); // Bind The Buffer
	        // Load The Data
	        TyrGL.glBufferData(TyrGL.GL_ARRAY_BUFFER,  DRAW_ORDER.length * maxParticles * 2, buffer2, TyrGL.GL_STATIC_DRAW);
        }
	}
	
	public void setZWritingDisabled(boolean disabled) {
		this.disableZWriting = disabled;
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
		if (dynamic) {
			for (int l = 0; l < steps; ++l) {
			
				int countMaterials = particleBatches.size();
				for (int j = 0; j < countMaterials; ++j) {
					
					ParticleMaterial material = particleBatches.get(j).material;
					List<Particle> particles = particleBatches.get(j).particles;
					int countParticles = particles.size();
					for (int i = 0; i < countParticles; ++i) {
						
						Particle particle = particles.get(i);
						
						if (particle.isFinished()) {
							removeParticle(i, material);
							--i;
							--countParticles;
						} else {
							checkBoundingBox(particle);
							useAffectors(particle, time);
							
							particle.onUpdate(time);
							
							particle.acceleration.x = 0;
							particle.acceleration.y = 0;
							particle.acceleration.z = 0;
						}
					}
				}
				
				for (int i = 0; i < emitters.size(); ++i) {
					emitters.get(i).onUpdate(time);
				}
			}
		} else {
			for (int l = 0; l < steps; ++l) {
				int countMaterials = particleBatches.size();
				for (int j = 0; j < countMaterials; ++j) {
					List<Particle> particles = particleBatches.get(j).particles;
					int countParticles = particles.size();
					for (int i = 0; i < countParticles; ++i) {
						Particle particle = particles.get(i);
						checkBoundingBox(particle);
						particle.onUpdate(time);
					}
				}
			}
				
		}
		
		if (isBoundingBoxVisible()) {
			updateBoundingBox();
		}
		
		visible = false;
	}
	
	
	public void checkBoundingBox(Particle particle) {
		if (particle.pos.x > boundingBox.max.x) boundingBox.max.x = particle.pos.x + particle.size;
		if (particle.pos.y > boundingBox.max.y) boundingBox.max.y = particle.pos.y + particle.size;
		if (particle.pos.z > boundingBox.max.z) boundingBox.max.z = particle.pos.z + particle.size;
		
		if (particle.pos.x < boundingBox.min.x) boundingBox.min.x = particle.pos.x - particle.size;
		if (particle.pos.y < boundingBox.min.y) boundingBox.min.y = particle.pos.y - particle.size;
		if (particle.pos.z < boundingBox.min.z) boundingBox.min.z = particle.pos.z - particle.size;
	}
	
	private void useAffectors(Particle particle, float time) {
		for (int k = 0; k < countAffectors; ++k) {
			Affector a = affectors[k];
			if (a.isApplicable(particle, time)) {
				a.onUpdate(particle, time);
			}
		}
	}
	
	public void addParticle(Particle particle) {
		
		ParticleMaterial material = particle.getMaterial();
		
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
		
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(particle.color.r);
		particleData.pushBack(particle.color.g);
		particleData.pushBack(particle.color.b);
		particleData.pushBack(particle.color.a);
		particleData.pushBack(particle.material.getRegion().u2);
		particleData.pushBack(particle.material.getRegion().v2);
		
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(particle.color.r);
		particleData.pushBack(particle.color.g);
		particleData.pushBack(particle.color.b);
		particleData.pushBack(particle.color.a);
		particleData.pushBack(particle.material.getRegion().u2);
		particleData.pushBack(particle.material.getRegion().v1);
		
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(particle.color.r);
		particleData.pushBack(particle.color.g);
		particleData.pushBack(particle.color.b);
		particleData.pushBack(particle.color.a);
		particleData.pushBack(particle.material.getRegion().u1);
		particleData.pushBack(particle.material.getRegion().v2);
		
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(particle.color.r);
		particleData.pushBack(particle.color.g);
		particleData.pushBack(particle.color.b);
		particleData.pushBack(particle.color.a);
		particleData.pushBack(particle.material.getRegion().u1);
		particleData.pushBack(particle.material.getRegion().v1);
		
		particle.updateCorners();
		
		countParticles++;
	}
	
	public void removeParticle(int index, ParticleMaterial material) {
		
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
		
		visible = true;
		
		int countBatches = particleBatches.size();
		
		if (disableZWriting) {
			TyrGL.glDepthMask( false );
		}		
		
		for (int i = 0; i < countBatches; ++i) {
			
			ParticleBatch batch = particleBatches.get(i);
			ParticleMaterial material = batch.material;
			FloatArray particleData = batch.particleData;
			
			int size = particleData.getSize();
			
			if (size > 0) {

		        buffer.clear();
		        buffer.put(particleData.buffer, 0, size);
		        buffer.position(0);
		        				
				material.getProgram().use();
				
				material.render(null, vpMatrix);
				
				if (TyrGL.GL_USE_VBO == 1) {
				
					TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, buffers[0]);
					TyrGL.glBufferSubData(TyrGL.GL_ARRAY_BUFFER, 0, maxParticles * OpenGLRenderer.BYTES_PER_FLOAT * PARTICLE_DATA_SIZE, buffer);
					
					// Pass in the position.
					TyrGL.glVertexAttribPointer(material.getPositionHandle(), POSITION_SIZE, TyrGL.GL_FLOAT, false,
				    							 (PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, 0);
					TyrGL.glEnableVertexAttribArray(material.getPositionHandle());  
					
			        colorHandle = TyrGL.glGetAttribLocation(material.getProgram().handle, "a_Color");
			        
					// Pass in the color.
			        TyrGL.glVertexAttribPointer(colorHandle, COLOR_SIZE, TyrGL.GL_FLOAT, false,
				    							 (PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, COLOR_OFFSET * OpenGLRenderer.BYTES_PER_FLOAT);
			        TyrGL.glEnableVertexAttribArray(colorHandle);  

			        texHandle = TyrGL.glGetAttribLocation(material.getProgram().handle, "a_TexCoordinate");
			        
			        // Pass in the uv coords.
			        TyrGL.glVertexAttribPointer(texHandle, UV_SIZE, TyrGL.GL_FLOAT, false,
			        							(PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, UV_OFFSET * OpenGLRenderer.BYTES_PER_FLOAT);
			        TyrGL.glEnableVertexAttribArray(texHandle);  
				} else {
					// Pass in the position.
					TyrGL.glVertexAttribPointer(material.getPositionHandle(), POSITION_SIZE, TyrGL.GL_FLOAT, false,
												(PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
					
					TyrGL.glEnableVertexAttribArray(material.getPositionHandle());  
					
			        colorHandle = TyrGL.glGetAttribLocation(material.getProgram().handle, "a_Color");
			        
					// Pass in the color.
			        buffer.position(COLOR_OFFSET);
			        TyrGL.glVertexAttribPointer(colorHandle, COLOR_SIZE, TyrGL.GL_FLOAT, false,
			        							(PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
			        TyrGL.glEnableVertexAttribArray(colorHandle);  
			        
			        texHandle = TyrGL.glGetAttribLocation(material.getProgram().handle, "a_TexCoordinate");
			        
					// Pass in the uv coords.
			        buffer.position(UV_OFFSET);
			        TyrGL.glVertexAttribPointer(texHandle, UV_SIZE, TyrGL.GL_FLOAT, false,
			        							(PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
			        TyrGL.glEnableVertexAttribArray(texHandle);  
				}
		        
		        // Apply the projection and view transformation
				Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
		        
				// Pass in the transformation matrix.
				TyrGL.glUniformMatrix4fv(material.getMVPMatrixHandle(), 1, false, mvpMatrix, 0);
				
				//TyrGL.glDepthMask(false);
				
				// Draw the point.
				if (TyrGL.GL_USE_VBO == 1) {
					TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
					TyrGL.glDrawElements(TyrGL.GL_TRIANGLES, 6*particleData.getSize() / PARTICLE_DATA_SIZE, TyrGL.GL_UNSIGNED_SHORT, 0);
				} else {
					TyrGL.glDrawElements(TyrGL.GL_TRIANGLES, 6*particleData.getSize() / PARTICLE_DATA_SIZE, TyrGL.GL_UNSIGNED_SHORT, buffer2);
				}
			
				//TyrGL.glDepthMask(true);

			}
		}
		
		if (disableZWriting) {
			TyrGL.glDepthMask( true );
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
	
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
}
