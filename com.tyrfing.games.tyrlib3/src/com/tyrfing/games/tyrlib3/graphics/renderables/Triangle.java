package com.tyrfing.games.tyrlib3.graphics.renderables;

import com.tyrfing.games.tyrlib3.graphics.renderer.Material;
import com.tyrfing.games.tyrlib3.graphics.renderer.Renderable;
import com.tyrfing.games.tyrlib3.math.Vector3F;

public class Triangle extends Renderable {
	
	public static final short[] DRAW_ORDER = { 2, 1, 0 };
	
	public Triangle(Material material, Vector3F[] points) {
		super(material, points, DRAW_ORDER);
	}
}
