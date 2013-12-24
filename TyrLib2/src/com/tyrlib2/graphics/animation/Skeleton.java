package com.tyrlib2.graphics.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.scene.SceneNode;

/**
 * This class manages skeletal animations by using bones.
 * @author Sascha
 *
 */

public class Skeleton implements IUpdateable {
	
	private Map<String, Animation> animations;
	protected List<Bone> bones;
	protected float[] boneData = null; 
	protected SceneNode rootNode = new SceneNode();
	
	public Skeleton() {
		animations = new HashMap<String, Animation>();
		bones = new ArrayList<Bone>();
	}
	
	/** 
	 * Add a new animation
	 * @param animation
	 */
	public void addAnimation(Animation animation) {
		animations.put(animation.name, animation);
		animation.skeleton = this;
	}
	
	/**
	 * Get an existing animation
	 * @param animationName
	 */
	public Animation getAnimation(String animationName) {
		Animation anim = animations.get(animationName);
		return anim;
	}
	
	public boolean hasAnimation(String animationName) {
		return animations.containsKey(animationName);
	}
	
	/**
	 * Add a new bone
	 * @param bone
	 */
	public void addBone(Bone bone) {
		boneData = null;
		bones.add(bone);
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
		return bones.get(index);
	}

	@Override
	public void onUpdate(float time) {
		boolean boneUpdate = false;
		for (Animation anim : animations.values()) {
			if (anim.playing) {
				anim.onUpdate(time);
				boneUpdate = true;
			}
		}
		
		if (boneUpdate) {
			
			for (int i = 0; i < bones.size(); ++i) {
				Bone bone = bones.get(i);
				if (bone.getParent() == null) {
					rootNode.attachChild(bone);
				}
				bone.forceUpdate();
			}
			
			rootNode.update();
			
			if (boneData == null) {
				boneData = new float[16 * bones.size()];
			}

			updateBoneData();
		}
	}
	
	public float[] getBoneData() {
		if (boneData == null) {
			boneData = new float[16 * bones.size()];
			updateBoneData();
		}
		return boneData;
	}
	
	private void updateBoneData() {
		for (int i = 0; i < bones.size(); ++i) {
			float[] modelMatrix = bones.get(i).getModelMatrix();
			System.arraycopy(modelMatrix, 0, boneData, i*16, 16);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public int getCountBones() {
		return bones.size();
	}
	
	
}
