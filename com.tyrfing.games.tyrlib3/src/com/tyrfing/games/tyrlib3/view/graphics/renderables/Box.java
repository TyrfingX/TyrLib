package com.tyrfing.games.tyrlib3.view.graphics.renderables;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;

/**
 * A simple non rotated box
 * @author Sascha
 *
 */

public class Box extends Renderable {
	
	public static final short[] DRAW_ORDER = { 6, 7, 3, 6, 3, 2, 5, 7, 6, 4, 5, 6, 5, 1, 3, 7, 5, 3, 0, 1, 4, 1, 5, 4, 4, 2, 0, 6, 2, 4, 2, 3, 1, 0, 2, 1};

	private Vector3F min;
	private Vector3F max;
	
	public Box() {
		
	}
	
	public Box(Material material, Vector3F min, Vector3F max) {
		createBox(material, min, max);
	}
	
	protected void createBox(Material material, Vector3F min, Vector3F max) {
		Vector3F points[] = {	
				new Vector3F(min),
				new Vector3F(max.x, min.y, min.z),
				new Vector3F(min.x, max.y, min.z),
				new Vector3F(max.x, max.y, min.z),
				new Vector3F(min.x, min.y, max.z),
				new Vector3F(max.x, min.y, max.z),
				new Vector3F(min.x, max.y, max.z),
				new Vector3F(max),

		};
		init(material, points, DRAW_ORDER);
	}

	public Vector3F getMin() {
		return min;
	}

	public Vector3F getMax() {
		return max;
	}
	
	
	
	
}
