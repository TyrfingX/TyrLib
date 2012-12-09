package com.tyrlib2.scene;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.tyrlib2.math.Vector3;

/**
 * This class manages the positioning of one or multiple scene objects.
 * @author Sascha
 *
 */

public class SceneNode {
	
	/** The SceneObjects attached to this node **/
	protected List<SceneObject> attachedObjects;
	
	/** The children of this node **/
	private List<SceneNode> children;
	
	/** The parent node **/
	private SceneNode parent;
	
	/** The relative position of this node **/
	private Vector3 pos;
	
	/** Absolute Position of this node in the world **/
	private Vector3 absolutePos;
	
	/** Transforms model space to world space **/
	protected float[] modelMatrix = new float[16];
	
	/** Was this node updated after the last update call? **/
	protected boolean update;
	
	/**
	 * Creates a SceneNode with default position (0,0,0)
	 */
	public SceneNode() {
		attachedObjects = new ArrayList<SceneObject>();
		children = new ArrayList<SceneNode>();
		setRelativePos(new Vector3(0,0,0));
		
		Matrix.setIdentityM(modelMatrix, 0);
	}
	
	/**
	 * Creates a SceneNode at the passed position
	 * @param pos
	 */
	
	public SceneNode(Vector3 pos) {
		attachedObjects = new ArrayList<SceneObject>();
		children = new ArrayList<SceneNode>();
		setRelativePos(pos);
	}
	
	/**
	 * Gets the absolute position of this SceneNode
	 * Currently iterates through all parent nodes, therefore expensive.
	 * @return Absolute position of this SceneNode
	 */
	
	public Vector3 getAbsolutePos() {
		
		absolutePos = pos;
		
		if (parent != null) {
			absolutePos = absolutePos.add(parent.getAbsolutePos());
		}
		
		return absolutePos;
	}
	
	/**
	 * Gives last calculated absolute world position of this node.
	 * May be invalid.
	 * @return
	 */
	
	public Vector3 getCachedAbsolutePos() {
		return absolutePos;
	}
	
	/**
	 * Sets the relative pos, so that the resulting
	 * absolute pos will match with the desired position
	 * @param pos	The absolute world position
	 */
	
	public void setAbsolutePos(Vector3 pos) {
		Vector3 parentPos = parent.getAbsolutePos();
		Vector3 newPos = pos.sub(parentPos);
		this.setRelativePos(newPos);
	}
	
	/**
	 * Gets the position of this SceneNode relative to its parent
	 * @return The position of this SceneNode relative to its parent
	 */
	
	public Vector3 getRelativePos() {
		return pos;
	}
	
	/**
	 * Set the relative position
	 * @param pos	The new position relative to the parent
	 */
	
	public void setRelativePos(Vector3 pos) {
		this.pos = new Vector3(pos);
		update = true;
	}
	
	/**
	 * Attaches a passed SceneObject to this SceneNode
	 * @param object The SceneObject to be attached
	 */
	
	public void attachSceneObject(SceneObject object) {
		object.attachTo(this);
	}
	
	/**
	 * Detaches a passed SceneObject from this SceneNode
	 * @param object The SceneObject to be detached
	 */
	
	public void detachSceneObject(SceneObject object) {
		attachedObjects.remove(object);
	}
	
	/**
	 * Get the total number of attached objects
	 * @return	The total number of attached objects
	 */
	
	public int getCountAttachedObjects() {
		return children.size();
	}
	
	/**
	 * Attaches a SceneNode as a child to this SceneNode
	 * @param node	The node to be attached
	 */
	
	public void attachChild(SceneNode node) {
		children.add(node);
		node.parent = this;
	}
	
	/**
	 * Detaches a SceneNode
	 * @param node	The node to be detached
	 */
	
	public void detachChild(SceneNode node) {
		children.remove(node);
		node.parent = null;
	}
	
	/**
	 * Gets the total number of child nodes
	 * @return	The total number of child nodes
	 */
	
	public int getCountChildren() {
		return children.size();
	}
	
	/**
	 * Detaches this node
	 */
	
	public void detach() {
		parent.detachChild(this);
	}
	
	/**
	 * Get the parent scene node
	 * @return	The parent scene node
	 */
	
	public SceneNode getParent() {
		return parent;
	}
	
	/**
	 * Create a new child scene node
	 * @param pos	The position of the child scene node relative to this node
	 * @return		The newly created child scene node
	 */
	
	public SceneNode createChild(Vector3 pos) {
		SceneNode child = new SceneNode(pos);
		this.attachChild(child);
		return child;
	}
	
	/**
	 * Updates the model matrix
	 */
	
	public void update(Vector3 parentPos) {
		if (update) {
			Matrix.setIdentityM(modelMatrix, 0);
			absolutePos = parentPos.add(pos);
			Matrix.translateM(modelMatrix, 0, absolutePos.x, absolutePos.y, absolutePos.z);
			update = false;
		}
		
		for (int i = 0; i < children.size(); ++i) {	
			children.get(i).update(absolutePos);
		}
	}
	
	
	/**
	 * Get the model matrix for all children
	 * @return The model matrix
	 */
	public float[] getModelMatrix() {
		return modelMatrix;
	}
}
