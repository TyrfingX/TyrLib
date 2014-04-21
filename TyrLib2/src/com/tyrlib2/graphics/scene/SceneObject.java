package com.tyrlib2.graphics.scene;

import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/**
 * Basic class for representing scene objects. The objects can
 * be attached to SceneNode objects in order to be positioned.
 * @author Sascha
 *
 */

public abstract class SceneObject {
	
	/** Parent node of this SceneObject **/
	protected SceneNode parent;
	
	/** Mask for performing queries **/
	protected int mask = 1;
	
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
		if (parent != null) {
			parent.attachedObjects.remove(this);
			parent = null;
		}
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
			Vector3 cached = parent.getCachedAbsolutePosVector();
			if (cached == null) {
				cached = new Vector3();
			}
			
			return cached;
		} 
		
		return new Vector3();
	}
	
	public Quaternion getAbsoluteRotation() {
		if (parent != null) {
			Quaternion cached = parent.getCachedAbsoluteRot();
			if (cached == null) {
				cached = new Quaternion();
			}
			
			return cached;
		} 
		
		return new Quaternion();
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
	
	public void setRelativePos(Vector3 pos) {
		if (parent != null) {
			parent.setRelativePos(pos);
		}
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

	public int getMask() {
		return mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}
	
	
	
}
