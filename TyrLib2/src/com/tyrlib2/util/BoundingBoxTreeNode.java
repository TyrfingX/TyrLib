package com.tyrlib2.util;

import java.util.Arrays;
import java.util.Comparator;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

public class BoundingBoxTreeNode extends BoundedSceneObject {
	private AABB aabb;
	private BoundingBoxTreeNode childLeft;
	private BoundingBoxTreeNode childRight;
	private boolean hasChildren;
	
	public static int MAX_POINTS = 20;
	
	public class XComparator implements Comparator<Vector3> {
		@Override
		public int compare(Vector3 v1, Vector3 v2) {
			if (v1.x < v2.x) {
				return 1;
			} else if (v1.x > v2.x) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	public class YComparator implements Comparator<Vector3> {
		@Override
		public int compare(Vector3 v1, Vector3 v2) {
			if (v1.y < v2.y) {
				return 1;
			} else if (v1.y > v2.y) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	public class ZComparator implements Comparator<Vector3> {
		@Override
		public int compare(Vector3 v1, Vector3 v2) {
			if (v1.z < v2.z) {
				return 1;
			} else if (v1.z > v2.z) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	public BoundingBoxTreeNode(Vector3[] points) {
		this(points, 0, points.length-1);

		Comparator<Vector3> comparator;
			
		float lengthX = aabb.max.x - aabb.min.x;
		float lengthY = aabb.max.y - aabb.min.y;
		float lengthZ = aabb.max.z - aabb.min.z;
		
		if (lengthX >= lengthY && lengthX >= lengthZ) {
			comparator = new XComparator();
		} else if (lengthY >= lengthX && lengthY >= lengthZ) {
			comparator = new YComparator();
		} else {
			comparator = new ZComparator();
		}
		
		Arrays.sort(points, 0, points.length-1, comparator);

		aabb = AABB.createFromPoints(points, 0, points.length-1);
		
		if (points.length - 1> MAX_POINTS) {
			createChildren(points, 0, points.length-1);
		}			
	}
	
	public BoundingBoxTreeNode(Vector3[] points, int start, int end) {
		aabb = AABB.createFromPoints(points, start, end);
		
		if (end - start > MAX_POINTS) {
			createChildren(points, start, end);
		}			
	}
	
	private void createChildren(Vector3[] points, int start, int end) {

		hasChildren = true;
		
		childLeft = new BoundingBoxTreeNode(points, start, (start+end)/2);
		childRight = new BoundingBoxTreeNode(points, (start+end)/2+1, end);
	}
	
	public AABB getBoundingBox() {
		return aabb;
	}
	
	public boolean query(IBoundingBoxTreeQuery query) {
		
		if (query.intersectsAABB(aabb)) {
			if (hasChildren) {
				return childLeft.query(query) || childRight.query(query);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public void setBoundingBoxVisible(boolean visible) {
		super.setBoundingBoxVisible(visible);
		if (hasChildren) {
			childLeft.setBoundingBoxVisible(visible);
			childRight.setBoundingBoxVisible(visible);
		}
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		if (hasChildren) {
			childLeft.attachTo(node);
			childRight.attachTo(node);
		}
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		if (hasChildren) {
			childLeft.detach();
			childRight.detach();
		}
		return super.detach();	
	}
}
