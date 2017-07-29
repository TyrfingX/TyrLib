package com.tyrfing.games.tyrlib3.model.graphics.scene;

import com.tyrfing.games.tyrlib3.model.math.AABB;

/**
 * An interface for performing scene queries to a spatial hierachy
 * @author Sascha
 *
 */

public interface ISceneQuery {
	
	/**
	 * The query condition the object must fullfil
	 * @param aabb
	 * @return true iff. the aabb fullfills the query condition
	 */
	public boolean intersects(AABB aabb);
	
	/**
	 * If the object fullfills the query condition use this callback
	 * to work on the actual object
	 * @param sceneObject
	 */
	public void callback(BoundedSceneObject sceneObject);
}
