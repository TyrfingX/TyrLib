package com.tyrlib2.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * This class represents a mesh, containing the neccessary vertex data, etc.
 * @author Sascha
 *
 */

public class Mesh {
	protected FloatBuffer vertexBuffer;
	protected float[] vertexData;
	
	public Mesh(float[] vertexData) {
		this.vertexData = vertexData;
		
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

	}
}
