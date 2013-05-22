package com.tyrlib2.util;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.Renderable2;
import com.tyrlib2.math.Vector3;


/**
 * Creates a new renderable consisting of the inputed meshs and using
 * one uniform material
 * @author Sascha
 *
 */

public class MeshMixer {
	
	private List<Renderable> inputRenderables;
	private int countVertices;
	private int countIndices;
	private Material material;

	public MeshMixer(Material material) {
		inputRenderables = new ArrayList<Renderable>();
		this.material = material;
	}
	
	
	public void addRenderable(Renderable renderable) {
		inputRenderables.add(renderable);
		Mesh mesh = renderable.getMesh();
		countVertices += mesh.getVertexData().length;
		countIndices += mesh.getDrawOrder().length;
	}
	
	/**
	 * Create the renderable
	 * @return
	 */
	
	public Renderable create() {
		float[] vertexData = new float[countVertices];
		short[] drawOrder = new short[countIndices];
		
		int vertexStart = 0;
		int drawOrderStart = 0;
		
		for (int i = 0; i < inputRenderables.size(); ++i) {
			Renderable inputRenderable = inputRenderables.get(i);
			Mesh inputMesh = inputRenderable.getMesh();
			
			Vector3 pos = inputRenderable.getParent().getCachedAbsolutePos();
			
			int vertexAdd = 0;
			int drawOrderAdd = 0;
			
			float[] vertexDataMesh = inputMesh.getVertexData();
			short[] drawOrderMesh = inputMesh.getDrawOrder();
			for (int j = 0; j < vertexDataMesh.length; ++j) {
				vertexData[vertexStart + j] = vertexDataMesh[j];
				
				if (j % material.getByteStride() == material.getPositionOffset()) {
					vertexData[vertexStart + j] += pos.x;
				} else if (j % material.getByteStride() == material.getPositionOffset() + 1) {
					vertexData[vertexStart + j] += pos.y;
				} else if (j % material.getByteStride() == material.getPositionOffset() + 2) {
					vertexData[vertexStart + j] += pos.z;
				}
				
				vertexAdd++;
			}
			
			for (int j = 0; j < drawOrderMesh.length; ++j) {
				drawOrder[drawOrderStart + j] = drawOrderMesh[j];
				drawOrderAdd++;
			}
			
			vertexStart += vertexAdd;
			drawOrderStart += drawOrderAdd;
		}
		
		Mesh mesh = new Mesh(vertexData, drawOrder, countVertices);
		Renderable renderable = new Renderable(mesh, material);
		
		return renderable;
	}
	
	
}
