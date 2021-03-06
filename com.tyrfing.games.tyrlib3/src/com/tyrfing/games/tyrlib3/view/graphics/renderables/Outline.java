package com.tyrfing.games.tyrlib3.view.graphics.renderables;

import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.math.AABB;
import com.tyrfing.games.tyrlib3.view.graphics.Mesh;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.animation.Skeleton;
import com.tyrfing.games.tyrlib3.view.graphics.materials.OutlineMaterial;

public class Outline extends BoundedRenderable {

	private Renderable outline;
	private Skeleton skeleton;
	private float size;
	private int insertionID;
	
	public Outline(Mesh mesh, OutlineMaterial material) {
		outline = new Renderable(mesh, material);
	}
	
	public Outline(Mesh mesh, Skeleton skeleton, OutlineMaterial material) {
		outline = new Renderable(mesh, material);
		this.skeleton = skeleton;
		this.size = 0.1f;
	}
	
	public Outline(Mesh mesh, Skeleton skeleton, OutlineMaterial material, float size) {
		outline = new Renderable(mesh, material);
		this.skeleton = skeleton;
		this.size = size;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		
		if (skeleton != null) {
			float[] boneData = skeleton.getBoneData();
			int bones = skeleton.getCountBones();
			Skeleton.passData(boneData, bones, size, outline.getMaterial(), outline.getMesh());
		}
		
		TyrGL.glCullFace(TyrGL.GL_FRONT);
    	outline.render(vpMatrix);
    	TyrGL.glCullFace(TyrGL.GL_BACK);
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

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}
	
	public OutlineMaterial getMaterial() {
		return (OutlineMaterial) outline.getMaterial();
	}
	
	public void setMaterial(OutlineMaterial mat) {
		outline.setMaterial(mat);
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}

	@Override
	public void destroy() {
		if (outline != null) {
			outline.destroy();
		}
	}

}
