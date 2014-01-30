package com.tyrlib2.util;

import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.scene.BoundedSceneObject;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

public class BoundingBoxTree extends BoundedSceneObject {
	
	private BoundingBoxTreeNode root;
	
	public BoundingBoxTree(Mesh mesh) {
		float[] vertexData = mesh.getVertexData();
		int byteStride = vertexData.length / mesh.getVertexCount();
		
		Vector3[] points = new Vector3[mesh.getVertexCount()];
		for (int i = 0; i < mesh.getVertexCount(); ++i) {
			points[i] = new Vector3(vertexData[i * byteStride], vertexData[i * byteStride + 1], vertexData[i * byteStride + 2]); 
		}
		
		root = new BoundingBoxTreeNode(points);
	}
	
	public boolean query(IBoundingBoxTreeQuery query) {
		return root.query(query);
	}

	@Override
	public AABB getBoundingBox() {
		return root.getBoundingBox();
	}
	
	@Override
	public void setBoundingBoxVisible(boolean visible) {
		
		if (!this.isBoundingBoxVisible() && visible) {
			root.attachTo(getParent());
		} else if (this.isBoundingBoxVisible() && !visible) {
			root.detach();
		}
		
		super.setBoundingBoxVisible(visible);
		root.setBoundingBoxVisible(visible);
	}
}
