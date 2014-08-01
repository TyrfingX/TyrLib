package com.tyrlib2.graphics.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

import com.tyrlib2.math.AABB;

/**
 * This class represents a mesh, containing the neccessary vertex data, etc.
 * @author Sascha
 *
 */

public class Mesh {
	protected FloatBuffer vertexBuffer;
	protected ShortBuffer drawListBuffer;
	protected FloatBuffer boneBuffer;
	
	protected float[] vertexData;
	protected short[] drawOrder;
	protected float[] boneData;
	
	protected boolean usesVBO;
	
	public static final int MAX_BONES_PER_VERTEX = 4;
	public static final int BONE_BYTE_STRIDE = MAX_BONES_PER_VERTEX * 2;
	public static final int MAX_BONES_PER_MESH = 55;
	public static final int BONE_INDEX_OFFSET = 0;
	public static final int BONE_WEIGHT_OFFSET = MAX_BONES_PER_VERTEX;
	
	private int vertexCount;
	private int indexCount;
	
	/** A bounding box enclosing this mesh **/
	protected AABB boundingBox;
	
	protected int buffers[] = new int[3];
	
	/** Dummy Mesh constructor **/
	public Mesh() {
		
	}
	
	public Mesh(float[] vertexData, short[] drawOrder, int vertexCount) {
		this(vertexData, drawOrder, vertexCount, TyrGL.GL_USE_VBO == 1);
	}
	
	public Mesh(float[] vertexData, short[] drawOrder, int vertexCount, boolean useVBO) {
		this.vertexData = vertexData;
		this.drawOrder = drawOrder;
		this.vertexCount = vertexCount;
		this.indexCount = drawOrder.length;
		
	    // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexData.length * OpenGLRenderer.BYTES_PER_FLOAT);
        
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(vertexData);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
      
        int stride = vertexData.length / vertexCount;
        
        boundingBox = AABB.createFromPoints(vertexData, stride);
        
        if (useVBO) {
        	createVBO();
        	usesVBO = true;
        }
	}

	public Mesh(FloatBuffer vertexBuffer, ShortBuffer drawListBuffer, int vertexCount, int indexCount, int stride, AABB boundingBox) {

		this.vertexBuffer = vertexBuffer;
		this.drawListBuffer = drawListBuffer;
		
		this.vertexCount = vertexCount;

        vertexBuffer.position(0);
        drawListBuffer.position(0);
        
        this.boundingBox = boundingBox;
        
        this.indexCount = indexCount;
        
        if (TyrGL.GL_USE_VBO == 1) {
        	createVBO();
        }
	}

	
	/** 
	 * Assign vertices to bones.
	 * The weights indicates how much the bone transformation will affect the vertices.
	 * The array needs to have the following format:
	 * numBones bone_index_1 bone_index_2 ...  weight_1 weight_2
	 * Whereas 
	 * numBones contains number of bones affecting this vertex
	 * bone_index_x is the index xth of the bone affecting this vertex (1 Byte)
	 * bone_weight_x is the xth weight of the bxth bone (Float, 4 Byte)
	 * For each vertex a total space for 4 bone indicies/weights must be provided
	 * So in total 4 * 8 = 32 Byte per vertex
	 */
	public void setVertexBones(float[] boneData) {
	    this.boneData = boneData;
		
		// initialize bone byte buffer for animation data
		
        ByteBuffer bb = ByteBuffer.allocateDirect(boneData.length * OpenGLRenderer.BYTES_PER_FLOAT);
        
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        
        boneBuffer = bb.asFloatBuffer();
        
        boneBuffer.put(boneData);
        
        boneBuffer.position(0);
        
        if (usesVBO) {
        	createBoneBuffer();
        }
      
	}
	
	public FloatBuffer getBoneBuffer() {
		return boneBuffer;
	}
	
	public short[] getDrawOrder() {
		return drawOrder;
	}
	
	public ShortBuffer getDrawOrderBuffer() {
		return drawListBuffer;
	}
	
	public float[] getVertexData() {
		return vertexData;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public int getIndexCount() {
		return indexCount;
	}
	
	public AABB getBoundingBox() {
		return boundingBox;
	}
	
	public void setVertexInfo(int index, float info) {
		vertexBuffer.put(index, info);
		vertexData[index] = info;
		
		if (usesVBO) {
			TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, buffers[0]);
			TyrGL.glBufferSubData(TyrGL.GL_ARRAY_BUFFER, 0, vertexData.length * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
			TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, 0);
		}
	}
	
	public int getVBOBuffer() {
		return buffers[0];
	}
	
	public int getIBOBuffer() {
		return buffers[1];
	}
	
	public int getBBuffer() {
		return buffers[2];
	}
	
	private void createVBO() {
		TyrGL.glGenBuffers(1, buffers, 0); // Get A Valid Name
		TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, buffers[0]); // Bind The Buffer
        // Load The Data
		TyrGL.glBufferData(TyrGL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer, TyrGL.GL_STATIC_DRAW);
		TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, 0);
		
        if (TyrGL.GL_USE_VBO == 1) {
        	TyrGL.glGenBuffers(1, buffers, 1);
			TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, buffers[1]); // Bind The Buffer
	        // Load The Data
	        TyrGL.glBufferData(TyrGL.GL_ELEMENT_ARRAY_BUFFER, indexCount * 2, drawListBuffer, TyrGL.GL_STATIC_DRAW);
        }
        
	}
	
	public FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}
	
	private void createBoneBuffer() {
		TyrGL.glGenBuffers(1, buffers, 2); // Get A Valid Name
		TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, buffers[2]); // Bind The Buffer
        // Load The Data
        TyrGL.glBufferData(TyrGL.GL_ARRAY_BUFFER, boneData.length * OpenGLRenderer.BYTES_PER_FLOAT, boneBuffer, TyrGL.GL_STATIC_DRAW);
	}
	
	public boolean isUsingVBO() {
		return usesVBO;
	}

}
