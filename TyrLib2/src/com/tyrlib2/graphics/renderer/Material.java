package com.tyrlib2.graphics.renderer;


import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Vector3;

/**
 * Defines how a mesh is rendered
 * @author Sascha
 *
 */

public class Material {
	
	public static final int DEFAULT_POSITION_SIZE = 3;
	public static final int DEFAULT_POSITION_OFFSET = 0;

	/** Which program will be used for the rendering **/
	protected Program program;
	
	/** Handle to the position data **/
	protected int positionHandle;
	
	/** Layout of the vertices **/
	protected VertexLayout vertexLayout = new VertexLayout();
	
	/** Handle to the final display matrix **/
	protected int mvpMatrixHandle;
	
	/** Name of this material **/
	protected String name;
	
	/** This material is affected by lights **/
	protected boolean lighted = false;
	
	protected String mvpParamName;
	
	protected String positionParamName;
	
	/** Whether or not this material takes animation into account **/
	protected boolean animated = false;
	
	/** Handle to the bone data **/
	protected String boneParam;
	
	/** Handle to the bones used for the vertex **/
	protected String boneIndexParam;
	
	/** Handlet to the bone weights used for the vertex **/
	protected String boneWeightParam;
	
	protected List<Param> params = new ArrayList<Param>();
	
	public Material() {

	}
	
	public void render(Mesh mesh, float[] modelMatrix) {
		setParams();
	}
	
	public void addParam(Param param) {
		params.add(param);
		param.setProgram(program.handle);
	}
	
	public void setParams() {
		for (int i = 0; i < params.size(); ++i) {
			params.get(i).set(program.handle);
		}
	}
	
	/**
	 * Sets up how this material will work with the vertex data, what will occur where, etc
	 * @param strideBytes
	 * @param positionOffset
	 * @param positionDataSize
	 */
	protected void init(int positionOffset, int positionDataSize, 
						String mvpParamName, String positionParamName) {
		this.vertexLayout.setPos(VertexLayout.POSITION, positionOffset);
		this.vertexLayout.setSize(VertexLayout.POSITION, positionDataSize);
		this.mvpParamName = mvpParamName;
		this.positionParamName = positionParamName;
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			updateHandles();
		}
	}
	
	public int getByteStride() {
		return vertexLayout.getByteStride();
	}
	
	public int getPositionOffset() {
		return getInfoOffset(VertexLayout.POSITION);
	}
	
	public int getPositionSize() {
		return getInfoSize(VertexLayout.POSITION);
	}
	
	public float[] createVertexData(Vector3[] points, short[] drawOrder) {
		float[] vertexData = new float[getByteStride() * points.length];
		
		int byteStride = getByteStride();
		int positionOffset = getPositionOffset();
		
		// Populate the vertex data
		for (int i = 0; i < points.length; ++i) {
			int pos = byteStride * i + positionOffset;
			vertexData[pos + positionOffset + 0] = points[i].x;
			vertexData[pos + positionOffset + 1] = points[i].y;
			vertexData[pos + positionOffset + 2] = points[i].z;
		}
		
		return vertexData;
		
	}
	
	public int getPositionHandle() {
		return positionHandle;
	}
	
	public int getMVPMatrixHandle() {
		return mvpMatrixHandle;
	}
	
	public Program getProgram() {
		return program;
	}
	
	public String getBoneParam() {
		return boneParam;
	}
	
	public String getBoneIndexParam() {
		return boneIndexParam;
	}
	
	public String getBoneWeightParam() {
		return boneWeightParam;
	}
	
	public void setAnimated(boolean animated) {
		this.animated = animated;
	}
	
	public Material copy() {
		return new Material();
	}
	
	public void setProgram(Program program) {
		this.program = program;
		updateHandles();
	}
	
	public void updateHandles() {
		mvpMatrixHandle = TyrGL.glGetUniformLocation(program.handle, mvpParamName);
		positionHandle = TyrGL.glGetAttribLocation(program.handle, positionParamName);
	}
	
	public int getInfoOffset(int info) {
		return vertexLayout.getPos(info);
	}
	
	public int getInfoSize(int info) {
		return vertexLayout.getSize(info);
	}
	
	public void addVertexInfo(int info, int offset, int size) {
		vertexLayout.setPos(info, offset);
		vertexLayout.setSize(info, size);
	}
	
	public VertexLayout getVertexLayout() {
		return vertexLayout;
	}
	
	public void setVertexLayout(VertexLayout layout) {
		this.vertexLayout = layout;
	}
	
	
}
