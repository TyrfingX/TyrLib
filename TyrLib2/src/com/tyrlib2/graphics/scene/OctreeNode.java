package com.tyrlib2.graphics.scene;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderables.BoundingBox;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

/**
 * A node in an octree
 * @author Sascha
 *
 */

public class OctreeNode extends BoundedSceneObject {
	
	public static final int CHILDREN_PER_NODE = 8;
	
	private final int minimumObjectsPerNode;
	private final int maximumObjectsPerNode;
	
	private List<BoundedSceneObject> objects;
	
	private OctreeNode[] children;
	
	private AABB boundingBox;
	private BoundingBox boundingBoxRenderable;

	private Vector3 center;
	private float dimension;
	
	private OctreeNode parentOctree;

	/** Contains the offsets for calculating the new centers of the child nodes
	 *  Each 3 sequential entries stand for the x,y,z coordinates
	 */
	private static final float[] childCenterOffsets = { -1, -1, 1, 
														1, -1, 1,  
														-1, 1, 1,
														1, 1, 1,
														-1, -1, -1,
														1, -1, -1,
														-1, 1, -1,
														1, 1, -1 };
	
	/**
	 * Create a new octree
	 * @param minimumObjectsPerNode	The minimum number of objects held before merging nodes
	 * @param maximumObjectsPerNode	The maximum number of objects in a node before splitting
	 * 								May be exceeded due to objects not insertable into child nodes
	 * @param center				Center of this octree
	 * @param dimension				Size of the octree in each direction
	 */
	
	public OctreeNode(int minimumObjectsPerNode, int maximumObjectsPerNode, Vector3 center, float dimension) {
		this.minimumObjectsPerNode = minimumObjectsPerNode;
		this.maximumObjectsPerNode = maximumObjectsPerNode;

		this.center = center;
		this.dimension = dimension;
		
		boundingBox = new AABB(	new Vector3(-dimension/2+center.x, -dimension/2+center.y, -dimension/2+center.z),
								new Vector3(dimension/2+center.x, dimension/2+center.y, dimension/2+center.z));
		
		objects = new ArrayList<BoundedSceneObject>();
	}
	
	/**
	 * Insert an object into the octree
	 * Note that the inserted object must provide a none-null aabb!
	 * @param sceneObject	The sceneobject to be inserted
	 */
	
