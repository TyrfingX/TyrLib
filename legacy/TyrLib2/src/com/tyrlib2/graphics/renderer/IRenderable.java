package com.tyrlib2.graphics.renderer;


/**
 * Basic interface for objects providing a render capability
 * @author Sascha
 *
 */

public interface IRenderable {
	
	/** Renders this object the matrix containing projection and view will be passed **/
	public void render(float[] vpMatrix);
	public void renderShadow(float[] vpMatrix);
	public void setInsertionID(int id);
	public int getInsertionID();
	public void destroy();
}
