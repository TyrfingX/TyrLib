package com.tyrfing.games.tyrlib3.view.graphics.animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrfing.games.tyrlib3.model.game.IUpdateable;
import com.tyrfing.games.tyrlib3.model.graphics.animation.Bone;
import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.view.graphics.Mesh;
import com.tyrfing.games.tyrlib3.view.graphics.Program;
import com.tyrfing.games.tyrlib3.view.graphics.SceneManager;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;

/**
 * This class manages skeletal animations by using bones.
 * @author Sascha
 *
 */

public class Skeleton implements IUpdateable {
	
	private List<Animation> animations = new ArrayList<Animation>();
	private Map<String, Animation> animationsMap = new HashMap<String, Animation>();
	private List<Bone> bones = new ArrayList<Bone>();
	protected float[] boneData = null; 
	protected SceneNode rootNode = new SceneNode();
	
	/** 
	 * Add a new animation
	 * @param animation
	 */
	public void addAnimation(Animation animation) {
		animations.add(animation);
		animationsMap.put(animation.name, animation);
		animation.skeleton = this;
	}
	
	/**
	 * Get an existing animation
	 * @param animationName
	 */
	public Animation getAnimation(String animationName) {
		Animation anim = animationsMap.get(animationName);
		return anim;
	}
	
	public boolean hasAnimation(String animationName) {
		return animationsMap.containsKey(animationName);
	}
	
	/**
	 * Add a new bone
	 * @param bone
	 */
	public void addBone(Bone bone) {
		boneData = null;
		getBones().add(bone);
	}
	
	/**
	 * Add a bone and assign a parent for it
	 * @param bone
	 * @param parent
	 */
	public void addBone(Bone bone, Bone parent) {
		parent.attachChild(bone);
		addBone(bone);
	}
	
	public Bone getBone(int index) {
		return getBones().get(index);
	}

	@Override
	public void onUpdate(float time) {
		boolean boneUpdate = false;
		for (int i = 0; i < animations.size(); ++i) {
			if (animations.get(i).isPlaying()) {
				animations.get(i).onUpdate(time);
				boneUpdate = true;
			}
		}
		
		if (boneUpdate) {
			
			for (int i = 0, countBones = getBones().size(); i < countBones; ++i) {
				Bone bone = getBones().get(i);
				if (bone.getParent() == null) {
					rootNode.attachChild(bone);
				}
				bone.forceUpdate();
			}
			
			rootNode.update();
			
			if (boneData == null) {
				boneData = new float[16 * getBones().size()];
			}

			updateBoneData();
		}
	}
	
	public float[] getBoneData() {
		if (boneData == null) {
			boneData = new float[16 * getBones().size()];
			updateBoneData();
		}
		return boneData;
	}
	
	private void updateBoneData() {
		for (int i = 0; i < getBones().size(); ++i) {
			float[] modelMatrix = getBones().get(i).getModelMatrix();
			System.arraycopy(modelMatrix, 0, boneData, i*16, 16);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public int getCountBones() {
		return getBones().size();
	}
	
	public static void passData(float[] skeletonBuffer, int bones, Material material, Mesh mesh) {
		passData(skeletonBuffer, bones, 0.1f, material, mesh);
	}
	
	public static void passData(float[] skeletonBuffer, int bones, float size, Material material, Mesh mesh) {
		if (skeletonBuffer != null && skeletonBuffer.length > 0) {
			Program program = material.getProgram();
			passDataIntern(skeletonBuffer,bones,size,material,mesh,program);
		}
	}

	public static void passShadowData(float[] skeletonBuffer, int bones, Material material, Mesh mesh) {
		if (skeletonBuffer != null && skeletonBuffer.length > 0) {
			Program program = SceneManager.getInstance().getRenderer().getShadowProgram(true);
			program.use();
			material.updateHandles();
			passDataIntern(skeletonBuffer,bones,0.1f,material,mesh,program);
		}
	}
	
	private static void passDataIntern(float[] skeletonBuffer, int bones, float size, Material material, Mesh mesh, Program program) {
		
		int boneHandle = TyrGL.glGetUniformLocation(program.handle, material.getBoneParam());
		int sizeHandle = TyrGL.glGetUniformLocation(program.handle, "u_Size");
		int boneIndexHandle = TyrGL.glGetAttribLocation(program.handle, material.getBoneIndexParam());
		int boneWeightHandle = TyrGL.glGetAttribLocation(program.handle, material.getBoneWeightParam());
		
        // Prepare the skeleton data
		TyrGL.glUniformMatrix4fv(boneHandle, bones, false, skeletonBuffer, 0);
		
		if (sizeHandle != -1) {
			TyrGL.glUniform1f(sizeHandle, size);
		}
		
		if (mesh.isUsingVBO()) {
			TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, mesh.getBBuffer());
	        TyrGL.glEnableVertexAttribArray(boneIndexHandle);
	        TyrGL.glVertexAttribPointer(boneIndexHandle, Mesh.MAX_BONES_PER_VERTEX,
	        							TyrGL.GL_FLOAT, false,
						                Mesh.BONE_BYTE_STRIDE * OpenGLRenderer.BYTES_PER_FLOAT, 
						                Mesh.BONE_INDEX_OFFSET * OpenGLRenderer.BYTES_PER_FLOAT);
	        
	        TyrGL.glEnableVertexAttribArray(boneWeightHandle);
	        TyrGL.glVertexAttribPointer(boneWeightHandle, Mesh.MAX_BONES_PER_VERTEX,
	        							TyrGL.GL_FLOAT, false,
						                Mesh.BONE_BYTE_STRIDE * OpenGLRenderer.BYTES_PER_FLOAT, 
						                Mesh.BONE_WEIGHT_OFFSET * OpenGLRenderer.BYTES_PER_FLOAT);
		} else {
	        FloatBuffer boneBuffer = mesh.getBoneBuffer();
	        TyrGL.glEnableVertexAttribArray(boneIndexHandle);
	        boneBuffer.position(Mesh.BONE_INDEX_OFFSET);
	        TyrGL.glVertexAttribPointer(boneIndexHandle, Mesh.MAX_BONES_PER_VERTEX,
	        							TyrGL.GL_FLOAT, false,
						                Mesh.BONE_BYTE_STRIDE * OpenGLRenderer.BYTES_PER_FLOAT, 
						                mesh.getBoneBuffer());
	        
	        
	        boneBuffer.position(Mesh.BONE_WEIGHT_OFFSET);
	        TyrGL.glEnableVertexAttribArray(boneWeightHandle);
	        TyrGL.glVertexAttribPointer(boneWeightHandle, Mesh.MAX_BONES_PER_VERTEX,
	        							TyrGL.GL_FLOAT, false,
						                Mesh.BONE_BYTE_STRIDE * OpenGLRenderer.BYTES_PER_FLOAT, 
						                mesh.getBoneBuffer());
		}
	}

	public List<Bone> getBones() {
		return bones;
	}
	
}
