package com.tyrlib2.renderables;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.Renderable;

/**
 * A simple non rotated box
 * @author Sascha
 *
 */

public class Box extends Renderable {
	
	public static final short[] DRAW_ORDER = { 0, 2, 1, 2, 3, 1, 6, 2, 4, 4, 2, 0, 4, 5, 1, 4, 1, 0, 7, 5, 3, 5, 1, 3, 6, 5, 4, 6, 7, 5, 6, 3, 2, 6, 7, 3};
	
	public Box(Material material, Vector3 min, Vector3 max) {
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
}
