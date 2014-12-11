package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.animation.Skeleton;
import com.tyrlib2.graphics.materials.OutlineMaterial;
import com.tyrlib2.graphics.renderer.BoundedRenderable;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;

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

}