	public void addObject(BoundedSceneObject sceneObject) {
		AABB aabb = sceneObject.getBoundingBox();
		
		if (aabb == null) {
			objects.add(sceneObject);
			return;
		}
		
		if (boundingBox.containsAABB(aabb)) {
		
			if (objects.size() <= maximumObjectsPerNode) {
				objects.add(sceneObject);
			} else {
				
				if (children == null) {
	
					// This node contains the maximum amount of objects, and has not been split yet
					// So before inserting, split it
					split();
				}
				
				// Now try adding the object into a child node
				if (!addObjectIntoChild(sceneObject)) {
					
					// If this doesnt work out, then add the object to this node
					objects.add(sceneObject);
				}
			}
		
		} else {
			// Object doesnt fit into this octree node check parent
			
			if (parentOctree != null) {
				parentOctree.addObject(sceneObject);
			} else {
				// No parent octree existent, create one
				
				Vector3 objectCenter = aabb.getCenter();
				Vector3 vectorToObject = boundingBox.getCenter().vectorTo(objectCenter);
				vectorToObject.normalize();
				
				vectorToObject.x *= dimension/2;
				vectorToObject.y *= dimension/2;
				vectorToObject.z *= dimension/2;
				
				Vector3 parentCenter = new Vector3(	center.x + childCenterOffsets[0] * dimension/2,
													center.y + childCenterOffsets[1] * dimension/2,
													center.z + childCenterOffsets[2] * dimension/2);
				
				float minDistance = parentCenter.vectorTo(vectorToObject).length();
				int cornerIndex = 0;
				
				// Get the nearest corner to the object
				
				for (int i = 1; i < CHILDREN_PER_NODE; ++i) {
					Vector3 corner = new Vector3(	center.x + childCenterOffsets[i*3] * dimension/2,
													center.y + childCenterOffsets[i*3+1] * dimension/2,
													center.z + childCenterOffsets[i*3+2] * dimension/2);
					float distance = corner.vectorTo(vectorToObject).length();
					if (minDistance > distance) {
						minDistance = distance;
						parentCenter = corner;
						cornerIndex = i;
					}
				}
				
				// The octant of this octree node
				int octant = 0;
				
				for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
					if (childCenterOffsets[i*3] == -childCenterOffsets[cornerIndex*3] &&
						childCenterOffsets[i*3+1] == -childCenterOffsets[cornerIndex*3+1] &&
						childCenterOffsets[i*3+2] == -childCenterOffsets[cornerIndex*3+2]) {
						octant = i;
						break;
					}
				}
				
				parentOctree = new OctreeNode(minimumObjectsPerNode, maximumObjectsPerNode, parentCenter, dimension*2);
				parentOctree.children = new OctreeNode[CHILDREN_PER_NODE];
				

				
				for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
					if (i != octant) {
						Vector3 childCenter = new Vector3(	parentCenter.x + childCenterOffsets[i*3] * dimension/2,
															parentCenter.y + childCenterOffsets[i*3+1] * dimension/2,
															parentCenter.z + childCenterOffsets[i*3+2] * dimension/2);
						
						parentOctree.children[i] = new OctreeNode(minimumObjectsPerNode, maximumObjectsPerNode, childCenter, dimension);
						parentOctree.children[i].parentOctree = parentOctree;
					
					} else {
						parentOctree.children[i] = this;
					}
					
					
				}
				
				if (parent != null) {
					parentOctree.attachTo(parent);
				}
				
				
				// We are finished with constructing a parent octree now try inserting the object here
				parentOctree.addObject(sceneObject);
			}
		}
	}
	
	private boolean addObjectIntoChild(BoundedSceneObject sceneObject) {
		AABB aabb = sceneObject.getBoundingBox();
		
		if (aabb != null) {
			for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
				if (children[i].boundingBox.containsAABB(aabb)) {
					children[i].addObject(sceneObject);
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Remove a sceneObject
	 * @param sceneObject	The sceneobject to be removed
	 * @return 				Whether or not there actually was an object to remove
	 */
	
	public boolean removeObject(BoundedSceneObject sceneObject) {
		AABB aabb = sceneObject.getBoundingBox();
		
		if (aabb == null) {
			return objects.remove(sceneObject);
		}
		
		if (boundingBox.containsAABB(aabb)) {
			for (int i = 0; i < objects.size(); ++i) {
				if (objects.get(i) == sceneObject) {
					removeLocalObject(i);
					
					if (children != null && objects.size() < minimumObjectsPerNode) {
						merge();
					}
					
					return true;
				}
			}
			
			if (children != null) {
				for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
					if (children[i].removeObject(sceneObject)) {
						return true;
					}
				}
			}
			
		}
		
		return false;
	}
	
	private void removeLocalObject(int i) {
		objects.set(i, objects.get(objects.size()-1));
		objects.remove(objects.size()-1);
	}

	/**
	 * Update the tree (check the partitions, merging, splitting, etc)
	 */
	
	public void update() {
		for (int i = 0; i < objects.size(); ++i) {
			BoundedSceneObject object = objects.get(i);
			if (object.isDity() && object.getBoundingBox() != null) {
				
				boolean movedToChild = false;
				if (children != null) {
					movedToChild = addObjectIntoChild(object);
				}
				
				if (!movedToChild) {
					if (!boundingBox.containsAABB(object.getBoundingBox())) {
						
						removeLocalObject(i);
						--i;
						
						if (parentOctree != null) {
							parentOctree.addObject(object);
						} else {
							this.addObject(object);
						}
					}
				} else {
					removeLocalObject(i);
					--i;
				}
				
				object.setClean();
			}
		}
		
		if (children != null) {
			for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
				children[i].update();
			}
		}
		
	}
	
	@Override
	public AABB getBoundingBox() {
		return boundingBox;
	}

	@Override
	public void setBoundingBoxVisible(boolean visible) {
		if (boundingBoxRenderable == null && visible) {
			boundingBoxRenderable = new BoundingBox(boundingBox);
			SceneManager.getInstance().getRenderer().addRenderable(boundingBoxRenderable);
			parent.attachSceneObject(boundingBoxRenderable);
		} else if (boundingBoxRenderable != null && !visible) {
			parent.detachSceneObject(boundingBoxRenderable);
			SceneManager.getInstance().destroyRenderable(boundingBoxRenderable);
			boundingBoxRenderable = null;
		}
		
		for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
			if (children != null) {
				children[i].setBoundingBoxVisible(visible);
			}
		}
	}
	
	/**
	 * Splits this node into 8 children inserting as many objects into them as possible
	 */
	
	private void split() {
		children = new OctreeNode[CHILDREN_PER_NODE];
		
		float childDimension = dimension/2;
		
		for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
			Vector3 childCenter = new Vector3(	center.x + childCenterOffsets[i*3] * dimension/4,
												center.y + childCenterOffsets[i*3+1] * dimension/4,
												center.z + childCenterOffsets[i*3+2] * dimension/4);
			
			children[i] = new OctreeNode(minimumObjectsPerNode, maximumObjectsPerNode, childCenter, childDimension);
			children[i].parentOctree = this;
			
			if (parent != null) {
				children[i].attachTo(parent);
			}
		}
		
		for (int i = 0; i < objects.size(); ++i) {
			BoundedSceneObject object = objects.get(i);
			AABB aabb = object.getBoundingBox();
			
			if (aabb != null) {
				for (int j = 0; j < CHILDREN_PER_NODE; ++j) {
					if (children[j].boundingBox.containsAABB(aabb)) {
						children[j].addObject(object);
						
						removeLocalObject(i);
						--i;
						
						break;
					}
				}
			}
		}
	}
	
	public void merge() {
	
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		if (children != null) {
			for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
				children[i].attachTo(node);
			}
		}
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		if (children != null) {
			for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
				children[i].detach();
			}
		}
		return super.detach();	
	}
	
	
	public void query(ISceneQuery query) {
		if (query.intersects(boundingBox)) {
			queryObjects(query);
			queryChildren(query);
		}
	}
	
	private void queryObjects(ISceneQuery query) {
		for (int i = 0; i < objects.size(); ++i) {
			if (query.intersects(objects.get(i).getBoundingBox())) {
				query.callback(objects.get(i));
			}
		}
	}
	
	private void queryChildren(ISceneQuery query) {
		if (children != null) {
			for (int i = 0; i < CHILDREN_PER_NODE; ++i) {
				children[i].query(query);
			}
		}
	}
	
	public OctreeNode getParentOctree() {
		return parentOctree;
	}
	
}
