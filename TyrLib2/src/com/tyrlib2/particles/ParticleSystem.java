package com.tyrlib2.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.materials.PointSpriteMaterial;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.IRenderable;
import com.tyrlib2.renderer.OpenGLRenderer;
import com.tyrlib2.scene.SceneManager;
import com.tyrlib2.scene.SceneNode;
import com.tyrlib2.scene.SceneObject;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.FloatArray;

/**
 * Manages a system of particles.
 * This includes particles, emitters, affectors, etc
 * @author Sascha
 *
 */

public class ParticleSystem extends SceneObject implements IUpdateable, IRenderable {
	
	private float[] mvpMatrix = new float[16];
	private float[] modelMatrix;
	
	private List<Affector> affectors;
	private List<Emitter> emitters;
	private List<PointSpriteMaterial> materials;
	
	protected Map<PointSpriteMaterial, FloatArray> particleDataMap;
	protected Map<PointSpriteMaterial, List<Particle>> particleMap;
	
	private int maxParticles;
	
	public final static int POSITION_OFFSET = 0;
	public final static int POSITION_SIZE = 3;
	public final static int COLOR_OFFSET = 3;
	public final static int COLOR_SIZE = 4;
	public final static int PARTICLE_DATA_SIZE = 7;
	
	public final static int DEFAULT_SIZE = 16; 
	
	private int countParticles;
	
	public ParticleSystem(int maxParticles) {
		affectors = new ArrayList<Affector>();
		emitters = new ArrayList<Emitter>();
		particleMap = new HashMap<PointSpriteMaterial, List<Particle>>();
		particleDataMap = new HashMap<PointSpriteMaterial, FloatArray>();
		materials = new ArrayList<PointSpriteMaterial>();
		
		this.maxParticles = maxParticles;
		
		modelMatrix = SceneManager.getInstance().getRootSceneNode().getModelMatrix();
	}

	@Override
	public void onUpdate(float time) {
		
		// Update everything
		
		for (int j = 0; j < materials.size(); ++j) {
			
			PointSpriteMaterial material = materials.get(j);
			List<Particle> particles = particleMap.get(material);
			for (int i = 0; i < particles.size(); ++i) {
				Particle particle = particles.get(i);
				particle.onUpdate(time);
				particle.acceleration = new Vector3();
				if (particle.isFinished()) {
					removeParticle(i, material);
					--i;
				} else {
					FloatArray particleData = particleDataMap.get(material);
					particleData.buffer[particle.dataIndex + 0] = particle.pos.x;
					particleData.buffer[particle.dataIndex + 1] = particle.pos.y;
					particleData.buffer[particle.dataIndex + 2] = particle.pos.z;
					
					Color color = particle.color;
					particleData.buffer[particle.dataIndex + 3] = color.r;
					particleData.buffer[particle.dataIndex + 4] = color.g;
					particleData.buffer[particle.dataIndex + 5] = color.b;
					particleData.buffer[particle.dataIndex + 6] = color.a;
				
					for (int k = 0; k < affectors.size(); ++k) {
						Affector affector = affectors.get(k);
						if (affector.isApplicable(particle, time)) {
							affector.onUpdate(particle, time);
						}
					}
				
				}
			}
		}
		
		for (int i = 0; i < emitters.size(); ++i) {
			emitters.get(i).onUpdate(time);
		}
		
		
	}
	
	public void addParticle(Particle particle) {
		
		PointSpriteMaterial material = particle.getMaterial();
		if (!materials.contains(material)) {
			materials.add(material);
			particleDataMap.put(material, new FloatArray(DEFAULT_SIZE));
			particleMap.put(material, new ArrayList<Particle>());
		}
		
		particle.system = this;
		particleMap.get(material).add(particle);
	
		FloatArray particleData = particleDataMap.get(material);
		particle.dataIndex = particleData.getSize();
		
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
		List<Particle> particles = particleMap.get(material);
		Particle particle = particles.get(index);
		
		int indexLastParticle = particles.size() - 1;
		Particle lastParticle = particles.get(indexLastParticle);
		
		FloatArray particleData = particleDataMap.get(material);
		
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
		emitters.add(emitter);
		emitter.setParticleSystem(this);
	}
	
	public Emitter getEmitter(int index) {
		return emitters.get(index);
	}
	
	public void addAffector(Affector affector) {
		affectors.add(affector);
		affector.setParticleSystem(this);
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		for (Emitter emitter : emitters) {
			parent.attachChild(emitter.getParent());
		}
		for (Affector affector : affectors) {
			affector.attachTo(node);
		}
	}
	

	@Override
	public SceneNode detach() {
		for (Emitter emitter : emitters) {
			emitter.detach();
		}
		for (Affector affector : affectors) {
			affector.detach();
		}
		return super.detach();	
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public Map<PointSpriteMaterial, List<Particle>> getParticles() {
		return particleMap;
	}

	public int getCountParticles() {
		return countParticles;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		
		for (int i = 0; i < materials.size(); ++i) {
			
			PointSpriteMaterial material = materials.get(i);
			FloatArray particleData = particleDataMap.get(material);
			
			if (particleData.getSize() > 0) {

		        ByteBuffer bb = ByteBuffer.allocateDirect(particleData.getSize() * OpenGLRenderer.BYTES_PER_FLOAT);
		        
		        // use the device hardware's native byte order
		        bb.order(ByteOrder.nativeOrder());
		        FloatBuffer buffer = bb.asFloatBuffer();
		        buffer.put(particleData.buffer, 0, particleData.getSize());
		        buffer.position(0);
		        				
				material.getProgram().use();
				
				material.render(null, vpMatrix);
				
				// Pass in the position.
			    GLES20.glVertexAttribPointer(material.getPositionHandle(), POSITION_SIZE, GLES20.GL_FLOAT, false,
			    							 PARTICLE_DATA_SIZE * OpenGLRenderer.BYTES_PER_FLOAT, buffer);
				
		        GLES20.glEnableVertexAttribArray(material.getPositionHandle());  
				
		        int colorHandle = GLES20.glGetAttribLocation(material.getProgram().handle, "a_Color");
		        
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

	@Override
	public AABB getBoundingBox() {
		return null;
	}

	@Override
	public void setBoundingBoxVisible(boolean visible) {
	}
	
	public int getMaxParticles() {
		return maxParticles;
	}

}
