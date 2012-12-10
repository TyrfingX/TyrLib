package com.tyrlib2.renderables;

import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.Renderable;

/**
 * A non rotated rectangle
 * @author Sascha
 *
 */

public class Quad extends Renderable {
	
	public static final short[] DRAW_ORDER_QUAD = { 0, 2, 1, 2, 3, 1};
	
	public Quad(Material material, Vector2 min, Vector2 max) {
		
		Vector3[] points = { 	new Vector3(min.x, min.y, 0),
								new Vector3(max.x, min.y, 0),
								new Vector3(min.x, max.y, 0),
								new Vector3(max.x, max.y, 0) };
		init(material, points, DRAW_ORDER_QUAD);
	}
}
