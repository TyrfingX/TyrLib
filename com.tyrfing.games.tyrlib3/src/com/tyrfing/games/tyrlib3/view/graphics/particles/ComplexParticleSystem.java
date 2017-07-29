package com.tyrfing.games.tyrlib3.view.graphics.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.tyrfing.games.tyrlib3.model.data.FloatArray;
import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.graphics.particles.Affector;
import com.tyrfing.games.tyrlib3.model.graphics.particles.Emitter;
import com.tyrfing.games.tyrlib3.model.graphics.particles.Particle;
import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.math.AABB;
import com.tyrfing.games.tyrlib3.model.math.Matrix;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.materials.ParticleMaterial;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;

/**
 * Manages a system of particles.
 * This includes particles, emitters, affectors, etc
 * @author Sascha
 *
 */

public class ComplexParticleSystem extends ARenderableParticleSystem {
	
	public static final int MIN_AFFECTORS_SIZE = 5;
	private Affector[] affectors;
	private int countAffectors;
	
	private List<Emitter> emitters;

	private List<ParticleBatch> particleBatches;
	
	private AABB boundingBox;
	
	private boolean disableZWriting = true;
	
	private int[] buffers = new int[2];
	
	private boolean dynamic = true;
	private boolean fixInsertionID = false;
	private int insertionID = -1;
	
	private boolean staticUpdated;
	
	
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
		
