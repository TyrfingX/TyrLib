package com.tyrlib2.util;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.VertexLayout;
import com.tyrlib2.math.Vector3;


/**
 * Creates a new renderable consisting of the inputed meshs and using
 * one uniform material
 * @author Sascha
 *
 */

public class MeshMixer {
	
	private List<Renderable> inputRenderables;
	private List<Vector3> inputOffsets;
	private List<Vector3> inputScales;
	private int countVertices;
	private int countIndices;
	private Material material;

	public MeshMixer(Material material) {
		inputRenderables = new ArrayList<Renderable>();
		inputOffsets = new ArrayList<Vector3>();
		inputScales = new ArrayList<Vector3>();
		this.material = material;
	}
	
	public void addRenderable(Renderable renderable, Vector3 offset, Vector3 scale) {
		inputRenderables.add(renderable);
		inputOffsets.add(offset);
		inputScales.add(scale);
		Mesh mesh = renderable.getMesh();
		countVertices += mesh.getVertexData().length / renderable.getMaterial().getByteStride();
		countIndices += mesh.getDrawOrder().length;
	}
	
	public void addRenderable(Renderable renderable) {
		addRenderable(renderable, renderable.getRelativePos(), new Vector3(1,1,1));
	}
	
	/**
	 * Create the renderable
	 * @return
	 */
	
	public Renderable create() {
		float[] vertexData = new float[countVertices * material.getByteStride()];
		short[] drawOrder = new short[countIndices];
		
		short vertexAdd = 0;
		short drawOrderAdd = 0;
		
		for (int i = 0; i < inputRenderables.size(); ++i) {
			Renderable inputRenderable = inputRenderables.get(i);
			Mesh inputMesh = inputRenderable.getMesh();
			Material inputMat = inputRenderable.getMaterial();
			
			Vector3 pos = inputOffsets.get(i);
			Vector3 scale = inputScales.get(i);
			
			float[] vertexDataMesh = inputMesh.getVertexData();
			short[] drawOrderMesh = inputMesh.getDrawOrder();
			
			short vertexStart = vertexAdd;
			
			for (int j = 0; j < vertexDataMesh.length; j += inputMat.getByteStride()) {
				for (int option : VertexLayout.LAYOUT_OPTIONS) {
					if (inputMat.getVertexLayout().hasVertexAttribute(option) && material.getVertexLayout().hasVertexAttribute(option)) {
						int infoPos = inputMat.getVertexLayout().getPos(option);
						int infoSize = inputMat.getVertexLayout().getSize(option);
						for (int k = 0; k < infoSize; ++k) {
							vertexData[vertexAdd + material.getVertexLayout().getPos(option) + k] = vertexDataMesh[j + infoPos + k];
						}					
					}
				}
				
				int posOffset = material.getPositionOffset();
				vertexData[vertexAdd + posOffset + 0] = scale.x * vertexData[vertexAdd + posOffset + 0] + pos.x;
				vertexData[vertexAdd + posOffset + 1] = scale.y * vertexData[vertexAdd + posOffset + 1] + pos.y;
				vertexData[vertexAdd + posOffset + 2] = scale.z * vertexData[vertexAdd + posOffset + 2] + pos.z;
				
				vertexData[vertexAdd + material.getVertexLayout().getPos(VertexLayout.TEXTURE_WEIGHT) + 0] = 1;
				
				vertexAdd += material.getByteStride();
			}
			
			for (int j = 0; j < drawOrderMesh.length; ++j) {
				drawOrder[drawOrderAdd++] = (short) (drawOrderMesh[j] + vertexStart / material.getByteStride());
			}
		}
		
		Mesh mesh = new Mesh(vertexData, drawOrder, countVertices);
		Renderable renderable = new Renderable(mesh, material);
		
		return renderable;
	}
	
	
}
