package com.tyrlib2.renderer;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

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
	
	public Material() {

	}
	
	public void render(FloatBuffer vertexBuffer) {}
	
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
		
		mvpMatrixHandle = GLES20.glGetUniformLocation(program.handle, mvpParamName);
		positionHandle = GLES20.glGetAttribLocation(program.handle, positionParamName);
	}
	
	public int getByteStride() {
		return strideBytes;
	}
	
	public int getPositionOffset() {
		return positionOffest;
	}
	
	public void addVertexData(float[] vertexData) {}
	
}
