package com.tyrfing.games.tyrlib3.model.graphics.scene;

import com.tyrfing.games.tyrlib3.model.math.AABB;

public interface IBoundingBoxTreeQuery {
	public boolean intersectsAABB(AABB aabb);
}
