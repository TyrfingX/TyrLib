package com.tyrfing.games.tyrlib3.util;

import com.tyrfing.games.tyrlib3.graphics.renderer.Mesh;
import com.tyrfing.games.tyrlib3.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.math.AABB;
import com.tyrfing.games.tyrlib3.math.Vector3F;

public class BoundingBoxTree extends BoundedSceneObject {
	
	private BoundingBoxTreeNode root;
	
	public BoundingBoxTree(Mesh mesh) {
		float[] vertexData = mesh.getVertexData();
		int byteStride = vertexData.length / mesh.getVertexCount();
		
		Vector3F[] points = new Vector3F[mesh.getVertexCount()];
		for (int i = 0; i < mesh.getVertexCount(); ++i) {
			points[i] = new Vector3F(vertexData[i * byteStride], vertexData[i * byteStride + 1], vertexData[i * byteStride + 2]); 
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
