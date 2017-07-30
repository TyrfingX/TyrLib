package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.materials.ColoredMaterial;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Renderable2;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

/**
 * Takes care of rendering 2D lines
 * @author Sascha
 *
 */

public class Line2 extends Renderable2 {
	private Vector2 startPoint;
	private Vector2 endPoint;
	private int thickness;
	
	public Line2(Vector2 startPoint, Vector2 endPoint, Color color, int thickness) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.thickness = thickness;
		
		Vector3[] points = { 	new Vector3(startPoint.x, startPoint.y, 0),
								new Vector3(endPoint.x, endPoint.y, 0) };
		
		Material material = new ColoredMaterial(new Color[] { color });
		
		init(material, points, new short[] { 0, 1});
	}
	
	public Vector2 getStartPoint() {
		return startPoint;
	}
	
	public Vector2 getEndPoint() {
		return endPoint;
	}
	
	@Override
	public void render(float[] vpMatrix) {

		renderMode = TyrGL.GL_LINES;
		TyrGL.glLineWidth(thickness);
		drawOrderLength = mesh.getDrawOrder().length;
		drawOrderBuffer = mesh.getDrawOrderBuffer();
		super.render(vpMatrix);
	}
}
