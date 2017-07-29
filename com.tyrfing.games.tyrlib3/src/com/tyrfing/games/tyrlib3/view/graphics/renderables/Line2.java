package com.tyrfing.games.tyrlib3.view.graphics.renderables;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.materials.ColoredMaterial;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;

/**
 * Takes care of rendering 2D lines
 * @author Sascha
 *
 */

public class Line2 extends Renderable2 {
	private Vector2F startPoint;
	private Vector2F endPoint;
	private int thickness;
	
	public Line2(Vector2F startPoint, Vector2F endPoint, Color color, int thickness) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.thickness = thickness;
		
		Vector3F[] points = { 	new Vector3F(startPoint.x, startPoint.y, 0),
								new Vector3F(endPoint.x, endPoint.y, 0) };
		
		Material material = new ColoredMaterial(new Color[] { color });
		
		init(material, points, new short[] { 0, 1});
	}
	
	public Vector2F getStartPoint() {
		return startPoint;
	}
	
	public Vector2F getEndPoint() {
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
