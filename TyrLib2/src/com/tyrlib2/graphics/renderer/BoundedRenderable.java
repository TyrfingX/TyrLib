package com.tyrlib2.graphics.renderer;

import android.opengl.Matrix;

import com.tyrlib2.graphics.renderables.BoundingBox;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
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
	private boolean boundingBoxVisible;
	private BoundingBox boundingBoxRenderable;
	
	protected abstract AABB createUntransformedBoundingBox();
	
	protected void createBoundingBoxRenderable() {
		boundingBoxRenderable = new BoundingBox(boundingBox);
		SceneManager.getInstance().getRenderer().addRenderable(boundingBoxRenderable);
		parent.attachSceneObject(boundingBoxRenderable);
	}
	
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

	@Override
	public void setBoundingBoxVisible(boolean visible) {		
		boundingBoxVisible = visible;
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		calcBoundingBox();
	}
	

	@Override
	public SceneNode detach() {
		calcBoundingBox();
		return super.detach();
	}
	
	protected void calcBoundingBox() {
		
		if (untransformedBoundingBox == null) {
			untransformedBoundingBox = createUntransformedBoundingBox();
		}
		
		if (parent != null) {
		
			float[] transform = parent.getModelMatrix();
			
			Vector3 min = untransformedBoundingBox.min;
			Vector3 max = untransformedBoundingBox.max;
			
			float[] points = {	
					min.x, min.y, min.z,1,
					max.x, min.y, min.z,1,
					min.x, max.y, min.z,1,
					max.x, max.y, min.z,1,
					min.x, min.y, max.z,1,
					max.x, min.y, max.z,1,
					min.x, max.y, max.z,1,
					max.x, max.y, max.z,1,
			};
			
			// First get the untransformed bounding box and transform it by the scene node transform
			
			for (int i = 0; i < 8; ++i) {
				Matrix.multiplyMV(points, i*4, transform, 0, points, i*4);
			}
			
			// Make the bounding box axis aligned
			
			boundingBox = AABB.createFromPoints(points, 4);
			
			
			min = boundingBox.min;
			max = boundingBox.max;
			
			if (boundingBoxVisible) {
				if (boundingBoxRenderable == null) {
					createBoundingBoxRenderable();
				} else {
					
					// Get the new vertex positions of the bounding box
					
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
					
					Mesh mesh = boundingBoxRenderable.getMesh();
					Material mat = boundingBoxRenderable.getMaterial();
					for (int i = 0; i < 8; ++i) {
						mesh.setVertexInfo(i*mat.getByteStride() + 0, pointsTransformed[i*4 + 0]);
						mesh.setVertexInfo(i*mat.getByteStride() + 1, pointsTransformed[i*4 + 1]);
						mesh.setVertexInfo(i*mat.getByteStride() + 2, pointsTransformed[i*4 + 2]);
					}
					
					boundingBoxRenderable.setBoundingBox(boundingBox);
				}
			}
		
		}
			
	}
}
