package com.tyrlib2.renderer;

/**
 * Basic interface for objects providing a render capability
 * @author Sascha
 *
 */

public interface IRenderable {
	
	/** Renders this object the matrix containing projection and view will be passed **/
	public void render(float[] vpMatrix);
}
