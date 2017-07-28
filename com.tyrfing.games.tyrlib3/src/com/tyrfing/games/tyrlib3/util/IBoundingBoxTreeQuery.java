package com.tyrfing.games.tyrlib3.util;

import com.tyrfing.games.tyrlib3.math.AABB;

public interface IBoundingBoxTreeQuery {
	public boolean intersectsAABB(AABB aabb);
}
