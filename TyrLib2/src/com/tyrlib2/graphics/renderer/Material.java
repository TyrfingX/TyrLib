package com.tyrlib2.graphics.renderer;

import java.nio.FloatBuffer;

import com.tyrlib2.math.Vector3;

/**
 * Defines how a mesh is rendered
 * @author Sascha
 *
 */

public class Material {
	
	/** Which program will be used for the rendering **/
	protected Program program;
	
	/** How many bytes will be needed per vertex **/
	protected int strideBytes;
	
	/** Where does the position data begin **/
	protected int positionOffest;
	
	/** how much data does one position encompass **/
	protected int positionDataSize;
	
	/** Handle to the position data **/
	protected int positionHandle;
	
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
	
	public Material() {

	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {

	}
	
	/**
	 * Sets up how this material will work with the vertex data, what will occur where, etc
	 * @param strideBytes
	 * @param positionOffset
	 * @param positionDataSize
	 */
	protected void init(int strideBytes, int positionOffset, int positionDataSize, 
						String mvpParamName, String positionParamName) {
		this.strideBytes = strideBytes;
		this.positionOffest = positionOffset;
		this.positionDataSize = positionDataSize;
		this.mvpParamName = mvpParamName;
		this.positionParamName = positionParamName;
		
		mvpMatrixHandle = TyrGL.glGetUniformLocation(program.handle, mvpParamName);
		positionHandle = TyrGL.glGetAttribLocation(program.handle, positionParamName);
		

	}
	
	public int getByteStride() {
		return strideBytes;
	}
	
	public int getPositionOffset() {
		return positionOffest;
	}
	
	public float[] createVertexData(Vector3[] points, short[] drawOrder) {
		float[] vertexData = new float[getByteStride() * points.length];
		
		// Populate the vertex data
		for (int i = 0; i < points.length; ++i) {
			int pos = strideBytes * i + getPositionOffset();
			vertexData[pos + positionOffest + 0] = points[i].x;
			vertexData[pos + positionOffest + 1] = points[i].y;
			vertexData[pos + positionOffest + 2] = points[i].z;
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
	
}
