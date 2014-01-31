package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.materials.ColoredMaterial;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector3;
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
		
		ColoredMaterial mat = new ColoredMaterial(new Color[] { Color.getRandomColor(0.2f) });
		mat.setAlpha(0.3f);
		this.material = mat;
		
		Matrix.setIdentityM(unitMatrix, 0);
		
		
		init(material, points, DRAW_ORDER);
	}
	
	@Override
	public void render(float[] vpMatrix) {
		TyrGL.glDepthMask(false);
		TyrGL.glDisable(TyrGL.GL_CULL_FACE);
		if (modelMatrix == null) {
			modelMatrix = unitMatrix;
		}
		super.render(vpMatrix);
		TyrGL.glEnable(TyrGL.GL_CULL_FACE);
		TyrGL.glDepthMask(true);
	}
	
	@Override
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
	public void setBoundingBox(AABB boundingBox) {
		this.boundingBox = boundingBox;
		
		// Get the new vertex positions of the bounding box
		
		Vector3 min = boundingBox.min;
		Vector3 max = boundingBox.max;
		
		float[] pointsTransformed = {	
				min.x, min.y, min.z,1,
				max.x, min.y, min.z,1,
				min.x, max.y, min.z,1,
				max.x, max.y, min.z,1,
				min.x, min.y, max.z,1,
				max.x, min.y, max.z,1,
				min.x, max.y, max.z,1,
				max.x, max.y, max.z,1,
		};
		
		Mesh mesh = this.getMesh();
		Material mat = this.getMaterial();
		for (int i = 0; i < 8; ++i) {
			mesh.setVertexInfo(i*mat.getByteStride() + 0, pointsTransformed[i*4 + 0]);
			mesh.setVertexInfo(i*mat.getByteStride() + 1, pointsTransformed[i*4 + 1]);
			mesh.setVertexInfo(i*mat.getByteStride() + 2, pointsTransformed[i*4 + 2]);
		}
	}
}
