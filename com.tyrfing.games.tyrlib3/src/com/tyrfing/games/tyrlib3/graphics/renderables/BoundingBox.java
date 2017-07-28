package com.tyrfing.games.tyrlib3.graphics.renderables;

import com.tyrfing.games.tyrlib3.graphics.materials.ColoredMaterial;
import com.tyrfing.games.tyrlib3.graphics.renderer.Material;
import com.tyrfing.games.tyrlib3.graphics.renderer.Mesh;
import com.tyrfing.games.tyrlib3.graphics.renderer.Renderable;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.math.AABB;
import com.tyrfing.games.tyrlib3.math.Matrix;
import com.tyrfing.games.tyrlib3.math.Vector3F;
import com.tyrfing.games.tyrlib3.util.Color;

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
		
		Vector3F min = aabb.min;
		Vector3F max = aabb.max;
		
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
		
		Vector3F min = boundingBox.min;
		Vector3F max = boundingBox.max;
		
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
