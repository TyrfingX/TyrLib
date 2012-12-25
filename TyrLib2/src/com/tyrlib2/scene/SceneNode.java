package com.tyrlib2.scene;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.tyrlib2.math.Quaternion;
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
	protected List<SceneNode> children;
	
	/** The parent node **/
	protected SceneNode parent;
	
	/** The relative position of this node **/
	protected Vector3 pos;
	
	/** Absolute Position of this node in the world **/
	protected Vector3 absolutePos;
	
	/** Rotation of this node relative to its parent **/
	protected Quaternion rot;
	
	/** Absolute rotation in the world **/
	protected Quaternion absoluteRot;
	
	/** relative scaling of this node to the parent node **/
	protected Vector3 scale = new Vector3(1,1,1);
	
	/** absolute scaling of this node in the world **/
	protected Vector3 absoluteScale = new Vector3(1,1,1);
	
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
		setRelativePos(new Vector3());
		setRelativeRot(new Quaternion(0,0,0,1));
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
		setRelativeRot(new Quaternion(0,0,0,1));
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
		Vector3 newPos = pos;
		if (parent != null) {
			Vector3 parentPos = parent.getAbsolutePos();
			newPos = pos.sub(parentPos);
		}
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
	 * Gets the absolute rotation of this node in world space
	 * @return	The absolute rotation of this node in world space
	 */
	
	public Quaternion getAbsoluteRot() {
		absoluteRot = rot;
		
		if (parent != null) {
			rot = parent.getAbsoluteRot().multiply(rot);
		}
		
		return absoluteRot;
	}
	
	
	/**
	 * Get the cashed absolute rotation of this node in world space
	 * May be invalid.
	 * @return	The cashed absolute rotation of this node in world space
	 */
	
	public Quaternion getCachedAbsoluteRot() {
		return absoluteRot;
	}
	
	
	/**
	 * Sets the relative rotation of this node so that it will have
	 * the passed absolute rotation of world space
	 * @param quaternion	The rotation in world space
	 */
	public void setAbsoluteRot(Quaternion rotation) {
		Quaternion newRot = rotation;
		if (parent != null) {
			Quaternion parentRot = parent.getAbsoluteRot();
			newRot = rotation.multiply(parentRot.inverse());
		}
		this.setRelativeRot(newRot);
	}
	
	/**
	 * Set the relative rotation of this node
	 * @param rotation	The rotation relative to its parent this node will have
	 */
	
	public void setRelativeRot(Quaternion rotation) {
		this.rot = rotation;
		update = true;
	}
	
	/**
	 * Gets the rotation of this node relative to its parent
	 * @return	The rotation of this node relative to its parent
	 */
	
	public Quaternion getRelativeRot() {
		return rot;
	}
	
	
	/**
	 * Gets the absolute scaling of this SceneNode
	 * Currently iterates through all parent nodes, therefore expensive.
	 * @return Absolute scaling of this SceneNode
	 */
	
	public Vector3 getAbsoluteScale() {
		
		absoluteScale = scale;
		
		if (parent != null) {
			absoluteScale = absoluteScale.add(parent.getAbsoluteScale());
		}
		
		return absoluteScale;
	}
	
	/**
	 * Gives last calculated absolute world scaling of this node.
	 * May be invalid.
	 * @return
	 */
	
	public Vector3 getCachedAbsoluteScale() {
		return absoluteScale;
	}
	
	/**
	 * Sets the relative scaling, so that the resulting
	 * absolute scaling will match with the desired position
	 * @param scale	The absolute world scaling
	 */
	
	public void setAbsoluteScale(Vector3 scale) {
		Vector3 newScale = scale;
		if (parent != null) {
			Vector3 parentScale = parent.getAbsoluteScale();
			newScale = scale.sub(parentScale);
		}
		this.setRelativeScale(newScale);
	}
	
	/**
	 * Gets the scaling of this SceneNode relative to its parent
	 * @return The scaling of this SceneNode relative to its parent
	 */
	
	public Vector3 getRelativeScale() {
		return scale;
	}
	
	/**
	 * Set the relative scaling
	 * @param scale	The new scale relative to the parent
	 */
	
	public void setRelativeScale(Vector3 scale) {
		this.scale = new Vector3(scale);
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
	
	public void update(Vector3 parentPos, Quaternion parentRot, Vector3 parentScale, float[] parentTransform) {
		if (update) {			
			// there was an update, all children of this tree must be updated
			updateAll(parentPos, parentRot, absoluteScale, parentTransform);
		} else {
			// There was no update, hopefully the children also had no update
			for (int i = 0; i < children.size(); ++i) {	
				children.get(i).update(absolutePos, absoluteRot, absoluteScale, modelMatrix);
			}
		}
		

	}
	
	/**
	 * Root update
	 */
	
	public void update() {
		for (int i = 0; i < children.size(); ++i) {	
			children.get(i).update(pos, rot, absoluteScale, modelMatrix);
		}
	}
	
	/**
	 * Updates the model matrix. There was an update in a parent node, therefore all children need to
	 * be informed and update their matrices accordingly.
	 */
	
	public void updateAll(Vector3 parentPos, Quaternion parentRot, Vector3 parentScale, float[] parentTransform) {
		
		Matrix.setIdentityM(modelMatrix, 0);
		absolutePos = parentPos.add(pos);
		absoluteRot = parentRot.multiply(parentRot);
		absoluteScale = new Vector3(scale.x * parentScale.x, scale.y * parentScale.y, scale.z * parentScale.z);
		
		Matrix.translateM(modelMatrix, 0, pos.x, pos.y, pos.z);
		Matrix.multiplyMM(modelMatrix, 0, rot.toMatrix(), 0, modelMatrix, 0);
		Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);
		
		Matrix.multiplyMM(modelMatrix, 0, parentTransform, 0, modelMatrix, 0);
		update = false;
		
		
		for (int i = 0; i < children.size(); ++i) {	
			children.get(i).updateAll(absolutePos, absoluteRot, absoluteScale, modelMatrix);
		}
	}
	
	
	/**
	 * Get the model matrix for all children
	 * @return The model matrix
	 */
	public float[] getModelMatrix() {
		return modelMatrix;
	}
	
	
	/**
	 * Translate this node by a vector3
	 * @param translation	The vector3 by which this node will be translated
	 */
	
	public void translate(Vector3 translation) {
		setRelativePos(pos.add(translation));
	}
	
	/**
	 * Rotates this node by the passed rotation
	 * @param rotation	The rotation which will be applied to this node
	 */
	
	public void rotate(Quaternion rotation) {
		setRelativeRot(this.rot.multiply(rotation));
	}
	
	/**
	 * Scales this node by the passed scaling
	 * @param scale	The scaling which will be applied to this node
	 */
	
	public void scale(Vector3 scale) {
		setRelativeScale(this.scale.add(scale));
	}
	
	
}
