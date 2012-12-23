package com.tyrlib2.renderables;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.Renderable;

public class Triangle extends Renderable {
	
	public static final short[] DRAW_ORDER = { 2, 1, 0 };
	
	public Triangle(Material material, Vector3[] points) {
		super(material, points, DRAW_ORDER);
	}
}
