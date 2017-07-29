package com.tyrfing.games.tyrlib3.view.graphics;

import com.tyrfing.games.tyrlib3.view.graphics.renderables.Entity;

/**
 * A interface for a creator of entity objects
 * @author Sascha
 *
 */

public interface IEntityFactory {
	public Entity create();
}
