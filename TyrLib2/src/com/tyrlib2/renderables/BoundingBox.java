package com.tyrlib2.renderables;

import android.opengl.Matrix;

import com.tyrlib2.materials.ColoredMaterial;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Renderable;
import com.tyrlib2.util.Color;

/**
 * Class for rendering AABB.
 * @author Sascha
 *
 */

public class BoundingBox extends Renderable {
	
	public static final short[] DRAW_ORDER = { 6, 7, 3, 6, 3, 2, 5, 7, 6, 4, 5, 6, 5, 1, 3, 7, 5, 3, 0, 1, 4, 1, 5, 4, 4, 2, 0, 6, 2, 4, 2, 3, 1, 0, 2, 1};
	private static float[] unitMatrix = new float[16];
	protected AABB boundingBox;
	
	public BoundingBox(AABB aabb) {
		
		boundingBox = aabb;
		
		Vector3 min = aabb.min;
		Vector3 max = aabb.max;
		
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
		
		ColoredMaterial mat = new ColoredMaterial(new Color[] { Color.RED });
		mat.setAlpha(0.2f);
		this.material = mat;
		
		Matrix.setIdentityM(unitMatrix, 0);
		
		
		init(material, points, DRAW_ORDER);
	}
	
	@Override
	public void render(float[] vpMatrix) {
		modelMatrix = unitMatrix;
		super.render(vpMatrix);
	}
	
	@Override
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
	public void setBoundingBox(AABB boundingBox) {
		this.boundingBox = boundingBox;
	}
}
