package com.tyrlib2.graphics.renderables;

import android.opengl.GLES20;

import com.tyrlib2.graphics.materials.ColoredMaterial;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class Line3 extends Renderable {
	private Vector3 startPoint;
	private Vector3 endPoint;
	private int thickness;
	
	public Line3(Vector3 startPoint, Vector3 endPoint, Color color, int thickness) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.thickness = thickness;
		
		Vector3[] points = { 	startPoint,
								endPoint };
		
		Material material = new ColoredMaterial(new Color[] { color });
		
		init(material, points, new short[] { 0, 1});
		
		renderMode = GLES20.GL_LINES;
	}
	
	public Vector3 getStartPoint() {
		return startPoint;
	}
	
	public Vector3 getEndPoint() {
		return endPoint;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		GLES20.glLineWidth(thickness);
		super.render(vpMatrix);
	}
}
