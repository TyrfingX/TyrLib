package com.tyrlib2.graphics.renderer;

import gnu.trove.map.hash.TIntIntHashMap;

public class VertexLayout {
	
	public static final int POSITION = 0;
	public static final int NORMAL = 1;
	public static final int UV = 2;
	public static final int COLOR = 3;
	public static final int TEXTURE_WEIGHT = 4;
	
	public static final int[] LAYOUT_OPTIONS = { POSITION, NORMAL, UV, COLOR, TEXTURE_WEIGHT };
	
	private TIntIntHashMap vertexPosLayout;
	private TIntIntHashMap vertexSizeLayout;
	
	private int byteStride;
	
	public VertexLayout() {
		vertexPosLayout = new TIntIntHashMap();
		vertexSizeLayout = new TIntIntHashMap();
	}
	
	public VertexLayout(VertexLayout layout) {
		this.vertexPosLayout = new TIntIntHashMap(layout.vertexPosLayout);
		this.vertexSizeLayout = new TIntIntHashMap(layout.vertexSizeLayout);
		this.byteStride = layout.byteStride;
	}
	
	public VertexLayout(int[] posLayout, int[] posLayoutValues, int[] sizeLayout, int[] sizeLayoutValues) {
		this();
		
		if (posLayout.length != sizeLayout.length) {
			throw new IllegalArgumentException("VertexLayout::VertexLayout posLayout and sizeLayout must be of same length!");
		}
		
		for (int i = 0; i < posLayout.length; ++i) {
			setPos(posLayout[i], posLayoutValues[i]);
			setSize(sizeLayout[i], sizeLayoutValues[i]);
		}
	}
	
	public boolean hasVertexAttribute(int vertexInfo) {
		return vertexPosLayout.contains(vertexInfo);
	}
	
	public int getPos(int vertexInfo) {
		return vertexPosLayout.get(vertexInfo);
	}
	
	public void setPos(int info, int pos) {
		vertexPosLayout.put(info, pos);
	}
	public int getSize(int vertexInfo) {
		return vertexSizeLayout.get(vertexInfo);
	}
	
	public void setSize(int info, int size) {
		if (vertexSizeLayout.containsKey(info)) {
			byteStride -= vertexSizeLayout.get(info);
		} 
		
		byteStride += size;
		vertexSizeLayout.put(info, size);
	}
	
	public int getByteStride() {
		return byteStride;
	}

	public VertexLayout copy() {
		return new VertexLayout(this);
	}

	public void setBytestride(int byteStride) {
		this.byteStride = byteStride;
	}

	public void remove(int info) {
		vertexPosLayout.remove(info);
		vertexSizeLayout.remove(info);
	}
}
