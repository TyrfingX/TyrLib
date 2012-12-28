package com.tyrlib2.util;

import com.tyrlib2.renderables.Entity;

/**
 * A interface for a creator of entity objects
 * @author Sascha
 *
 */

public interface IEntityFactory {
	public Entity create();
}
