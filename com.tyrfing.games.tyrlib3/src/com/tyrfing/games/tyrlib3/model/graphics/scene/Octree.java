package com.tyrfing.games.tyrlib3.model.graphics.scene;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;

/**
 * Partitions the space for the ease of scene management
 * @author Sascha
 *
 */

public class Octree extends SceneObject {
	private OctreeNode root;
	
	public Octree(int minimumObjectsPerNode, int maximumObjectsPerNode, Vector3F center, float dimension) {
		root = new OctreeNode(minimumObjectsPerNode, maximumObjectsPerNode, center, dimension);
	}
	
	public void addObject(BoundedSceneObject sceneObject) {
		root.addObject(sceneObject);
		getCurrentRoot();
	}
	
	public void removeObject(BoundedSceneObject sceneObject) {
		if (sceneObject.octree != null) {
			sceneObject.octree.removeObject(sceneObject);
			getCurrentRoot();
		}
	}
	
	public void setBoundingBoxVisible(boolean visible) {
		root.setBoundingBoxVisible(visible);
	}
	
	public void update() {
		root.update();
		getCurrentRoot();
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		root.attachTo(node);
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		root.detach();
		return super.detach();	
	}
	
	public void query(ISceneQuery query) {
		root.query(query);
	}
	
	public void query(ISceneQuery query, BoundedSceneObject startNode) {
		startNode.octree.query(query);
	}
	
	private void getCurrentRoot() {
		while (root.getParentOctree() != null) {
			root = root.getParentOctree();
		}
	}
	
	public boolean checkDuplicates() {
		return root.checkDuplicates();
	}
	
	
}
