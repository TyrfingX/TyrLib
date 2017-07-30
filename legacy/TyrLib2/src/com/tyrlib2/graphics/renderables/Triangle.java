package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.math.Vector3;

public class Triangle extends Renderable {
	
	public static final short[] DRAW_ORDER = { 2, 1, 0 };
	
	public Triangle(Material material, Vector3[] points) {
		super(material, points, DRAW_ORDER);
	}
}
