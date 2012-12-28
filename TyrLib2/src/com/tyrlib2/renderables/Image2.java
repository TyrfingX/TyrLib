package com.tyrlib2.renderables;

import android.opengl.GLES20;

import com.tyrlib2.materials.PointSpriteMaterial;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.Renderable2;

/**
 * Class for rendering a 2D image
 * @author Sascha
 *
 */

public class Image2 extends Renderable2 {
	private String textureName;
	private float size;
	
	private Material material;
	
	public Image2(float size, String textureName) {
		this.size = size;
		this.textureName = textureName;
		
		material = new PointSpriteMaterial(textureName, size, 1);
		
		Vector3[] points = { new Vector3(0,0,0) }; 
		short[] drawOrder = { 0 };
		
		init(material, points, drawOrder);
	}
	
	
	
	public float getSize() {
		return size;
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		renderMode = GLES20.GL_POINTS;
		drawOrderLength = mesh.getDrawOrder().length;
		drawOrderBuffer = mesh.getDrawOrderBuffer();
		super.render(vpMatrix);
	}

}
