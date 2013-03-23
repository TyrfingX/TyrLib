package com.tyrlib2.graphics.particles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.materials.PointSpriteMaterial;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
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

public class ParticleSystem extends BoundedSceneObject implements IUpdateable, IRenderable {
	
	private float[] mvpMatrix = new float[16];
	private float[] modelMatrix;
	
	public static final int MIN_AFFECTORS_SIZE = 5;
	private Affector[] affectors;
	private int countAffectors;
	
	private List<Emitter> emitters;
	private List<PointSpriteMaterial> materials;
	private Stack<Particle> deadParticles;
	
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
	
	private int steps = 1;
	
	private FloatBuffer buffer;
	
	private AABB boundingBox;
	
	public ParticleSystem() {
		affectors = new Affector[MIN_AFFECTORS_SIZE];
		emitters = new ArrayList<Emitter>();
		particleMap = new HashMap<PointSpriteMaterial, List<Particle>>();
		particleDataMap = new HashMap<PointSpriteMaterial, FloatArray>();
		materials = new ArrayList<PointSpriteMaterial>();
		modelMatrix = SceneManager.getInstance().getRootSceneNode().getModelMatrix();
		deadParticles = new Stack<Particle>();
		boundingBox = new AABB();
	}
	
	public ParticleSystem(int maxParticles) {
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
		
			int countMaterials = materials.size();
			for (int j = 0; j < countMaterials; ++j) {
				
				PointSpriteMaterial material = materials.get(j);
				List<Particle> particles = particleMap.get(material);
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
		for (int k = 0; k < countAffectors; ++k) {
			if (affectors[k].isApplicable(particle, time)) {
				affectors[k].onUpdate(particle, time);
			}
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
		List<Particle> particles = particleMap.get(material);
		Particle particle = particles.get(index);
		deadParticles.push(particle);
		
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
	
	protected Particle requestDeadParticle() {
		if (!deadParticles.empty()) {
			return deadParticles.pop();
		}
		
		return null;
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
	
	public int getMaxParticles() {
		return maxParticles;
	}
	
	public boolean allowsMoreParticles() {
		if (maxParticles == 0) return true;
		return (maxParticles > countParticles);
	}
	
	public void setMaxParticles(int maxParticles) {
		this.maxParticles = maxParticles;
		
        ByteBuffer bb = ByteBuffer.allocateDirect(maxParticles * OpenGLRenderer.BYTES_PER_FLOAT * PARTICLE_DATA_SIZE);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        buffer = bb.asFloatBuffer();
	}
	
	public ParticleSystem copy() {
		ParticleSystem other = new ParticleSystem(maxParticles);
		
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

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	@Override
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
}
