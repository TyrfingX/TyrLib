package com.tyrlib2.graphics.renderables;

import android.opengl.GLES20;

import com.tyrlib2.graphics.materials.OutlineMaterial;
import com.tyrlib2.graphics.renderer.BoundedRenderable;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;

public class Outline extends BoundedRenderable {

	private Renderable outline;
	
	public Outline(Mesh mesh, OutlineMaterial material) {
		outline = new Renderable(mesh, material);
	}
	
	@Override
	public void render(float[] vpMatrix) {
    	GLES20.glCullFace(GLES20.GL_FRONT);
    	outline.render(vpMatrix);
    	GLES20.glCullFace(GLES20.GL_BACK);
	}

	@Override
	protected AABB createUntransformedBoundingBox() {
		return outline.getUntransformedBoundingBox();
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		outline.attachTo(node);
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		outline.detach();
		return super.detach();	
	}

}
