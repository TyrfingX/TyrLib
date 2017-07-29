package com.tyrfing.games.tyrlib3.view.graphics;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.graphics.VertexLayout;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Quad;

public class MeshBuilder {
	
	private final VertexLayout layout;
	private final int numVertex;
	private final float[] vertexData;
	private final short[] edgeData;
	
	private int offset;
	private int edge;
	
	public static final float[] DEFAULT_UV = {
		0, 0,
		1, 0,
		0, 1,
		1, 1
	};
	
	public MeshBuilder(VertexLayout layout, int numVertex, int numEdges) {
		this.layout = layout;
		this.numVertex = numVertex;
	
		vertexData = new float[numVertex * layout.getByteStride()];
		edgeData = new short[numEdges];
	}
	
	public void addQuad(Vector3F[] points, Color color) {
		addQuad(points, color, 1, 1);
	}
	
	public void addQuad(Vector3F[] points, Color color, float repeatX, float repeatY) {
		addQuad(points, color, 1, 1, null);
	}
	
	/**
	 * Adds a quad with the provided vertices
	 * @param points
	 */
	public void addQuad(Vector3F[] points, Color color, float repeatX, float repeatY, float[][] textureWeights) {
		
		if (layout.hasVertexAttribute(VertexLayout.POSITION)) {
			int posPos = layout.getPos(VertexLayout.POSITION);
			for (int i = 0; i < points.length; ++i) {
				vertexData[offset + posPos + 0 + i * layout.getByteStride()] = points[i].x;
				vertexData[offset + posPos + 1 + i * layout.getByteStride()] = points[i].y;
				vertexData[offset + posPos + 2 + i * layout.getByteStride()] = points[i].z;
			}		
		}
		
		if (layout.hasVertexAttribute(VertexLayout.NORMAL)) {
			Vector3F dir1 = points[0].vectorTo(points[1]);
			dir1.normalize();
			Vector3F dir2 = points[0].vectorTo(points[3]);
			dir2.normalize();
			Vector3F normal = dir1.cross(dir2);
			normal.normalize();
			
			int normalPos = layout.getPos(VertexLayout.NORMAL);
			
			for (int i = 0; i < points.length; ++i) {
				vertexData[offset + normalPos + 0 + i * layout.getByteStride()] = normal.x;
				vertexData[offset + normalPos + 1 + i * layout.getByteStride()] = normal.y;
				vertexData[offset + normalPos + 2 + i * layout.getByteStride()] = normal.z;
			}
		}
		
		if (layout.hasVertexAttribute(VertexLayout.UV)) {
			int uvPos = layout.getPos(VertexLayout.UV);
			
			for (int i = 0; i < points.length; ++i) {
				vertexData[offset + uvPos + 0 + i * layout.getByteStride()] = DEFAULT_UV[i*2] * repeatX;
				vertexData[offset + uvPos + 1 + i * layout.getByteStride()] = DEFAULT_UV[i*2+1] * repeatY;
			}
		}
		
		if (layout.hasVertexAttribute(VertexLayout.TEXTURE_WEIGHT)) {
			int texWeight = layout.getPos(VertexLayout.TEXTURE_WEIGHT);
			
			for (int i = 0; i < points.length; ++i) {
				vertexData[offset + texWeight + 0 + i * layout.getByteStride()] = textureWeights[i][0];
				vertexData[offset + texWeight + 1 + i * layout.getByteStride()] = textureWeights[i][1];
				vertexData[offset + texWeight + 2 + i * layout.getByteStride()] = textureWeights[i][2];
				vertexData[offset + texWeight + 3 + i * layout.getByteStride()] = textureWeights[i][3];
			}
		}
		
		if (layout.hasVertexAttribute(VertexLayout.COLOR)) {
			int colorPos = layout.getPos(VertexLayout.COLOR);
			
			for (int i = 0; i < points.length; ++i) {
				vertexData[offset + colorPos + 0 + i * layout.getByteStride()] = color.r;
				vertexData[offset + colorPos + 1 + i * layout.getByteStride()] = color.g;
				vertexData[offset + colorPos + 2 + i * layout.getByteStride()] = color.b;
				vertexData[offset + colorPos + 3 + i * layout.getByteStride()] = color.a;
			}			
		}
		
		for (int i = 0; i < Quad.DRAW_ORDER_QUAD.length; ++i) {
			edgeData[edge++] = (short) (Quad.DRAW_ORDER_QUAD[i] + offset / layout.getByteStride());
		}
		
		offset += layout.getByteStride() * 4;
	}
	
	public Mesh build() {
		return new Mesh(vertexData, edgeData, numVertex, true);
	}
}
