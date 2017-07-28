package com.tyrfing.games.tyrlib3.util;

import com.tyrfing.games.tyrlib3.math.AABB;
import com.tyrfing.games.tyrlib3.math.Matrix;
import com.tyrfing.games.tyrlib3.math.Vector3F;

public class PointBoundingBoxTreeQuery implements IBoundingBoxTreeQuery {
	
	private Vector3F point;
	
	private static float matrix[] = new float[16];
	private static float vector[] = { 0, 0, 0, 1 };
	
	public PointBoundingBoxTreeQuery(BoundingBoxTree tree, Vector3F point) {
		Matrix.invertM(matrix, 0, tree.getParent().getModelMatrix(), 0);
		vector[0] = point.x;
		vector[1] = point.y;
		vector[2] = point.z;
		Matrix.multiplyMV(vector, 0, matrix, 0, vector, 0);
		
		this.point = new Vector3F(vector[0], vector[1], vector[2]);
	}

	@Override
	public boolean intersectsAABB(AABB aabb) {
		return aabb.containsPoint(point);
	}
}