		visible = true;
	}
	
	public ComplexParticleSystem(int maxParticles, boolean screenSpace) {
		this();
		this.maxParticles = maxParticles;
		this.screenSpace = screenSpace;
		
        ByteBuffer bb = ByteBuffer.allocateDirect(maxParticles * OpenGLRenderer.BYTES_PER_FLOAT * PARTICLE_DATA_SIZE);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        buffer = bb.asFloatBuffer();
        
        ByteBuffer bb2 = ByteBuffer.allocateDirect(DRAW_ORDER.length * maxParticles * 2);
        
        // use the device hardware's native byte order
        bb2.order(ByteOrder.nativeOrder());
        buffer2 = bb2.asShortBuffer();
        
        short[] drawOrder_ = screenSpace ? DRAW_ORDER_SCREEN_SPACE : DRAW_ORDER;
        
        for (int i = 0; i < maxParticles; ++i) {
        	short[] drawOrder = { 	(short) (drawOrder_[0]+4*i), 
									(short) (drawOrder_[1]+4*i),
									(short) (drawOrder_[2]+4*i),
									(short) (drawOrder_[3]+4*i),
									(short) (drawOrder_[4]+4*i),
									(short) (drawOrder_[5]+4*i) };
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
		
		if (parent != null) {
			parent.forceUpdate();
		}
		
		// Update everything
		if (dynamic) {
			
			boundingBox.min.x = Float.MAX_VALUE;
			boundingBox.min.y = Float.MAX_VALUE;
			boundingBox.min.z = Float.MAX_VALUE;
			
			boundingBox.max.x = -Float.MAX_VALUE;
			boundingBox.max.y = -Float.MAX_VALUE;
			boundingBox.max.z = -Float.MAX_VALUE;
			
			for (int l = 0; l < steps; ++l) {
			
				int countMaterials = particleBatches.size();
				for (int j = 0; j < countMaterials; ++j) {
					ParticleBatch batch = particleBatches.get(j);
					ParticleMaterial material = batch.material;
					List<Particle> particles = batch.particles;
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
							
							particle.getAcceleration().x = 0;
							particle.getAcceleration().y = 0;
							particle.getAcceleration().z = 0;
						}
					}
				}
				
				for (int i = 0, countEmitters = emitters.size(); i < countEmitters; ++i) {
					emitters.get(i).onUpdate(time);
				}
			}
		} else if (!staticUpdated || visible){
			
			boundingBox.min.x = Float.MAX_VALUE;
			boundingBox.min.y = Float.MAX_VALUE;
			boundingBox.min.z = Float.MAX_VALUE;
			
			boundingBox.max.x = -Float.MAX_VALUE;
			boundingBox.max.y = -Float.MAX_VALUE;
			boundingBox.max.z = -Float.MAX_VALUE;
			
			if (this.getParent() != null && !this.getParent().isDirty()) {
				staticUpdated = true;
			}
			
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
		
		if (staticUpdated) {
			if (this.getParent() != null && this.getParent().isDirty()) {
				staticUpdated = false;
			}
		}
		
		if (isBoundingBoxVisible()) {
			updateBoundingBox();
		}
	}
	
	public boolean isSaturated() {
		for (int i = 0; i < emitters.size(); ++i) {
			if (!emitters.get(i).isFinished()) return false;
		}
		
		return countParticles == 0;
	}
	
	
	public void checkBoundingBox(Particle particle) {
		if (particle.getPos().x > boundingBox.max.x) boundingBox.max.x = particle.getPos().x + particle.getSize();
		if (particle.getPos().y > boundingBox.max.y) boundingBox.max.y = particle.getPos().y + particle.getSize();
		if (particle.getPos().z > boundingBox.max.z) boundingBox.max.z = particle.getPos().z + particle.getSize();
		
		if (particle.getPos().x < boundingBox.min.x) boundingBox.min.x = particle.getPos().x - particle.getSize();
		if (particle.getPos().y < boundingBox.min.y) boundingBox.min.y = particle.getPos().y - particle.getSize();
		if (particle.getPos().z < boundingBox.min.z) boundingBox.min.z = particle.getPos().z - particle.getSize();
	}
	
	private void useAffectors(Particle particle, float time) {
		for (int k = 0; k < countAffectors; ++k) {
			Affector a = affectors[k];
			if (particle.getPassedTime() >= a.getTimeMin() && particle.getPassedTime() <= a.getTimeMax()) {
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
		
		particle.setSystem(this);
		
		batch.particles.add(particle);
		batch.colorHandle = TyrGL.glGetAttribLocation(material.getProgram().handle, "a_Color");
		batch.texHandle = TyrGL.glGetAttribLocation(material.getProgram().handle, "a_TexCoordinate");
		
		FloatArray particleData = batch.particleData;
		particle.dataIndex = particleData.getSize();
		particle.floatArray = particleData;
		
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(0);
		Color color = particle.getColor();
		particleData.pushBack(color.r);
		particleData.pushBack(color.g);
		particleData.pushBack(color.b);
		particleData.pushBack(color.a);
		
		ParticleMaterial localMaterial = particle.getMaterial();
		particleData.pushBack(localMaterial.getRegion().u2);
		particleData.pushBack(localMaterial.getRegion().v2);
		
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(color.r);
		particleData.pushBack(color.g);
		particleData.pushBack(color.b);
		particleData.pushBack(color.a);
		particleData.pushBack(localMaterial.getRegion().u2);
		particleData.pushBack(localMaterial.getRegion().v1);
		
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(color.r);
		particleData.pushBack(color.g);
		particleData.pushBack(color.b);
		particleData.pushBack(color.a);
		particleData.pushBack(localMaterial.getRegion().u1);
		particleData.pushBack(localMaterial.getRegion().v2);
		
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(0);
		particleData.pushBack(color.r);
		particleData.pushBack(color.g);
		particleData.pushBack(color.b);
		particleData.pushBack(color.a);
		particleData.pushBack(localMaterial.getRegion().u1);
		particleData.pushBack(localMaterial.getRegion().v1);
		
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
			if (emitter.getParent() != null) {
				parent.attachChild(emitter.getParent());
			} else {
				parent.attachSceneObject(emitter);
			}
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
	
	public int getCountMaterials() {
		return particleBatches.size();
	}
	
	public ParticleMaterial getMaterial(int index) {
		return particleBatches.get(index).material;
	}
	
	public Affector getAffector(int index) {
		return affectors[index];
	}
	
	@Override
	public void renderShadow(float[] vpMatrix) {
		
	}
	
	@Override
	public void render(float[] vpMatrix) {
		
		if (parent == null || !visible) {
			return;
		}
		
		int countBatches = particleBatches.size();
		
		if (disableZWriting && !screenSpace) {
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
			        
					// Pass in the color.
			        TyrGL.glVertexAttribPointer(batch.colorHandle, COLOR_SIZE, TyrGL.GL_FLOAT, false,
				    							 (PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, COLOR_OFFSET * OpenGLRenderer.BYTES_PER_FLOAT);
			        TyrGL.glEnableVertexAttribArray(batch.colorHandle);  
			        
			        // Pass in the uv coords.
			        TyrGL.glVertexAttribPointer(batch.texHandle, UV_SIZE, TyrGL.GL_FLOAT, false,
			        							(PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, UV_OFFSET * OpenGLRenderer.BYTES_PER_FLOAT);
			        TyrGL.glEnableVertexAttribArray(batch.texHandle);  
				} else {
					TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, 0);
					
					// Pass in the position.
					TyrGL.glVertexAttribPointer(material.getPositionHandle(), POSITION_SIZE, TyrGL.GL_FLOAT, false,
												(PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
					
					TyrGL.glEnableVertexAttribArray(material.getPositionHandle());  
					
					// Pass in the color.
			        buffer.position(COLOR_OFFSET);
			        TyrGL.glVertexAttribPointer(batch.colorHandle, COLOR_SIZE, TyrGL.GL_FLOAT, false,
			        							(PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
			        TyrGL.glEnableVertexAttribArray(batch.colorHandle);  
			        
					// Pass in the uv coords.
			        buffer.position(UV_OFFSET);
			        TyrGL.glVertexAttribPointer(batch.texHandle, UV_SIZE, TyrGL.GL_FLOAT, false,
			        							(PARTICLE_DATA_SIZE/4) * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
			        TyrGL.glEnableVertexAttribArray(batch.texHandle);  
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
		
		if (disableZWriting && !screenSpace) {
			TyrGL.glDepthMask( true );
		}		
		
	}
	
	public ARenderableParticleSystem copy() {
		ComplexParticleSystem other = new ComplexParticleSystem(maxParticles, screenSpace);
		
		other.affectors = new Affector[countAffectors];
		other.countAffectors = countAffectors;
		other.screenSpace = screenSpace;
		
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
	
	public void setFixInsertionID(boolean state) {
		fixInsertionID = state;
	}
	
	@Override
	public void setInsertionID(int id) {
		if (!fixInsertionID || this.insertionID == -1) {
			this.insertionID = id;
		}
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		for (int i = 0; i < buffers.length; ++i) {
			if (buffers[i] != 0) {
				TyrGL.glDeleteBuffers(1, buffers, i);
			}
		}
	}
	
	public void clear() {
		for (int i = 0; i < particleBatches.size(); ++i) {
			ParticleBatch batch = particleBatches.get(i);
			
			List<Particle> particles = batch.particles;
			batch.particleData.clear();
			deadParticles.addAll(particles);
			particles.clear();
		}
		
		countParticles = 0;
	}
	
	@Override
	public void setGlobalScale(int scale) {
		super.setGlobalScale(scale);
		for (int i = 0; i < emitters.size(); ++i) {
			emitters.get(i).setRelativePos(emitters.get(i).getRelativePos().multiply(globalScale));
		}
	}

}
