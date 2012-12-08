package com.tyrlib2.renderables;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.Mesh;
import com.tyrlib2.renderer.Renderable;

public class Triangle extends Renderable {
	
	public Triangle(Material material, Vector3[] points) {
		this.material = material;
		
		float[] vertexData = new float[material.getByteStride() * 3];
		
		// Populate the vertex data
		for (int i = 0; i < 3; ++i) {
			int pos = material.getByteStride() * i + material.getPositionOffset();
			vertexData[pos + 0] = points[i].x;
			vertexData[pos + 1] = points[i].y;
			vertexData[pos + 2] = points[i].z;
		}
		
		material.addVertexData(vertexData);
		
		mesh = new Mesh(vertexData);
		
	}

}
