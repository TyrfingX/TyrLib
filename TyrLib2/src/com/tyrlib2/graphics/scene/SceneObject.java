package com.tyrlib2.graphics.scene;

import com.tyrlib2.math.Vector3;

/**
 * Basic abstract class for representing scene objects. The objects can
 * be attached to SceneNode objects in order to be positioned.
 * @author Sascha
 *
 */

public abstract class SceneObject {
	
	/** Parent node of this SceneObject **/
	protected SceneNode parent;
	
	/**
	 * Attaches this object to a SceneNode
	 * @param node The node onto which this SceneObject will be attached
	 */
	
	public void attachTo(SceneNode node)  {
		node.attachedObjects.add(this);
		parent = node;
	}
	
	/**
	 * Detaches this node from its parent
	 * @returns The old parent node
	 */
	
	public SceneNode detach() {
		SceneNode oldParent = parent;
		parent.attachedObjects.remove(this);
		parent = null;
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
		if (parent != null) {
			return parent.getAbsolutePos();
		} 
		
		return null;
	}
	
	/**
	 * Gets the position of this SceneObject relative to the parent of the parent SceneNode
	 * @return The position of this SceneObject relative to the parent of the parent SceneNode
	 */
	
	public Vector3 getRelativePos() {
		if (parent != null) {
			return parent.getRelativePos();
		}
		
		return null;
	}
	
	/**
	 * Get the model matrix of this scene object
	 * @return	The model matrix of this scene matrix
	 */
	
	public float[] getModelMatrix() {
		if (parent != null) {
			return parent.getModelMatrix();
		} 
		
		return null;
	}
	
	
	/** 
	 * Called if the node to which this scene object is attached to
	 * has been transformed due to translation, rotation, etc
	 */
	
	public void onTransformed() {
	}
	
}
