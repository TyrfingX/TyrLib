package com.tyrfing.games.tyrlib3.view.graphics.renderables;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.materials.ColoredMaterial;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;

public class Line3 extends Renderable {
	private Vector3F startPoint;
	private Vector3F endPoint;
	private int thickness;
	
	public Line3(Vector3F startPoint, Vector3F endPoint, Color color, int thickness) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.thickness = thickness;
		
		Vector3F[] points = { 	startPoint,
								endPoint };
		
		Material material = new ColoredMaterial(new Color[] { color });
		
		init(material, points, new short[] { 0, 1});
		
		renderMode = TyrGL.GL_LINES;
	}
	
	public Vector3F getStartPoint() {
		return startPoint;
	}
	
	public Vector3F getEndPoint() {
		return endPoint;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		TyrGL.glLineWidth(thickness);
		super.render(vpMatrix);
	}
}
