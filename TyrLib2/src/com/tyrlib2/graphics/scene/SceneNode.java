package com.tyrlib2.graphics.scene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/**
 * This class manages the positioning of one or multiple scene objects.
 * @author Sascha
 *
 */

public class SceneNode implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3917422812324934988L;

	/** The SceneObjects attached to this node **/
	protected transient List<SceneObject> attachedObjects;
	
	/** The children of this node **/
	protected transient List<SceneNode> children;
	
	/** The parent node **/
	protected transient SceneNode parent;
	
	/** The relative position of this node **/
	protected Vector3 pos = new Vector3();
	
	/** Absolute Position of this node in the world **/
	protected Vector3 absolutePos = new Vector3();
	
	/** Rotation of this node relative to its parent **/
	protected Quaternion rot = new Quaternion(0,0,0,1);
	
	/** Absolute rotation in the world **/
	protected Quaternion absoluteRot = new Quaternion();
	
	/** relative scaling of this node to the parent node **/
	protected Vector3 scale = new Vector3(1,1,1);
	
	/** absolute scaling of this node in the world **/
	protected Vector3 absoluteScale = new Vector3(1,1,1);
	
	/** Transforms model space to world space **/
	protected transient float[] modelMatrix = new float[16];
	
	/** This node has been transformed and requires an update for itself and all children **/
	protected transient boolean update;
	
	/** A child node requires an update. **/
	protected transient boolean dirty;
	
	private static float[] translation = new float[16];
	private static float[] rotation = new float[16];
	private static float[] scaling = new float[16];
	private static float[] transPos = new float[4];
	private static float[] untransfPos = { 0, 0, 0, 1.0f };
	
	/**
	 * Creates a SceneNode with default position (0,0,0)
	 */
	public SceneNode() {
		attachedObjects = new ArrayList<SceneObject>();
		children = new ArrayList<SceneNode>();
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
		
		Matrix.setIdentityM(modelMatrix, 0);
	}
	
	
	/** 
	 * Force this node to update 
	 * **/
	public void forceUpdate() {
		update = true;
		setDirty();
	}
	
	public void setDirty() {
		dirty = true;
		if (parent != null) {
			if (!parent.dirty) {
				parent.setDirty();
			}
		}
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
		return new Vector3(absolutePos);
	}
	
	public Vector3 getCachedAbsolutePosVector() {
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
			Vector3 parentPos = parent.getCachedAbsolutePosVector();
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
		this.pos.x = pos.x;
		this.pos.y = pos.y;
		this.pos.z = pos.z;
		forceUpdate();
	}
	
	public void setRelativePos(float x, float y, float z) {
		this.pos.x = x;
		this.pos.y = y;
		this.pos.z = z;
		forceUpdate();
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
		this.rot.x = rotation.x;
		this.rot.y = rotation.y;
		this.rot.z = rotation.z;
		this.rot.w = rotation.w;
		forceUpdate();
	}
	
	public void setRelativeRot(float x, float y, float z, float w) {
		rot.x = x;
		rot.y = y;
		rot.z = z;
		rot.w = w;
		forceUpdate();
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
		forceUpdate();
	}
	
	/**
	 * Attaches a passed SceneObject to this SceneNode
	 * @param object The SceneObject to be attached
	 */
	
	public void attachSceneObject(SceneObject object) {
		object.attachTo(this);
		forceUpdate();
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
		
		this.setDirty();
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
		if (parent != null) {
			parent.detachChild(this);
			parent = null;
		}
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
	 * Create a new child scene node at the parents origin
	 * @return	The newely created child scene node with an relative offset of (0,0,0) to its parent
	 */
	
	public SceneNode createChild() {
		return createChild(new Vector3());
	}
	
	/**
	 * Updates the model matrix
	 */
	
	public void update(Quaternion parentRot, Vector3 parentScale, float[] parentTransform) {
		if (update) {			
			// there was an update, all children of this tree must be updated
			updateAll(parentRot, parentScale, parentTransform);
		} else {
			
			if (dirty) {
				// There was no update, but some child node requires an update
				for (int i = 0; i < children.size(); ++i) {	
					children.get(i).update(absoluteRot, absoluteScale, modelMatrix);
				}
				dirty = false;
			}
		}
		

	}
	
	/**
	 * Root update
	 */
	
	public void update() {
		for (int i = 0; i < children.size(); ++i) {	
			children.get(i).update(rot, scale, modelMatrix);
		}
		
		dirty = false;
	}
	
	private void setIdentityMatrix(float[] matrix) {
		matrix[0] = 1;
		matrix[1] = 0;
		matrix[2] = 0;
		matrix[3] = 0;
		
		matrix[4] = 0;
		matrix[5] = 1;
		matrix[6] = 0;
		matrix[7] = 0;
		
		matrix[8] = 0;
		matrix[9] = 0;
		matrix[10] = 1;
		matrix[11] = 0;
		
		matrix[12] = 0;
		matrix[13] = 0;
		matrix[14] = 0;
		matrix[15] = 1;
	}
	
	/**
	 * Updates the model matrix. There was an update in a parent node, therefore all children need to
	 * be informed and update their matrices accordingly.
	 */
	
	public void updateAll(Quaternion parentRot, Vector3 parentScale, float[] parentTransform) {
		
		setIdentityMatrix(modelMatrix);
		parentRot.multiply(rot, absoluteRot);
		absoluteScale.x = scale.x * parentScale.x;
		absoluteScale.x = scale.y * parentScale.y;
		absoluteScale.x = scale.z * parentScale.z;
		
		rot.toMatrix(rotation);
		setIdentityMatrix(translation);
		setIdentityMatrix(scaling);
		
		Matrix.scaleM(scaling, 0, scale.x, scale.y, scale.z);
		Matrix.multiplyMM(modelMatrix, 0, scaling, 0, modelMatrix, 0);
		
		Matrix.multiplyMM(modelMatrix, 0, rotation, 0, modelMatrix, 0);
		Matrix.translateM(translation, 0, pos.x, pos.y, pos.z);
		Matrix.multiplyMM(modelMatrix, 0, translation, 0, modelMatrix, 0);
		Matrix.multiplyMM(modelMatrix, 0, parentTransform, 0, modelMatrix, 0);
		
		Matrix.multiplyMV(transPos, 0, modelMatrix, 0, untransfPos, 0);
		
		absolutePos.x = transPos[0];
		absolutePos.y = transPos[1];
		absolutePos.z = transPos[2];
	
		int countAttachedObjects = attachedObjects.size();
		for (int i = 0; i < countAttachedObjects; ++i) {
			SceneObject so = attachedObjects.get(i);
			so.onTransformed();
		}
		
		update = false;
		dirty = false;
		
		
		for (int i = 0; i < children.size(); ++i) {	
			children.get(i).updateAll(absoluteRot, absoluteScale, modelMatrix);
		}
		
		Matrix.scaleM(scaling, 0, absoluteScale.x, absoluteScale.y, absoluteScale.z);
		
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
		pos.x += translation.x;
		pos.y += translation.y;
		pos.z += translation.z;
		forceUpdate();
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
