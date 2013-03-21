package com.tyrlib2.graphics.renderer;

import android.opengl.Matrix;

import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

/**
 * A renderable object which can be placed within the scene
 * and supports bounding boxes
 * @author Sascha
 *
 */

public abstract class BoundedRenderable extends BoundedSceneObject implements IRenderable {
	
	private AABB boundingBox;
	private AABB untransformedBoundingBox;
	
	private static float[] points = new float[4*8];
	
	protected abstract AABB createUntransformedBoundingBox();
	
	@Override
	public AABB getBoundingBox() {		
		if (boundingBox == null) {
			calcBoundingBox();
		}
		return boundingBox;
	}
	
	public AABB getUntransformedBoundingBox() {
		if (untransformedBoundingBox == null) {
			calcBoundingBox();
		}
		return untransformedBoundingBox;
	}
	
	protected void calcBoundingBox() {
		
		if (untransformedBoundingBox == null) {
			untransformedBoundingBox = createUntransformedBoundingBox();
		}
		
		if (parent != null) {
		
			float[] transform = parent.getModelMatrix();
			
			Vector3 min = untransformedBoundingBox.min;
			Vector3 max = untransformedBoundingBox.max;
			
			points[0] = min.x;
			points[1] = min.y;
			points[2] = min.z;
			points[3] = 1;
			
			points[4] = max.x;
			points[5] = min.y;
			points[6] = min.z;
			points[7] = 1;
			
			points[8] = min.x;
			points[9] = max.y;
			points[10] = min.z;
			points[11] = 1;

			points[12] = min.x;
			points[13] = min.y;
			points[14] = max.z;
			points[15] = 1;
			
			points[16] = max.x;
			points[17] = min.y;
			points[18] = max.z;
			points[19] = 1;
			
			points[20] = max.x;
			points[21] = max.y;
			points[22] = min.z;
			points[23] = 1;
			
			points[24] = min.x;
			points[25] = max.y;
			points[26] = max.z;
			points[27] = 1;
			
			points[28] = max.x;
			points[29] = max.y;
			points[30] = max.z;
			points[31] = 1;
			
			// First get the untransformed bounding box and transform it by the scene node transform
			
			for (int i = 0; i < 8; ++i) {
				Matrix.multiplyMV(points, i*4, transform, 0, points, i*4);
			}
			
			// Make the bounding box axis aligned
			if (boundingBox == null) {
				boundingBox = AABB.createFromPoints(points, 4);
			} else {
				boundingBox.updateWithPoints(points, 4);
			}
		
		}
			
	}
}
