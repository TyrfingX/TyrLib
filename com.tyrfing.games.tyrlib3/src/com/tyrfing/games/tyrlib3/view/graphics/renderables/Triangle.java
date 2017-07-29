package com.tyrfing.games.tyrlib3.view.graphics.renderables;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;

public class Triangle extends Renderable {
	
	public static final short[] DRAW_ORDER = { 2, 1, 0 };
	
	public Triangle(Material material, Vector3F[] points) {
		super(material, points, DRAW_ORDER);
	}
}
