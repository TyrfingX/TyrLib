package com.tyrlib.animation;

import com.tyrlib2.scene.SceneNode;

/**
 * Represents a single bone of a skeleton. Further Bones, SceneNode or vertices may be bound to
 * this bone.
 * @author Sascha
 *
 */

public class Bone extends SceneNode {
	protected String name;
	
	public Bone(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
