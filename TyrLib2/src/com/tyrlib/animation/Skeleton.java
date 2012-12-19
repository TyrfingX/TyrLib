package com.tyrlib.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages skeletal animations by using bones.
 * @author Sascha
 *
 */

public class Skeleton {
	
	private Map<String, Animation> animations;
	protected List<Bone> bones;
	
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
	}
	
	/**
	 * Get an existing animation
	 * @param animationName
	 */
	public void getAnimation(String animationName) {
		animations.get(animationName);
	}
	
	/**
	 * Add a new bone
	 * @param bone
	 */
	public void addBone(Bone bone) {
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
	
	
}
