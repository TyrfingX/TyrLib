package com.tyrlib2.renderables;

import com.tyrlib2.math.AABB;
import com.tyrlib2.renderer.BoundedRenderable;

/**
 * A smaller chunk of a bigger terrain
 * @author Sascha
 *
 */

public class TerrainChunk extends BoundedRenderable {

	@Override
	public void render(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AABB createUntransformedBoundingBox() {
		return null;
	}

}
