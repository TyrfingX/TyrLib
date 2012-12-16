package com.tyrlib2.renderables;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.Mesh;
import com.tyrlib2.renderer.Renderable;


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
			
			float[] vertexDataMesh = inputMesh.getVertexData();
			short[] drawOrderMesh = inputMesh.getDrawOrder();
			for (int j = 0; j < vertexDataMesh.length; ++j) {
				
				// TODO: Transform the vertex data using the parent scene node of the renderable
				
				
				
				vertexData[vertexStart + j] = vertexDataMesh[j];
			}
			
			for (int j = 0; j < drawOrderMesh.length; ++j) {
				drawOrder[drawOrderStart + j] = drawOrderMesh[j];
			}
			
			vertexStart += vertexDataMesh.length;
			drawOrderStart += drawOrderMesh.length;
		}
		
		Mesh mesh = new Mesh(vertexData, drawOrder);
		Renderable renderable = new Renderable(mesh, material);
		
		return renderable;
	}
}
