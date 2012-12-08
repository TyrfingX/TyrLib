package com.tyrlib2.scene;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.math.Vector3;

/**
 * This class manages the positioning of one or multiple scene objects.
 * @author Sascha
 *
 */

public class SceneNode {
	
	private List<SceneObject> attachedObjects;
	private List<SceneNode> children;
	private SceneNode parent;
	
	/**
	 * Creates a SceneNode with default position (0,0,0)
	 */
	public SceneNode() {
		attachedObjects = new ArrayList<SceneObject>();
		children = new ArrayList<SceneNode>();
	}
	
	/**
	 * Creates a SceneNode at the passed position
	 * @param pos
	 */
	
	public SceneNode(Vector3 pos) {
		attachedObjects = new ArrayList<SceneObject>();
		children = new ArrayList<SceneNode>();
	}
	
	/**
	 * Gets the absolute position of this SceneNode
	 * @return Absolute position of this SceneNode
	 */
	
	public Vector3 getAbsolutePos() {
		return null;
	}
	
	/**
	 * Gets the position of this SceneNode relative to its parent
	 * @return The position of this SceneNode relative to its parent
	 */
	
	public Vector3 getRelativePos() {
		return null;
	}
	
	/**
	 * Attaches a passed SceneObject to this SceneNode
	 * @param object The SceneObject to be attached
	 */
	
	public void attachSceneObject(SceneObject object) {
		attachedObjects.add(object);
	}
	
	/**
	 * Detaches a passed SceneObject from this SceneNode
	 * @param object The SceneObject to be detached
	 */
	
	public void detachSceneObject(SceneObject object) {
		attachedObjects.remove(object);
	}
	
	/**
	 * Attaches a SceneNode as a child to this SceneNode
	 * @param node
	 */
	
	public void attachChild(SceneNode node) {
		
	}
	
	public void detachChild(SceneNode node) {
		
	}
	
	public void attachTo(SceneNode node) {
		
	}
	
	public void detach() {
		
	}
	
	public SceneNode getParent() {
		return parent;
	}
	
}
