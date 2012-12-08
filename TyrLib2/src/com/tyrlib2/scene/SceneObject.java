package com.tyrlib2.scene;

import com.tyrlib2.math.Vector3;

/**
 * Basic abstract class for representing scene objects. The objects can
 * be attached to SceneNode objects in order to be positioned.
 * @author Sascha
 *
 */

public abstract class SceneObject {
	
	/** Parent node of this SceneObject **/
	private SceneNode parent;
	
	/**
	 * Attaches this object to a SceneNode
	 * @param node The node onto which this SceneObject will be attached
	 */
	
	public void attachTo(SceneNode node)  {
		
	}
	
	/**
	 * Detaches this node from its parent
	 * @returns The old parent node
	 */
	
	public SceneNode detach() {
		SceneNode oldParent = parent;
		return oldParent;
	}
	
	/**
	 * Gets the parent SceneNode of this object
	 * @return The parent SceneNode of this object
	 */
	
	public SceneNode getParent() {
		return parent;
	}
	
	/**
	 * Gets the absolute position of this SceneObject
	 * @return Absolute position of this SceneObject
	 */
	
	public Vector3 getAbsolutePos() {
		return parent.getAbsolutePos();
	}
	
	/**
	 * Gets the position of this SceneObject relative to the parent of the parent SceneNode
	 * @return The position of this SceneObject relative to the parent of the parent SceneNode
	 */
	
	public Vector3 getRelativePos() {
		return parent.getRelativePos();
	}
	
}
