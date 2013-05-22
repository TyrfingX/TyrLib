package com.tyrlib2.util;

import com.tyrlib2.math.AABB;

public interface IBoundingBoxTreeQuery {
	public boolean intersectsAABB(AABB aabb);
}
