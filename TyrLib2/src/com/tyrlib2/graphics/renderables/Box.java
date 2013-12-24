package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.math.Vector3;

/**
 * A simple non rotated box
 * @author Sascha
 *
 */

public class Box extends Renderable {
	
	public static final short[] DRAW_ORDER = { 6, 7, 3, 6, 3, 2, 5, 7, 6, 4, 5, 6, 5, 1, 3, 7, 5, 3, 0, 1, 4, 1, 5, 4, 4, 2, 0, 6, 2, 4, 2, 3, 1, 0, 2, 1};

	private Vector3 min;
	private Vector3 max;
	
	public Box() {
		
	}
	
	public Box(Material material, Vector3 min, Vector3 max) {
		createBox(material, min, max);
	}
	
	protected void createBox(Material material, Vector3 min, Vector3 max) {
		Vector3 points[] = {	
				new Vector3(min),
				new Vector3(max.x, min.y, min.z),
				new Vector3(min.x, max.y, min.z),
				new Vector3(max.x, max.y, min.z),
				new Vector3(min.x, min.y, max.z),
				new Vector3(max.x, min.y, max.z),
				new Vector3(min.x, max.y, max.z),
				new Vector3(max),

		};
		init(material, points, DRAW_ORDER);
	}

	public Vector3 getMin() {
		return min;
	}

	public Vector3 getMax() {
		return max;
	}
	
	
	
	
}
