package com.tyrlib2.util;

import android.opengl.Matrix;

import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

public class PointBoundingBoxTreeQuery implements IBoundingBoxTreeQuery {
	
	private Vector3 point;
	
	private static float matrix[] = new float[16];
	private static float vector[] = { 0, 0, 0, 1 };
	
	public PointBoundingBoxTreeQuery(BoundingBoxTree tree, Vector3 point) {
		Matrix.invertM(matrix, 0, tree.getParent().getModelMatrix(), 0);
		vector[0] = point.x;
		vector[1] = point.y;
		vector[2] = point.z;
		Matrix.multiplyMV(vector, 0, matrix, 0, vector, 0);
		
		this.point = new Vector3(vector[0], vector[1], vector[2]);
	}

	@Override
	public boolean intersectsAABB(AABB aabb) {
		return aabb.containsPoint(point);
	}
}
