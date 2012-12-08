package com.tyrlib2.scene;

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
	 * @param node
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
}
