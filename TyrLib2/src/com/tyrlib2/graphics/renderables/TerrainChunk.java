package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.renderer.BoundedRenderable;
import com.tyrlib2.math.AABB;

/**
 * A smaller chunk of a bigger terrain
 * @author Sascha
 *
 */

public class TerrainChunk extends BoundedRenderable {

	private int insertionID;

	@Override
	public void render(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AABB createUntransformedBoundingBox() {
		return null;
	}

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}

}
